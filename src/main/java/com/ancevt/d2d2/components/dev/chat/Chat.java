/**
 * Copyright (C) 2024 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.components.dev.chat;

import com.ancevt.commons.fs.IsolatedDirectory;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.engine.lwjgl.LwjglEngine;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.components.TextInput;
import com.ancevt.d2d2.components.TextInputEvent;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Chat extends Container {

    private String lastText;
    private static final int INPUT_MAX_LENGTH = 100;
    private static final int ALPHA_TIME = 500;
    private int alphaTime = ALPHA_TIME;
    private final TextInput input;
    private final List<ChatMessage> messages;
    private final List<ChatMessage> displayedMessages;
    private final List<String> history;
    private float width;
    private float height;
    @Getter
    private int scroll;

    @Getter
    private int maxMessages = 2048;
    @Getter
    private int lastChatMessageId;
    private int historyIndex;
    @Getter
    private boolean inputEnabled;

    @Getter
    private final IsolatedDirectory getIsolatedDirectory;

    @Getter
    private boolean autoHide;

    public Chat(String dirInUserHome) {
        getIsolatedDirectory = new IsolatedDirectory(Path.of(System.getProperty("user.home")).resolve(dirInUserHome));

        input = new TextInput();
        messages = new CopyOnWriteArrayList<>();
        displayedMessages = new CopyOnWriteArrayList<>();
        history = new ArrayList<>();

        width = D2D2.stage().getWidth() / 2.0f;
        height = D2D2.stage().getHeight() / 3.0f;

        input.setWidth(20);
        input.addEventListener(TextInputEvent.ENTER, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_CHANGE, this::textInputEvent);
        input.addEventListener(TextInputEvent.KEY_DOWN, this::textInputEvent);
        input.setComponentFocusRectVisibleEnabled(false);
        input.setFocusRectVisibleEnabled(false);

        loadHistory();

        redraw();
    }

    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
        redraw();
    }

    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;

        if (!autoHide) {
            setAlpha(1.0f);
        }
    }

    public List<ChatMessage> getMessages() {
        return List.copyOf(messages);
    }

    public void setInputEnabled(boolean b) {
        if (this.inputEnabled == b) return;
        this.inputEnabled = b;

        D2D2.stage().removeEventListener(this, InputEvent.KEY_DOWN);

        if (inputEnabled) {
            D2D2.stage().addEventListener(InputEvent.KEY_DOWN, event -> {
                InputEvent inputEvent = (InputEvent) event;
                switch (inputEvent.getKeyCode()) {
                    case KeyCode.PAGE_UP -> {
                        setScroll(getScroll() - 10);
                    }

                    case KeyCode.PAGE_DOWN -> {
                        setScroll(getScroll() + 10);
                    }

                    case KeyCode.F6 -> {
                        if (!isInputOpened())
                            openInput();
                        else
                            closeInput();
                    }
                }
            });
        }
    }

    public void setWidth(float width) {
        this.width = width;
        redraw();
    }

    public void setHeight(float height) {
        this.height = height;
        scrollToEnd();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        scrollToEnd();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    private void redraw() {
        input.setXY(0, height - input.getHeight());

        input.setMaxSize(D2D2.stage().getWidth(), 16);

        displayedMessages.forEach(DisplayObject::removeFromParent);

        int y = 0;

        for (int i = scroll; i < messages.size() && i - scroll < getMessageCountOnDisplay(); i++) {
            ChatMessage chatMessage = messages.get(i);
            displayedMessages.add(chatMessage);

            add(chatMessage, 0, y);
            y += (int) chatMessage.getHeight();
        }
    }

    private int getMessageCountOnDisplay() {
        return (int) ((getHeight() - input.getHeight()) / ChatMessage.DEFAULT_HEIGHT) - 1;
    }

    public void setScroll(int scroll) {
        setAlpha(1.0f);
        if (this.scroll == scroll) return;

        if (scroll > messages.size() - getMessageCountOnDisplay()) {
            scroll = messages.size() - getMessageCountOnDisplay();
        }
        if (scroll < 0) scroll = 0;

        this.scroll = scroll;

        redraw();
    }

    public void addPlayerMessage(int id,
                                 int playerId,
                                 String playerName,
                                 Color playerColor,
                                 String messageText,
                                 Color textColor) {

        if (messageText.length() > 70) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < messageText.length(); i += 70) {
                String part = messageText.substring(i, Math.min(i + 70, messageText.length()));
                addPlayerMessage(id, playerId, playerName, playerColor, part, textColor);
            }
            return;
        }

        if (messageText.contains("\n")) {
            messageText.lines().forEach(line ->
                addPlayerMessage(id, playerId, playerName, playerColor, line, textColor));
            return;
        }

        addMessage(new ChatMessage(id, playerId, playerName, playerColor, messageText, textColor));
        if (id != 0) lastChatMessageId = id;
        redraw();
    }

    public void addMessage(String messageText, Color textColor) {
        setAlpha(1.0f);
        alphaTime = ALPHA_TIME;
        if (messageText.length() > 100) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < messageText.length(); i += 100) {
                String part = messageText.substring(i, Math.min(i + 100, messageText.length()));
                addMessage(part, textColor);
            }
            return;
        }

        if (messageText.contains("\n")) {
            messageText.lines().forEach(line -> addMessage(line, textColor));
            return;
        }

        addMessage(new ChatMessage(0, messageText, textColor));
        redraw();
    }

    public void print(String messageText) {
        print(messageText, Color.LIGHT_GRAY);
    }

    public void print(String messageText, Color color) {
        if (messageText.contains("\n")) {
            messageText.lines().forEach(line -> print(line, color));
        } else {
            addMessage(messageText, color);
        }
    }

    public void addMessage(String messageText) {
        addMessage(messageText, Color.WHITE);
    }

    public void addMessage(ChatMessage chatMessage) {
        setAlpha(1.0f);
        alphaTime = ALPHA_TIME;
        messages.add(chatMessage);

        if (messages.size() > maxMessages) {
            messages.removeAll(messages.subList(0, maxMessages / 3));
        }

        scrollToEnd();
        redraw();

        if (log.isDebugEnabled()) {
            if (chatMessage.isFromPlayer()) {
                log.debug("{}({}): {}", chatMessage.getPlayerName(), chatMessage.getPlayerId(), chatMessage.getText());
            } else {
                log.debug("{}", chatMessage.getText());
            }
        }
    }

    private void scrollToEnd() {
        setScroll(Integer.MAX_VALUE);
    }

    public void openInput() {
        Timer.setTimeout(t -> {
            setAlpha(1.0f);
            alphaTime = ALPHA_TIME;
            add(input);
            input.focus();
            dispatchEvent(ChatEvent.builder()
                .type(ChatEvent.CHAT_INPUT_OPEN)
                .build());
        }, 100);
    }

    public void closeInput() {
        remove(input);

        dispatchEvent(ChatEvent.builder()
            .type(ChatEvent.CHAT_INPUT_CLOSE)
            .build());
    }

    public boolean isInputOpened() {
        return input.hasParent();
    }

    public void clear() {
        messages.clear();
        redraw();
    }

    public void textInputEvent(Event event) {
        if (event instanceof TextInputEvent uiTextInputEvent) {
            switch (event.getType()) {

                case TextInputEvent.TEXT_CHANGE -> {
                    setAlpha(1.0f);
                    alphaTime = ALPHA_TIME;
                    String text = uiTextInputEvent.getText();
                    int length = text.length();
                    if (length > INPUT_MAX_LENGTH) {
                        input.setText(text.substring(0, INPUT_MAX_LENGTH));
                        return;
                    }
                    int w = text.length() * ComponentFont.getBitmapFontMiddle().getCharInfo('0').width();
                    input.setWidth(w + 20);
                }

                case TextInputEvent.ENTER -> {
                    String text = uiTextInputEvent.getText();
                    if (!text.isBlank()) {
                        dispatchEvent(ChatEvent.builder()
                            .type(ChatEvent.CHAT_TEXT_ENTER)
                            .text(text)
                            .build());
                        if (!Objects.equals(text, lastText)) {
                            history.add(text);
                            lastText = text;
                            historyIndex = history.size();
                        }
                    }
                    input.clear();
                    closeInput();
                }

                case TextInputEvent.KEY_DOWN -> {
                    switch (uiTextInputEvent.getKeyCode()) {
                        case KeyCode.UP -> {
                            if (historyIndex == history.size()) {
                                //history.add(input.getText());
                            }
                            historyIndex--;
                            restoreHistory();
                        }
                        case KeyCode.DOWN -> {
                            historyIndex++;
                            restoreHistory();
                        }
                        case KeyCode.ESCAPE -> {
                            closeInput();
                        }
                    }
                }
            }
        }
    }

    private void restoreHistory() {
        if (historyIndex > history.size() - 1) {
            input.moveCaretToEnd();
            historyIndex = history.size() - 1;
        }

        if (historyIndex < 0) {
            historyIndex = 0;
            return;
        }

        input.setText(history.get(historyIndex));
        input.moveCaretToEnd();
    }

    public void saveHistory() {
        String toSave = history.stream().reduce("", (s1, s2) -> s1.concat('\n' + s2));
        if (toSave.isEmpty()) return;
        toSave = toSave.substring(1);
        if (!toSave.isBlank()) {
            getIsolatedDirectory.writeString(toSave, "chatinputhistory");
        }
    }

    private void loadHistory() {
        getIsolatedDirectory.checkExists("chatinputhistory").ifPresent(strPath -> {
            String historyString = getIsolatedDirectory.readString(strPath);
            history.addAll(historyString.lines().toList());
            historyIndex = history.size();
        });
    }

    @Override
    public void onExitFrame() {
        super.onExitFrame();

        if (autoHide) {
            alphaTime--;
            if (alphaTime <= 0) {
                setAlpha(0.25f);
                alphaTime = 0;
            }
        }
    }

    public static void main(String[] args) {
        Stage stage = D2D2.directInit(new LwjglEngine(800, 600, "(floating)"));
        ComponentAssets.init();

        stage.setBackgroundColor(Color.of(0x223344));

        AtomicInteger idCounter = new AtomicInteger(1);

        Chat chat = new Chat(".d2d2-just-a-chat");
        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            if (event instanceof ChatEvent chatEvent) {
                String text = chatEvent.getText();
                idCounter.set(idCounter.get() + 1);
                chat.addPlayerMessage(idCounter.get(), 1, "Ancevt", Color.YELLOW, text, Color.WHITE);
            }
        });

        stage.add(chat, 10, 10);

        for (int i = 0; i < 10; i++) {
            idCounter.set(idCounter.get() + 1);
            chat.addPlayerMessage(idCounter.get(), 1, "Ancevt", Color.YELLOW, "Hello, i'm Ancevt" + i, Color.WHITE);
        }

        stage.addEventListener(InputEvent.KEY_DOWN, event -> {
            InputEvent inputEvent = (InputEvent) event;
            switch (inputEvent.getKeyCode()) {
                case KeyCode.PAGE_UP -> {
                    chat.setScroll(chat.getScroll() - 10);
                }

                case KeyCode.PAGE_DOWN -> {
                    chat.setScroll(chat.getScroll() + 10);
                }

                case KeyCode.F6 -> {
                    if (!chat.isInputOpened())
                        chat.openInput();
                    else
                        chat.closeInput();
                }

                case KeyCode.T -> {
                    if (!chat.isInputOpened())
                        chat.openInput();
                }
            }
        });
        D2D2.loop();
    }

}
