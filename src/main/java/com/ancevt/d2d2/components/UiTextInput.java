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
import com.ancevt.d2d2.event.FocusEvent;
import com.ancevt.d2d2.event.InteractiveButtonEvent;
import com.ancevt.d2d2.input.Clipboard;
import com.ancevt.d2d2.input.KeyCode;
import org.jetbrains.annotations.NotNull;

public class UiTextInput extends Component {

    private boolean enabled;

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        stage.setBackgroundColor(Color.GRAY);

        UiTextInput uiTextInput = new UiTextInput();
        stage.add(uiTextInput, 100, 100);
        uiTextInput.setText("AAAAAAAAAAAAAAAAAAA");

        UiTextInput uiTextInput1 = new UiTextInput();
        stage.add(uiTextInput1, 100, 140);
        uiTextInput1.setText("AAAAAAAAAAAAAAAAAAA");

        stage.add(new UiText("Hello world"), 400, 100);

        UiTextInputProcessor.setEnabled(true);
        D2D2.loop();
    }

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color SELECTION_COLOR = Color.DARK_GRAY;
    private static final float BACKGROUND_ALPHA = 0.75f;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 30;

    private final PlainRect background;
    private final PlainRect selection;
    private final UiText uiText;
    private final Caret caret;
    private boolean focused;
    private boolean selecting;
    private int selectionFromIndex;
    private int selectionToIndex;
    private int selectionStartIndex;
    private String text;

    public UiTextInput() {
        enabled = true;
        background = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, BACKGROUND_COLOR);
        selection = new PlainRect(0, DEFAULT_HEIGHT - 8, SELECTION_COLOR);
        uiText = new UiText();
        uiText.setShadowEnabled(false);

        background.setAlpha(BACKGROUND_ALPHA);

        add(background);
        // add(selection, uiText.getX(), 4); // selection is completely not implemented yet
        add(uiText);

        caret = new Caret(this);
        caret.setXY(uiText.getX(), 4);

        setText("");

        align();

        addEventListener(UiTextInput.class, Event.ADD_TO_STAGE, this::this_addToStage);
        addEventListener(UiTextInput.class, Event.REMOVE_FROM_STAGE, this::this_removeFromStage);
        addEventListener(UiTextInput.class, InteractiveButtonEvent.DOWN, this::this_down);
        addEventListener(UiTextInput.class, FocusEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(UiTextInput.class, FocusEvent.FOCUS_OUT, this::this_focusOut);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_focusIn(Event event) {
        UiTextInputProcessor.INSTANCE.focus(this);
    }

    private void this_focusOut(Event event) {
        focusLost();
    }

    private void this_down(Event event) {
        if (!enabled) return;

        InteractiveButtonEvent e = (InteractiveButtonEvent) event;
        UiTextInputProcessor.INSTANCE.focus(this);

        float x = e.getX();
        float c = uiText.getCharWidth();
        float s = uiText.getAbsoluteScaleX();

        setCaretPosition((int) ((x / c) / s));
    }

    private void this_addToStage(Event event) {
        UiTextInputProcessor.INSTANCE.addTextInput(this);
        UiTextInputProcessor.INSTANCE.focus(this);
    }

    private void this_removeFromStage(Event event) {
        UiTextInputProcessor.INSTANCE.removeTextInput(this);
        if (isFocused()) UiTextInputProcessor.INSTANCE.resetFocus();
    }

    public void setColor(Color color) {
        uiText.setColor(color);
    }

    public Color getColor() {
        return uiText.getColor();
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
        selection.setX(uiText.getX() + selectionFromIndex * uiText.getCharWidth());
        selection.setWidth((selectionToIndex - selectionFromIndex) * uiText.getCharWidth());
    }

    public void setText(@NotNull String text) {
        this.text = text;
        this.uiText.setText(text);
        if (getCaretPosition() > text.length()) {
            setCaretPosition(Integer.MAX_VALUE);
        }

        dispatchEvent(UiTextInputEvent.builder()
                .type(UiTextInputEvent.TEXT_CHANGE)
                .text(getText())
                .build());
    }

    public String getText() {
        return text;
    }

    public void setCaretPosition(int index) {
        index = fixIndex(index);
        caret.setX(uiText.getX() + (index * uiText.getCharWidth()) + 1);
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
        return (int) (caret.getX() / uiText.getCharWidth());
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
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void dispose() {
        super.dispose();
        removeEventListener(UiTextInput.class, Event.ADD_TO_STAGE);
        removeEventListener(UiTextInput.class, Event.REMOVE_FROM_STAGE);
        removeEventListener(UiTextInput.class, InteractiveButtonEvent.DOWN);
        removeFromParent();
        UiTextInputProcessor.INSTANCE.removeTextInput(this);
    }

    @Override
    public boolean isDisposed() {
        return super.isDisposed();
    }

    private void align() {
        int alignTop = 10;
        int alignLeft = 5;

        uiText.setXY(alignLeft, alignTop);
        uiText.setWidth(getWidth() - (alignLeft * 2));
        uiText.setHeight(getHeight() - (alignTop * 2));

        System.out.println(uiText.getWidth() + ", " + uiText.getHeight());
    }

    public void key(int keyCode, char keyChar, boolean control, boolean shift, boolean alt, boolean down) {
        if (!enabled) return;

        if (down) {
            if (KeyCode.isShift(keyCode)) {
                selecting = true;
                selectionStartIndex = getCaretPosition();
            } else if (!selecting) {
                resetSelection();
            }

            keyDown(keyCode, keyChar, control, shift);
        } else {
            if (KeyCode.isShift(keyCode)) {
                selecting = false;
            }
        }
    }

    private void keyDown(int keyCode, char keyChar, boolean control, boolean shift) {
        if (!enabled) return;

        switch (keyCode) {

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
                if (control) removeWord();
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

            case KeyCode.ENTER -> dispatchEvent(
                    UiTextInputEvent.builder()
                            .type(UiTextInputEvent.TEXT_ENTER)
                            .text(getText())
                            .keyCode(keyCode)
                            .build());
        }

        if (control) {
            switch (keyChar) {
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

        dispatchEvent(UiTextInputEvent.builder()
                .type(UiTextInputEvent.TEXT_INPUT_KEY_DOWN)
                .text(getText())
                .keyCode(keyCode)
                .build());
    }

    public void keyType(int codepoint, String keyType) {
        if (!uiText.getBitmapFont().isCharSupported(keyType.charAt(0))) return;
        if (text.length() * uiText.getCharWidth() < getWidth() - 10) insertText(keyType);
    }

    public void focus() {
        this.focused = true;
        add(caret);
    }

    void focusLost() {
        focused = false;
        caret.removeFromParent();
    }

    public boolean isFocused() {
        return focused;
    }

    private void insertText(String textToInsert) {
        if (textToInsert.length() == 0) {
            setText(textToInsert);
        }
        int index = getCaretPosition();
        setText(text.substring(0, index) + textToInsert + text.substring(index));
        setCaretPosition(getCaretPosition() + textToInsert.length());

        while (text.length() * uiText.getCharWidth() > getWidth() - 10) {
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

    public void requestFocus() {
        UiTextInputProcessor.INSTANCE.focus(this);
    }

    private static class Caret extends PlainRect {

        public static final int BLINK_DELAY = 25;

        private int blinkCounter = BLINK_DELAY;
        private UiTextInput uiTextInput;

        public Caret(UiTextInput uiTextInput) {
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































