/**
 * Copyright (C) 2025 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2.components.dev;

import com.ancevt.commons.fs.IsolatedDirectory;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.components.TextInput;
import com.ancevt.d2d2.components.TextInputEvent;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.scene.AbstractNode;
import com.ancevt.d2d2.scene.BasicGroup;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat extends BasicGroup {

    private String lastText;
    private static final int INPUT_MAX_LENGTH = 100;
    private static final int ALPHA_TIME = 500;
    private int alphaTime = ALPHA_TIME;

    @Getter
    protected final TextInput textInput;
    private final List<ChatMessage> messages;
    private final List<ChatMessage> displayedMessages;
    private final List<String> history;
    private float width;
    private float height;
    @Getter
    private int scroll;

    @Getter
    private int maxMessages = 2048;
    private int historyIndex;
    @Getter
    private boolean inputEnabled;

    @Getter
    private final IsolatedDirectory isolatedDirectory;

    @Getter
    private boolean autoHide;

    @Getter
    private boolean multicolorEnabled;

    public Chat(String dirInUserHome) {
        isolatedDirectory = IsolatedDirectory.newIsolatedDirectoryInApplicationData(Path.of(dirInUserHome));

        textInput = new TextInput();
        messages = new CopyOnWriteArrayList<>();
        displayedMessages = new CopyOnWriteArrayList<>();
        history = new ArrayList<>();

        width = D2D2.getStage().getWidth() / 2.0f;
        height = D2D2.getStage().getHeight() / 3.0f;

        textInput.setWidth(20);
        textInput.addEventListener(TextInputEvent.Enter.class, this::textInput_enter);
        textInput.addEventListener(TextInputEvent.TextChange.class, this::textInput_textChange);
        textInput.addEventListener(TextInputEvent.TextInputKeyDown.class, this::textInput_keyDown);
        textInput.setComponentFocusRectVisibleEnabled(false);
        textInput.setFocusRectVisibleEnabled(false);

        loadInputHistory();

        redraw();

        stage.onPostFrame(e -> postFrame());
    }

    private void textInput_keyDown(TextInputEvent.TextInputKeyDown e) {
        switch (e.getKeyCode()) {
            case KeyCode.UP -> {
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

    private void textInput_enter(TextInputEvent.Enter e) {
        String text = e.getText();
        if (!text.isBlank()) {
            dispatchEvent(ChatEvent.TextEnter.create(text));
            if (!Objects.equals(text, lastText)) {
                history.add(text);
                lastText = text;
            }
            historyIndex = history.size();
        }

        textInput.clear();
        //closeInput();
    }

    private void textInput_textChange(TextInputEvent.TextChange e) {
        setAlpha(1.0f);
        alphaTime = ALPHA_TIME;
        String text = e.getText();
        int length = text.length();
        if (length > INPUT_MAX_LENGTH) {
            textInput.setText(text.substring(0, INPUT_MAX_LENGTH));
            return;
        }
        int w = text.length() * ComponentFont.getFontMiddle().getCharInfo('0').width();
        textInput.setWidth(w + 20);
    }

    public void setMulticolorEnabled(boolean multicolorEnabled) {
        this.multicolorEnabled = multicolorEnabled;
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

        D2D2.getStage().removeEventListener(this, InputEvent.KeyDown.class);

        if (inputEnabled) {
            D2D2.getStage().addEventListener(InputEvent.KeyDown.class, e -> {
                switch (e.getKeyCode()) {
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
        textInput.setPosition(0, height);

        textInput.setMaxSize(D2D2.getStage().getWidth(), 16);

        displayedMessages.forEach(AbstractNode::removeFromParent);

        int y = 0;

        for (int i = scroll; i < messages.size() && i - scroll < getMessageCountOnDisplay(); i++) {
            ChatMessage chatMessage = messages.get(i);
            chatMessage.setMulticolorEnabled(multicolorEnabled);
            displayedMessages.add(chatMessage);

            chatMessage.bitmapText.setWidth(getWidth());
            chatMessage.bitmapText.setHeight(20);

            addChild(chatMessage, 0, y);
            y += 16;
        }
    }

    private int getMessageCountOnDisplay() {
        return (int) (getHeight() / ChatMessage.DEFAULT_HEIGHT);
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

    public void println(String messageText) {
        println(messageText, Color.LIGHT_GRAY);
    }

    public void println(String messageText, Color color) {
        if (messageText.contains("\n")) {
            messageText.lines().forEach(line -> println(line, color));
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
    }

    private void scrollToEnd() {
        setScroll(Integer.MAX_VALUE);
    }

    public void openInput() {
        Timer.setTimeout(100, t -> {
            setAlpha(1.0f);
            alphaTime = ALPHA_TIME;
            addChild(textInput);
            textInput.focus();
            dispatchEvent(ChatEvent.InputOpen.create());
        });
    }

    public void closeInput() {
        removeChild(textInput);
        dispatchEvent(ChatEvent.InputClose.create());
    }

    public boolean isInputOpened() {
        return textInput.hasParent();
    }

    public void clear() {
        messages.clear();
        redraw();
    }

    private void restoreHistory() {
        if (historyIndex > history.size() - 1) {
            textInput.moveCaretToEnd();
            historyIndex = history.size() - 1;
        }

        if (historyIndex < 0) {
            historyIndex = 0;
            return;
        }

        textInput.setText(history.get(historyIndex));
        textInput.moveCaretToEnd();
    }

    public void saveInputHistory() {
        String toSave = history.stream().reduce("", (s1, s2) -> s1.concat('\n' + s2));
        if (toSave.isEmpty()) return;
        toSave = toSave.substring(1);
        if (!toSave.isBlank()) {
            isolatedDirectory.writeString(toSave, "inputhistory");
        }
    }

    private void loadInputHistory() {
        isolatedDirectory.checkExists("inputhistory").ifPresent(strPath -> {
            String historyString = isolatedDirectory.readString(strPath);
            history.addAll(historyString.lines().toList());
            historyIndex = history.size();
        });
    }

    public void postFrame() {
        if (autoHide) {
            alphaTime--;
            if (alphaTime <= 0) {
                setAlpha(0.25f);
                alphaTime = 0;
            }
        }
    }
}
