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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LwjglBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Clipboard;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.interactive.Combined9Sprites;
import com.ancevt.d2d2.interactive.InteractiveManager;
import org.jetbrains.annotations.NotNull;

public class TextInput extends Component {

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 30;


    private Color colorBackground = Component.BACKGROUND_COLOR;
    private Color colorSelection = Color.DARK_GRAY;
    private Color colorFocusRect = Color.of(0x515256);
    private float backgroundAlpha = Component.PANEL_BG_ALPHA;

    private final PlainRect bg;
    private final PlainRect selection;
    private final BitmapText bitmapText;
    private final Combined9Sprites focusRect;
    private final Caret caret;
    private boolean selecting;
    private int selectionFromIndex;
    private int selectionToIndex;
    private int selectionStartIndex;
    private String text;

    private Padding padding = new Padding(10, 0, 10, 0);

    public TextInput() {
        bg = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, colorBackground);
        selection = new PlainRect(0, DEFAULT_HEIGHT - 8, colorSelection);
        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());

        bg.setAlpha(backgroundAlpha);

        add(bg);
        // add(selection, uiText.getX(), 4); // selection is completely not implemented yet
        add(bitmapText);

        caret = new Caret(this);
        caret.setXY(bitmapText.getX(), 4);

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

    public void setBitmapFont(BitmapFont bitmapFont) {
        bitmapText.setBitmapFont(bitmapFont);
    }

    public BitmapFont getBitmapFont() {
        return bitmapText.getBitmapFont();
    }

    private void this_hover(Event event) {
        if (isEnabled()) Cursor.switchToText();
    }

    private void this_out(Event event) {
        Cursor.switchToIdle();
    }

    @Override
    public void setPadding(@NotNull Padding padding) {
        this.padding = padding;
        this_resize(null);
    }

    @Override
    public @NotNull Padding getPadding() {
        return padding;
    }

    private void this_resize(Event event) {
        caret.setHeight(getHeight() - getHeight() / 3f);
        caret.setY((getHeight() - caret.getHeight()) / 2);
        bitmapText.setXY(padding.getLeft(), (getHeight() - bitmapText.getCharHeight()) / 2 + 2);
        bitmapText.setWidth(getWidth() - padding.getLeft() - padding.getRight() + 10);
        focusRect.setSize(getWidth(), getHeight());
    }

    public void setFocusRectVisibleEnabled(boolean focusRectEnabled) {
        if (focusRectEnabled) {
            add(focusRect);
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
        if (!bitmapText.getBitmapFont().isCharSupported(keyType.charAt(0))) return;
        if (text.length() * bitmapText.getCharWidth() < getWidth() - 10) insertText(keyType);
    }

    private void this_keyUp(Event event) {

    }

    private void this_keyDown(Event event) {
        var e = (InteractiveEvent) event;
        switch (e.getCode()) {

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
                        .keyCode(e.getCode())
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
                .keyCode(e.getCode())
                .build());

    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) return;

        super.setEnabled(enabled);
        bitmapText.setColor(enabled ? Component.TEXT_COLOR : Component.TEXT_COLOR_DISABLED);
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
        focus();
    }

    private void this_focusOut(Event event) {
        focusRect.setVisible(false);
        caret.removeFromParent();
    }

    private void this_down(Event event) {
        InteractiveEvent e = (InteractiveEvent) event;

        float x = e.getX() - padding.getLeft();
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
        caret.setX(padding.getLeft() + (index * bitmapText.getCharWidth()) - 1);
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
        return (int) ((caret.getX() - padding.getLeft() + 1) / bitmapText.getCharWidth());
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
        add(caret);
    }

    private void insertText(String textToInsert) {
        if (textToInsert.length() == 0) {
            setText(textToInsert);
        }
        int index = getCaretPosition();
        setText(text.substring(0, index) + textToInsert + text.substring(index));
        setCaretPosition(getCaretPosition() + textToInsert.length());

        while (text.length() * bitmapText.getCharWidth() > getWidth() - padding.getLeft() - padding.getRight()) {
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
    public void update() {

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


    public static void main(String[] args) {
        Stage stage = D2D2.init(new LwjglBackend(800, 800, "(floating)"));
        ComponentAssets.init();
        InteractiveManager.getInstance().setTabbingEnabled(true);

        stage.setBackgroundColor(Color.of(0x112233));

        for (int i = 0; i < 20; i++) {
            TextInput textInput = new TextInput();
            textInput.setText((15 + i) + " " + Math.random());
            textInput.setHeight(15 + i);

            if (i == 5) {
                textInput.setEnabled(false);
            }

            //textInput.setBitmapFont(D2D2.getBitmapFontManager().loadBitmapFont(StandardBitmapFonts.TERMINUS_16));

            stage.add(textInput, 50, 10 + i * 35);
        }

        BitmapText bitmapText = new BitmapText("Hello world");
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        stage.add(bitmapText, 400, 100);
        D2D2.loop();
    }


}
