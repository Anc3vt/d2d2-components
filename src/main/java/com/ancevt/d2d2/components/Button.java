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
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveButtonEvent;
import com.ancevt.d2d2.interactive.InteractiveButton;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.commons.unix.UnixDisplay.debug;

public class Button extends DisplayObjectContainer {

    private static final float DEFAULT_WIDTH = 80;
    private static final String DEFAULT_TEXT = "Button";

    private final Sprite leftPart;
    private final Sprite rightPart;
    private final Sprite middlePart;
    private final UiText uiText;
    private final InteractiveButton interactiveButton;
    private float width;

    public Button() {
        this("");
    }

    public Button(String text) {
        leftPart = new Sprite(D2D2Components.getButtonLeftPartTexture());
        rightPart = new Sprite(D2D2Components.getButtonRightPartTexture());
        middlePart = new Sprite(D2D2Components.getButtonMiddlePartTexture());

        uiText = new UiText();

        interactiveButton = new InteractiveButton(true);
        interactiveButton.addEventListener(InteractiveButtonEvent.DOWN, this::interactiveButton_down);
        add(interactiveButton);

        add(leftPart);
        add(middlePart);
        add(rightPart);

        add(uiText);

        setWidth(DEFAULT_WIDTH);
        setText(text);
    }

    private void interactiveButton_down(Event event) {
        dispatchEvent(ButtonEvent.builder().type(ButtonEvent.BUTTON_PRESSED).build());
    }

    public void setEnabled(boolean enabled) {
        interactiveButton.setEnabled(enabled);
        uiText.setColor(enabled ? Color.WHITE : Color.GRAY);
    }

    public boolean isEnabled() {
        return interactiveButton.isEnabled();
    }

    public void setText(String text) {
        uiText.setText(text);
        fixTextXY();
    }

    public String getText() {
        return uiText.getText();
    }

    public void setWidth(float width) {
        this.width = width;
        interactiveButton.setSize(width, leftPart.getTexture().height());

        middlePart.setX(leftPart.getTexture().width());
        middlePart.setScaleX(width - leftPart.getTexture().width() - rightPart.getTexture().width());
        rightPart.setX(leftPart.getTexture().width() + middlePart.getScaleX());

        fixTextXY();
    }

    @Override
    public float getWidth() {
        return width;
    }

    private void fixTextXY() {
        float w = uiText.getTextWidth() - 5;
        uiText.setX((getWidth() - w) / 2);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ButtonEvent extends Event {
        public static final String BUTTON_PRESSED = "buttonPressed";
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2Components.load();
        Button button = new Button("Test");
        button.addEventListener(ButtonEvent.BUTTON_PRESSED, event -> {
            debug("Button:104: <A>TEST");
        });
        stage.add(button, 100, 100);
        D2D2.loop();
    }
}





























