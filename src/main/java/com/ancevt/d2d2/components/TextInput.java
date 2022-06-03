/**
 * Copyright (C) 2022 the original author or authors.
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
package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Clipboard;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.interactive.InteractiveManager;
import org.jetbrains.annotations.NotNull;

public class TextInput extends Component {

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        InteractiveManager.getInstance().setTabbingEnabled(true);

        stage.setBackgroundColor(Color.GRAY);

        for(int i = 0; i < 10; i ++) {
            TextInput textInput = new TextInput();
            textInput.setText("" + Math.random());
            stage.add(textInput, 50, 50 + i * 35);

            if(i == 5) {
                textInput.setEnabled(false);
            }
        }

        stage.add(new BitmapTextEx("Hello world"), 400, 100);
        D2D2.loop();
    }

    private static final Color BACKGROUND_COLOR = Component.BACKGROUND_COLOR;
    private static final Color SELECTION_COLOR = Color.DARK_GRAY;
    private static final float BACKGROUND_ALPHA = Component.PANEL_BG_ALPHA;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 30;

    private final PlainRect background;
    private final PlainRect selection;
    private final BitmapTextEx bitmapText;
    private final Caret caret;
    private boolean selecting;
    private int selectionFromIndex;
    private int selectionToIndex;
    private int selectionStartIndex;
    private String text;

    public TextInput() {
        background = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, BACKGROUND_COLOR);
        selection = new PlainRect(0, DEFAULT_HEIGHT - 8, SELECTION_COLOR);
        bitmapText = new BitmapTextEx();
        bitmapText.setShadowEnabled(false);

        background.setAlpha(BACKGROUND_ALPHA);

        add(background);
        // add(selection, uiText.getX(), 4); // selection is completely not implemented yet
        add(bitmapText);

        caret = new Caret(this);
        caret.setXY(bitmapText.getX(), 4);

        setText("");

        align();

        addEventListener(TextInput.class, InteractiveEvent.DOWN, this::this_down);
        addEventListener(TextInput.class, InteractiveEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(TextInput.class, InteractiveEvent.FOCUS_OUT, this::this_focusOut);
        addEventListener(TextInput.class, InteractiveEvent.KEY_DOWN, this::this_keyDown);
        addEventListener(TextInput.class, InteractiveEvent.KEY_UP, this::this_keyUp);
        addEventListener(TextInput.class, InteractiveEvent.KEY_TYPE, this::this_keyType);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setEnabled(true);
    }

    private void this_keyType(Event event) {
        var e = (InteractiveEvent) event;
        String keyType = e.getKeyType();
        if (!bitmapText.getBitmapFont().isCharSupported(keyType.charAt(0))) return;
        if (text.length() * bitmapText.getCharWidth() < getWidth() - 10) insertText(keyType);
    }

    private void this_keyUp(Event event) {

    }

    private void this_keyDown(Event event) {
        var e = (InteractiveEvent) event;
        switch (e.getKeyCode()) {

            case KeyCode.RIGHT -> {
                setCaretPosition(getCaretPosition() + 1);
                if (selecting) {
                    if (getCaretPosition() > selectionStartIndex) {
                        setSelection(selectionStartIndex, getCaretPosition());
                    } else {
                        setSelection(getCaretPosition(), selectionStartIndex);
                    }
                }

            }
            case KeyCode.LEFT -> {
                setCaretPosition(getCaretPosition() - 1);

                if (selecting) {
                    if (getCaretPosition() < selectionStartIndex) {
                        setSelection(selectionStartIndex, getCaretPosition());
                    } else {
                        setSelection(getCaretPosition(), selectionStartIndex);
                    }
                }
            }
            case KeyCode.BACKSPACE -> {
                if (e.isControl()) removeWord();
                else removeChar();
            }
            case KeyCode.DELETE -> {
                int index = getCaretPosition();
                if (index < text.length()) {
                    setText(text.substring(0, index) + text.substring(index + 1));
                }
            }
            case KeyCode.HOME -> setCaretPosition(0);

            case KeyCode.END -> setCaretPosition(text.length());

            case KeyCode.ENTER -> {
                dispatchEvent(TextInputEvent.builder()
                                .type(TextInputEvent.ENTER)
                                .text(getText())
                                .keyCode(e.getKeyCode())
                                .build());

                setCaretPosition(Integer.MAX_VALUE);
            }
        }

        if (e.isControl()) {
            switch (e.getKeyChar()) {
                case 'X' -> {
                    Clipboard.set(getText());
                    setText("");
                    setCaretPosition(getCaretPosition());
                }
                case 'V' -> {
                    insertText(Clipboard.get());
                    setCaretPosition(getCaretPosition() + Clipboard.get().length());
                }
                case 'C' -> {
                    Clipboard.set(getText());
                }
                case 'W' -> {
                    removeWord();
                }
            }
        }

        dispatchEvent(TextInputEvent.builder()
                .type(TextInputEvent.KEY_DOWN)
                .text(getText())
                .keyCode(e.getKeyCode())
                .build());

    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled == isEnabled()) return;

        super.setEnabled(enabled);
        bitmapText.setColor(enabled ? Component.TEXT_COLOR : Component.TEXT_COLOR_DISABLED);
    }

    public void setBackgroundColor(Color backgroundColor) {
        background.setColor(backgroundColor);
    }

    public Color getBackgroundColor() {
        return background.getColor();
    }

    private void this_focusIn(Event event) {
        setCaretPosition(Integer.MAX_VALUE);
        focus();
    }

    private void this_focusOut(Event event) {
        caret.removeFromParent();
    }

    private void this_down(Event event) {
        InteractiveEvent e = (InteractiveEvent) event;

        float x = e.getX();
        float c = bitmapText.getCharWidth();
        float s = bitmapText.getAbsoluteScaleX();

        setCaretPosition((int) ((x / c) / s));
    }

    public void setTextColor(Color textColor) {
        bitmapText.setColor(textColor);
    }

    public Color getTextColor() {
        return bitmapText.getColor();
    }

    public void setSelection(int fromIndex, int toIndex) {
        if (fromIndex < toIndex) {
            int temp = fromIndex;
            fromIndex = toIndex;
            toIndex = temp;
        }

        this.selectionFromIndex = fromIndex;
        this.selectionToIndex = toIndex;

        redrawSelection();
    }

    public void resetSelection() {
        selectionStartIndex = 0;
        selectionFromIndex = 0;
        selectionToIndex = 0;
        redrawSelection();
    }

    private void redrawSelection() {
        selection.setX(bitmapText.getX() + selectionFromIndex * bitmapText.getCharWidth());
        selection.setWidth((selectionToIndex - selectionFromIndex) * bitmapText.getCharWidth());
    }

    public void setText(@NotNull String text) {
        this.text = text;
        this.bitmapText.setText(text);
        if (getCaretPosition() > text.length()) {
            setCaretPosition(Integer.MAX_VALUE);
        }

        dispatchEvent(TextInputEvent.builder()
                .type(TextInputEvent.TEXT_CHANGE)
                .text(getText())
                .build());
    }

    public String getText() {
        return text;
    }

    public void setCaretPosition(int index) {
        index = fixIndex(index);
        caret.setX(bitmapText.getX() + (index * bitmapText.getCharWidth()) + 1);
        caret.setAlpha(1f);
    }

    private int fixIndex(int index) {
        int len = text.length();

        if (index > len) {
            index = len;
        } else if (index < 0) {
            index = 0;
        }
        return index;
    }

    public int getCaretPosition() {
        return (int) (caret.getX() / bitmapText.getCharWidth());
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        background.setWidth(width);
        align();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        background.setHeight(height);
        align();
    }

    @Override
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

    @Override
    public void dispose() {
        super.dispose();
        removeEventListener(TextInput.class, InteractiveEvent.DOWN);
        removeEventListener(TextInput.class, InteractiveEvent.FOCUS_IN);
        removeEventListener(TextInput.class, InteractiveEvent.FOCUS_OUT);
        removeEventListener(TextInput.class, InteractiveEvent.KEY_DOWN);
        removeEventListener(TextInput.class, InteractiveEvent.KEY_UP);
        removeEventListener(TextInput.class, InteractiveEvent.KEY_TYPE);
        removeFromParent();
    }

    @Override
    public boolean isDisposed() {
        return super.isDisposed();
    }

    private void align() {
        int alignTop = 10;
        int alignLeft = 5;

        bitmapText.setXY(alignLeft, alignTop);
        bitmapText.setWidth(getWidth() - (alignLeft * 2));
        bitmapText.setHeight(getHeight() - (alignTop * 2));
    }

    public void focus() {
        super.focus();
        add(caret);
    }

    private void insertText(String textToInsert) {
        if (textToInsert.length() == 0) {
            setText(textToInsert);
        }
        int index = getCaretPosition();
        setText(text.substring(0, index) + textToInsert + text.substring(index));
        setCaretPosition(getCaretPosition() + textToInsert.length());

        while (text.length() * bitmapText.getCharWidth() > getWidth() - 10) {
            removeChar();
        }
    }

    private void removeChar() {
        int index = getCaretPosition();
        if (index > 0) {
            String newText = text.substring(0, index - 1) + text.substring(index);
            setCaretPosition(getCaretPosition() - 1);
            setText(newText);
        }
    }

    private void removeWord() {
        if (getCaretPosition() == 0) return;

        if (getCharUnderCaret() == ' ') {
            removeChar();
        } else
            while (getCaretPosition() > 0 && getCharUnderCaret() != ' ') {
                removeChar();
            }
    }

    private char getCharUnderCaret() {
        return text.charAt(getCaretPosition() - 1);
    }

    public void clear() {
        setText("");
    }

    public void moveCaretToEnd() {
        setCaretPosition(Integer.MAX_VALUE);
    }

    @Override
    void update() {

    }

    private static class Caret extends PlainRect {

        public static final int BLINK_DELAY = 25;

        private int blinkCounter = BLINK_DELAY;
        private TextInput uiTextInput;

        public Caret(TextInput uiTextInput) {
            super(1, DEFAULT_HEIGHT - 8, Color.WHITE);
            this.uiTextInput = uiTextInput;
        }

        @Override
        public void onEachFrame() {
            if (uiTextInput.isEnabled()) {
                blinkCounter--;
                if (blinkCounter <= 0) {
                    blinkCounter = BLINK_DELAY;
                    setAlpha(getAlpha() == 1f ? 0f : 1f);
                }
            }
        }
    }

}
