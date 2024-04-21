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
package com.ancevt.d2d2.components.dialog;

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IContainer;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2.event.Event.ADD_TO_STAGE;

public class DialogWindow extends Container {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final PlainRect bg;
    private final BitmapText bitmapText;
    private final Button buttonOk;
    private final Button buttonCancel;
    private Runnable onOkFunction;
    private Runnable onCancelFunction;

    public DialogWindow() {
        bg = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        add(bg);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        bitmapText.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        add(bitmapText, PADDING, PADDING);

        buttonOk = new Button("OK");
        buttonOk.setXY((getWidth() - buttonOk.getWidth()) / 2 - 50, getHeight() - PADDING_CONTROLS);
        buttonOk.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> ok());
        add(buttonOk);

        buttonCancel = new Button("Cancel");
        buttonCancel.setXY((getWidth() - buttonOk.getWidth()) / 2 + 50, getHeight() - PADDING_CONTROLS);
        buttonCancel.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> cancel());
        add(buttonCancel);

        addEventListener(this, ADD_TO_STAGE, this::add_to_stage);
    }

    private void add_to_stage(Event event) {
        removeEventListener(this, ADD_TO_STAGE);
        stage().addEventListener(this, InputEvent.KEY_DOWN, e1 -> {
            var e = (InputEvent) e1;
            switch (e.getKeyCode()) {
                case KeyCode.ENTER -> ok();
                case KeyCode.ESCAPE -> cancel();
            }
        });
    }

    public void setOnOkFunction(Runnable onOkFunction) {
        this.onOkFunction = onOkFunction;
    }

    public Runnable getOnOkFunction() {
        return onOkFunction;
    }

    public void setOnCancelFunction(Runnable onCancelFunction) {
        this.onCancelFunction = onCancelFunction;
    }

    public Runnable getOnCancelFunction() {
        return onCancelFunction;
    }

    public void setText(Object text) {
        bitmapText.setText(text + "");
    }

    public String getText() {
        return bitmapText.getText();
    }

    public void setSize(float w, float h) {
        setWidth(w);
        setHeight(h);
    }

    private void setWidth(float w) {
        bg.setWidth(w);
        bitmapText.setWidth(w - PADDING * 2f);
    }

    private void setHeight(float h) {
        bg.setHeight(h);
        bitmapText.setHeight(h - PADDING_CONTROLS - PADDING * 2f);
    }

    public float getWidth() {
        return bg.getWidth();
    }

    public float getHeight() {
        return bg.getHeight();
    }

    public void ok() {
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onOkFunction != null) {
            onOkFunction.run();
        }
    }

    public void cancel() {
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onCancelFunction != null) {
            onCancelFunction.run();
        }
    }

    public void center() {
        setXY(
            (stage().getWidth() - getWidth()) / 2f,
            (stage().getHeight() - getHeight()) / 2f
        );
    }

    public static DialogWindow show(String text, IContainer cont) {
        DialogWindow dialogWindow = new DialogWindow();
        dialogWindow.setText(text);
        cont.add(dialogWindow);
        dialogWindow.center();
        return dialogWindow;
    }

}
