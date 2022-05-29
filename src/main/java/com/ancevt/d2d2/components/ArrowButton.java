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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveButtonEvent;
import com.ancevt.d2d2.interactive.InteractiveButton;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.d2d2.components.D2D2ComponentAssets.ARROW_BUTTON;

public class ArrowButton extends DisplayObjectContainer {

    private final Sprite sprite;
    private final Sprite shadow;
    private final InteractiveButton interactiveButton;
    private int direction;

    public ArrowButton() {
        sprite = new Sprite(ARROW_BUTTON);
        shadow = new Sprite(ARROW_BUTTON);
        shadow.setColor(Color.BLACK);

        add(shadow, 1, 1);
        add(sprite);

        interactiveButton = new InteractiveButton((int) sprite.getWidth(), (int) sprite.getHeight(), true);
        add(interactiveButton);

        interactiveButton.addEventListener(InteractiveButtonEvent.DOWN, this::interactiveButton_down);

        setEnabled(true);
    }

    @Override
    public float getWidth() {
        return sprite.getTexture().width();
    }

    public void setEnabled(boolean enabled) {
        interactiveButton.setEnabled(enabled);
        sprite.setColor(enabled ? Color.WHITE : Color.GRAY);
    }

    public boolean isEnabled() {
        return interactiveButton.isEnabled();
    }

    public void setDirection(int direction) {
        this.direction = direction;
        if (direction == -1) {
            sprite.setScaleX(-1f);
            shadow.setScaleX(-1f);
            sprite.setX(sprite.getTexture().width());
        } else {
            sprite.setScaleX(1f);
            shadow.setScaleX(1f);
            sprite.setXY(0f, 0f);
        }

        shadow.setXY(sprite.getX(), sprite.getY());
        shadow.move(1f, 1f);
    }

    public int getDirection() {
        return direction;
    }

    private void interactiveButton_down(Event event) {
        dispatchEvent(
                ArrowButtonEvent.builder()
                        .type(ArrowButtonEvent.ARROW_BUTTON_PRESS)
                        .build()
        );
    }

    public void dispose() {
        interactiveButton.setEnabled(false);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ArrowButtonEvent extends Event {
        public static final String ARROW_BUTTON_PRESS = "arrowButtonPress";
    }
}
