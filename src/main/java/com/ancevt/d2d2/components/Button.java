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
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_LEFT_PART_DISABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_LEFT_PART_ENABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_MIDDLE_PART_DISABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_MIDDLE_PART_ENABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_RIGHT_PART_DISABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.BUTTON_RIGHT_PART_ENABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.MOUSE_CURSOR;

public class Button extends Component {

    private static final float DEFAULT_WIDTH = 80;
    private static final String DEFAULT_TEXT = "Button";

    private final Sprite leftPart;
    private final Sprite rightPart;
    private final Sprite middlePart;
    private final BitmapTextEx uiText;

    public Button() {
        this(DEFAULT_TEXT);
    }

    public Button(String text) {
        leftPart = new Sprite(BUTTON_LEFT_PART_ENABLED);
        rightPart = new Sprite(BUTTON_RIGHT_PART_ENABLED);
        middlePart = new Sprite(BUTTON_MIDDLE_PART_ENABLED);

        uiText = new BitmapTextEx();

        addEventListener(Button.class, InteractiveEvent.DOWN, event -> {
            dispatchEvent(ButtonEvent.builder().type(ButtonEvent.BUTTON_PRESSED).build());
        });
        addEventListener(Button.class, InteractiveEvent.FOCUS_IN, event -> setForegroundColor(DEFAULT_FOCUS_COLOR));
        addEventListener(Button.class, InteractiveEvent.FOCUS_OUT, event -> setForegroundColor(DEFAULT_FOREGROUND_COLOR));

        add(leftPart);
        add(middlePart);
        add(rightPart);

        middlePart.setVertexBleedingFix(0d);
        middlePart.setTextureBleedingFix(0d);

        add(uiText);

        setSize(DEFAULT_WIDTH, leftPart.getHeight());
        setText(text);

        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setForegroundColor(DEFAULT_FOREGROUND_COLOR);
        setTextColor(DEFAULT_TEXT_COLOR);
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        super.setForegroundColor(foregroundColor);
        leftPart.setColor(foregroundColor);
        middlePart.setColor(foregroundColor);
        rightPart.setColor(foregroundColor);
    }

    @Override
    public void setTextColor(Color textColor) {
        super.setTextColor(textColor);
        uiText.setColor(textColor);
    }

    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) return;

        super.setEnabled(enabled);
        uiText.setColor(enabled ? Color.WHITE : Color.GRAY);

        leftPart.setTexture(enabled ? BUTTON_LEFT_PART_ENABLED : BUTTON_LEFT_PART_DISABLED);
        rightPart.setTexture(enabled ? BUTTON_RIGHT_PART_ENABLED : BUTTON_RIGHT_PART_DISABLED);
        middlePart.setTexture(enabled ? BUTTON_MIDDLE_PART_ENABLED : BUTTON_MIDDLE_PART_DISABLED);
    }

    public void setText(String text) {
        uiText.setText(text);
        fixTextXY();
    }

    public String getText() {
        return uiText.getText();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setWidth(width);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        middlePart.setX(leftPart.getTexture().width());
        middlePart.setScaleX(width - leftPart.getTexture().width() - rightPart.getTexture().width());
        rightPart.setX(leftPart.getTexture().width() + middlePart.getScaleX());

        fixTextXY();
    }

    private void fixTextXY() {
        float w = uiText.getTextWidth() - 5;
        uiText.setX((getWidth() - w) / 2);
    }

    @Override
    public void dispose() {
        super.dispose();
        removeEventListener(Button.class, InteractiveEvent.DOWN);
        removeEventListener(Button.class, InteractiveEvent.FOCUS_IN);
        removeEventListener(Button.class, InteractiveEvent.FOCUS_IN);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ButtonEvent extends Event {
        public static final String BUTTON_PRESSED = "buttonPressed";
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2ComponentAssets.load();
        InteractiveManager.getInstance().setTabbingEnabled(true);
        DebugPanel.setEnabled(true);
        stage.setBackgroundColor(Color.BLACK);

        D2D2.setCursor(new Sprite(MOUSE_CURSOR));

        for (int i = 0; i < 5; i++) {
            Button button = new Button("Test " + i);
            button.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> {
                DebugPanel.show(Button.class.getName() + button.getName(), button.getText() + " pressed").ifPresent(debugPanel -> {
                    debugPanel.setY(button.getY());
                    debugPanel.addButton("enable", () -> button.setEnabled(true));
                    debugPanel.addButton("disable", () -> {
                        debug("Button:175: <a><G>test");
                        button.setEnabled(false);
                    });
                });
            });
            button.setWidth(100);
            stage.add(button, 20, 50 + i * 30);
        }

        D2D2.loop();
        DebugPanel.saveAll();
    }

}





























