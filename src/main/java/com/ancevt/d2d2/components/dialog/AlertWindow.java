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

import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.ContainerImpl;
import com.ancevt.d2d2.scene.text.Text;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.KeyCode;

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2.event.Event.ADD_TO_STAGE;

public class AlertWindow extends ContainerImpl {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final RectangleShape bg;
    private final Text text;
    private Runnable onCloseFunction;

    public AlertWindow() {
        bg = new RectangleShape(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        addChild(bg);

        text = new Text();
        text.setFont(ComponentFont.getFontMiddle());
        text.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        addChild(text, PADDING, PADDING);

        Button buttonOk = new Button("OK");
        buttonOk.setXY((getWidth() - buttonOk.getWidth()) / 2, getHeight() - PADDING_CONTROLS);
        addChild(buttonOk);

        buttonOk.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> {
            var e = (Button.ButtonEvent) event;
            close();
        });

        addEventListener(this, ADD_TO_STAGE, this::add_to_stage);
    }

    private void add_to_stage(Event event) {
        removeEventListener(this, ADD_TO_STAGE);
        stage().addEventListener(this, InteractiveEvent.KEY_DOWN, e1 -> {
            var e = (InteractiveEvent) e1;
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
        this.text.setText(text + "");
    }

    public String getText() {
        return text.getText();
    }

    public void setSize(float w, float h) {
        setWidth(w);
        setHeight(h);
    }

    private void setWidth(float w) {
        bg.setWidth(w);
        text.setWidth(w - PADDING * 2f);
    }

    private void setHeight(float h) {
        bg.setHeight(h);
        text.setHeight(h - PADDING_CONTROLS - PADDING * 2f);
    }

    public float getWidth() {
        return bg.getWidth();
    }

    public float getHeight() {
        return bg.getHeight();
    }

    public void close() {
        stage().removeEventListener(this, InteractiveEvent.KEY_DOWN);
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

    public static AlertWindow show(String text, ContainerImpl doc) {
        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText(text);
        doc.addChild(alertWindow);
        alertWindow.center();
        return alertWindow;
    }

}
