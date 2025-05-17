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

package com.ancevt.d2d2.components.dialog;

import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.SceneEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.BasicGroup;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.text.BitmapText;

import static com.ancevt.d2d2.D2D2.root;

public class AlertWindow extends BasicGroup {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final RectangleShape bg;
    private final BitmapText bitmapText;
    private Runnable onCloseFunction;

    public AlertWindow() {
        bg = new RectangleShape(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        addChild(bg);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getFontMiddle());
        bitmapText.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        addChild(bitmapText, PADDING, PADDING);

        Button buttonOk = new Button("OK");
        buttonOk.setPosition((getWidth() - buttonOk.getWidth()) / 2, getHeight() - PADDING_CONTROLS);
        addChild(buttonOk);

        buttonOk.addEventListener(Button.ButtonPressEvent.class, event -> close());

        addEventListener(this, SceneEvent.AddToScene.class, this::add_to_stage);
    }

    private void add_to_stage(SceneEvent.AddToScene event) {
        removeEventListener(this, SceneEvent.AddToScene.class);
        root().addEventListener(this, InputEvent.KeyDown.class, e1 -> {
            if (e1.getKeyCode() == KeyCode.ENTER) {
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
        this.bitmapText.setText(text + "");
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
        root().removeEventListener(this, InputEvent.KeyDown.class);
        removeFromParent();
        if (onCloseFunction != null) {
            onCloseFunction.run();
        }
    }

    public void center() {
        setPosition(
                (root().getWidth() - getWidth()) / 2f,
                (root().getHeight() - getHeight()) / 2f
        );
    }

    public static AlertWindow show(String text, BasicGroup doc) {
        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText(text);
        doc.addChild(alertWindow);
        alertWindow.center();
        return alertWindow;
    }

}
