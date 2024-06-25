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
package com.ancevt.d2d2.components;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.interactive.Combined9Sprites;
import com.ancevt.d2d2.display.shape.RectangleShape;
import com.ancevt.d2d2.display.text.Font;
import com.ancevt.d2d2.display.text.Text;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Clipboard;
import com.ancevt.d2d2.input.KeyCode;

public class TextInput extends Component {

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 30;


    private Color colorBackground = Component.BACKGROUND_COLOR;
    private Color colorSelection = Color.DARK_GRAY;
    private Color colorFocusRect = Color.of(0x515256);
    private float backgroundAlpha = Component.PANEL_BG_ALPHA;

    private final RectangleShape bg;
    private final RectangleShape selection;
    private final Text text;
    private final Combined9Sprites focusRect;
    private final Caret caret;
    private boolean selecting;
    private int selectionFromIndex;
    private int selectionToIndex;
    private int selectionStartIndex;
    private String textString;

    private Padding padding = new Padding(10, 0, 10, 0);

    public TextInput() {
        bg = new RectangleShape(DEFAULT_WIDTH, DEFAULT_HEIGHT, colorBackground);
        selection = new RectangleShape(0, DEFAULT_HEIGHT - 8, colorSelection);
        text = new Text();
        text.setAutosize(true);
        text.setFont(ComponentFont.getFontMiddle());

        bg.setAlpha(backgroundAlpha);

        addChild(bg);
        // add(selection, uiText.getX(), 4); // selection is completely not implemented yet
        addChild(text);

        caret = new Caret(this);
        caret.setXY(text.getX(), 4);

        focusRect = new Combined9Sprites(new String[]{
            ComponentAssets.RECT_BORDER_9_SIDE_TOP_LEFT,
            ComponentAssets.RECT_BORDER_9_SIDE_TOP,
            ComponentAssets.RECT_BORDER_9_SIDE_TOP_RIGHT,
            ComponentAssets.RECT_BORDER_9_SIDE_LEFT,
            ComponentAssets.RECT_BORDER_9_SIDE_CENTER,
            ComponentAssets.RECT_BORDER_9_SIDE_RIGHT,
            ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_LEFT,
            ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM,
            ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_RIGHT
        });
        focusRect.setColor(colorFocusRect);
        focusRect.setVisible(false);

        setText("");

        addEventListener(TextInput.class, InteractiveEvent.DOWN, this::this_down);
        addEventListener(TextInput.class, InteractiveEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(TextInput.class, InteractiveEvent.FOCUS_OUT, this::this_focusOut);
        addEventListener(TextInput.class, InteractiveEvent.KEY_DOWN, this::this_keyDown);
        addEventListener(TextInput.class, InteractiveEvent.KEY_UP, this::this_keyUp);
        addEventListener(TextInput.class, InteractiveEvent.KEY_TYPE, this::this_keyType);
        addEventListener(TextInput.class, InteractiveEvent.HOVER, this::this_hover);
        addEventListener(TextInput.class, InteractiveEvent.OUT, this::this_out);

        addEventListener(TextInput.class, Event.RESIZE, this::this_resize);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setEnabled(true);
    }

    public void setBitmapFont(Font font) {
        text.setFont(font);
    }

    public Font getBitmapFont() {
        return text.getFont();
    }

    private void this_hover(Event event) {
        if (isEnabled()) Cursor.switchToText();
    }

    private void this_out(Event event) {
        Cursor.switchToIdle();
    }

    @Override
    public void setPadding(Padding padding) {
        this.padding = padding;
        this_resize(null);
    }

    @Override
    public Padding getPadding() {
        return padding;
    }

    private void this_resize(Event event) {
        caret.setHeight(getHeight() - getHeight() / 3f);
        caret.setY((getHeight() - caret.getHeight()) / 2);
        text.setXY(padding.getLeft(), (getHeight() - text.getCharHeight()) / 2 + 2);
        text.setWidth(getWidth() - padding.getLeft() - padding.getRight());
        focusRect.setSize(getWidth(), getHeight());
    }

    public void setFocusRectVisibleEnabled(boolean focusRectEnabled) {
        if (focusRectEnabled) {
            addChild(focusRect);
        } else {
            focusRect.removeFromParent();
        }
    }

    @Override
    public boolean isComponentFocusRectVisibleEnabled() {
        return focusRect.hasParent();
    }

    public void setColorFocusRect(Color colorFocusRect) {
        this.colorFocusRect = colorFocusRect;
        focusRect.setColor(colorFocusRect);
    }

    public Color getColorFocusRect() {
        return colorFocusRect;
    }

    public Color getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(Color colorBackground) {
        this.colorBackground = colorBackground;
        bg.setColor(colorBackground);
    }

    public Color getColorSelection() {
        return colorSelection;
    }

    public void setColorSelection(Color colorSelection) {
        this.colorSelection = colorSelection;
        selection.setColor(colorSelection);
    }

    private void this_keyType(Event event) {
        var e = (InteractiveEvent) event;
        String keyType = e.getKeyType();
        if (!text.getFont().isCharSupported(keyType.charAt(0))) return;

        if (textString.length() * text.getCharWidth() < getWidth() - 10) {
            insertText(keyType);
        }
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
                if (index < textString.length()) {
                    setText(textString.substring(0, index) + textString.substring(index + 1));
                }
            }
            case KeyCode.HOME -> setCaretPosition(0);

            case KeyCode.END -> setCaretPosition(textString.length());

            case KeyCode.ENTER,
                 KeyCode.RIGHT_ENTER -> {
                dispatchEvent(TextInputEvent.builder()
                    .type(TextInputEvent.ENTER)
                    .text(getText())
                    .keyCode(e.getKeyCode())
                    .build());

                setCaretPosition(Integer.MAX_VALUE);
            }
        }

        if (e.isControl()) {
            switch (e.getCharacter()) {
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
        if (enabled == isEnabled()) return;

        super.setEnabled(enabled);
        text.setColor(enabled ? Component.TEXT_COLOR : Component.TEXT_COLOR_DISABLED);
    }

    public void setBackgroundColor(Color backgroundColor) {
        bg.setColor(backgroundColor);
    }

    public Color getBackgroundColor() {
        return bg.getColor();
    }

    private void this_focusIn(Event event) {
        focusRect.setVisible(true);
        setCaretPosition(Integer.MAX_VALUE);
        dispatchEvent(ComponentEvent.builder().type(ComponentEvent.FOCUS_IN).build());
        focus();
    }

    private void this_focusOut(Event event) {
        focusRect.setVisible(false);
        caret.removeFromParent();
        dispatchEvent(ComponentEvent.builder().type(ComponentEvent.FOCUS_OUT).build());
    }

    private void this_down(Event event) {
        InteractiveEvent e = (InteractiveEvent) event;

        float x = e.getX() - padding.getLeft();
        float c = text.getCharWidth();
        float s = text.getAbsoluteScaleX();

        setCaretPosition((int) ((x / c) / s));
    }

    public void setTextColor(Color textColor) {
        text.setColor(textColor);
    }

    public Color getTextColor() {
        return text.getColor();
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
        selection.setX(text.getX() + selectionFromIndex * text.getCharWidth());
        selection.setWidth((selectionToIndex - selectionFromIndex) * text.getCharWidth());
    }

    public void setText(String textString) {
        this.textString = textString;
        this.text.setText(textString);
        if (getCaretPosition() > textString.length()) {
            setCaretPosition(Integer.MAX_VALUE);
        }

        dispatchEvent(TextInputEvent.builder()
            .type(TextInputEvent.TEXT_CHANGE)
            .text(getText())
            .build());
    }

    public String getText() {
        return textString;
    }

    public void setCaretPosition(int index) {
        index = fixIndex(index);
        caret.setX(padding.getLeft() + (index * text.getCharWidth()) - 1);
        caret.setAlpha(1f);
    }

    private int fixIndex(int index) {
        int len = textString.length();

        if (index > len) {
            index = len;
        } else if (index < 0) {
            index = 0;
        }
        return index;
    }

    public int getCaretPosition() {
        return (int) ((caret.getX() - padding.getLeft() + 1) / text.getCharWidth());
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bg.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bg.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bg.setSize(width, height);
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

    public void focus() {
        super.focus();
        addChild(caret);
    }

    private void insertText(String textToInsert) {
        if (textToInsert.isEmpty()) {
            setText(textToInsert);
        }
        int index = getCaretPosition();
        setText(textString.substring(0, index) + textToInsert + textString.substring(index));
        setCaretPosition(getCaretPosition() + textToInsert.length());

        while (textString.length() * text.getCharWidth() > getWidth() - padding.getLeft() - padding.getRight()) {
            removeChar();
        }
    }

    private void removeChar() {
        int index = getCaretPosition();
        if (index > 0) {
            String newText = textString.substring(0, index - 1) + textString.substring(index);
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
        return textString.charAt(getCaretPosition() - 1);
    }

    public void clear() {
        setText("");
    }

    public void moveCaretToEnd() {
        setCaretPosition(Integer.MAX_VALUE);
    }

    @Override
    public void update() {

    }

    private static class Caret extends RectangleShape {

        public static final int BLINK_DELAY = 25;

        private int blinkCounter = BLINK_DELAY;
        private TextInput uiTextInput;

        public Caret(TextInput uiTextInput) {
            super(1, DEFAULT_HEIGHT - 8, Color.WHITE);
            this.uiTextInput = uiTextInput;
        }

        @Override
        public void onLoopUpdate() {
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
