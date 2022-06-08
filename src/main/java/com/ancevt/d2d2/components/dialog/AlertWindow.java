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
package com.ancevt.d2d2.components.dialog;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2.event.Event.ADD_TO_STAGE;

public class AlertWindow extends Container {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final PlainRect bg;
    private final BitmapText bitmapText;
    private Runnable onCloseFunction;

    public AlertWindow() {
        bg = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        add(bg);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFont());
        bitmapText.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        add(bitmapText, PADDING, PADDING);

        Button buttonOk = new Button("OK");
        buttonOk.setXY((getWidth() - buttonOk.getWidth()) / 2, getHeight() - PADDING_CONTROLS);
        add(buttonOk);

        buttonOk.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> {
            var e = (Button.ButtonEvent) event;
            close();
        });

        addEventListener(this, ADD_TO_STAGE, this::add_to_stage);
    }

    private void add_to_stage(Event event) {
        removeEventListener(this, ADD_TO_STAGE);
        stage().addEventListener(this, InputEvent.KEY_DOWN, e1 -> {
            var e = (InputEvent) e1;
            if (e.getKeyCode() == KeyCode.ENTER) {
                close();
            }
        });
    }

    public void setOnCloseFunction(Runnable onOkFunction) {
        this.onCloseFunction = onOkFunction;
    }

    public Runnable getOnCloseFunction() {
        return onCloseFunction;
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

    public void close() {
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onCloseFunction != null) {
            onCloseFunction.run();
        }
    }

    public void center() {
        setXY(
                (stage().getWidth() - getWidth()) / 2f,
                (stage().getHeight() - getHeight()) / 2f
        );
    }

    public static @NotNull AlertWindow show(String text, @NotNull Container doc) {
        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText(text);
        doc.add(alertWindow);
        alertWindow.center();
        return alertWindow;
    }

    public static void main(String[] args) {
        Stage root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        ComponentAssets.load();
        root.setBackgroundColor(Color.GRAY);

        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText("Server is localhost:3333 unavailable");
        root.add(alertWindow);
        alertWindow.center();

        D2D2.loop();
    }
}
