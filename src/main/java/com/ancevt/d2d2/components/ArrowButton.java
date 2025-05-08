/**
 * Copyright (C) 2025 the original author or authors.
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

import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.GroupImpl;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.SpriteFactory;
import com.ancevt.d2d2.scene.interactive.InteractiveGroup;
import lombok.Getter;

import static com.ancevt.d2d2.components.ComponentAssets.ARROW_BUTTON;

public class ArrowButton extends GroupImpl {

    private final Sprite sprite;
    private final Sprite shadow;
    private final InteractiveGroup interactiveButton;
    private int direction;

    public ArrowButton() {
        sprite = SpriteFactory.createSpriteByTextureKey(ARROW_BUTTON);
        shadow = SpriteFactory.createSpriteByTextureKey(ARROW_BUTTON);
        shadow.setColor(Color.BLACK);

        addChild(shadow, 1, 1);
        addChild(sprite);

        interactiveButton = new InteractiveGroup(sprite.getWidth(), sprite.getHeight());
        addChild(interactiveButton);

        interactiveButton.addEventListener(InputEvent.MouseDown.class, this::interactiveButton_down);

        setEnabled(true);
    }

    @Override
    public float getWidth() {
        return sprite.getTextureClip().getWidth();
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
            sprite.setX(sprite.getTextureClip().getWidth());
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

    private void interactiveButton_down(InputEvent.MouseDown e) {
        dispatchEvent(ArrowButtonPressEvent.create());
    }

    public void dispose() {
        interactiveButton.setEnabled(false);
    }

    @EventPooled
    @Getter
    public static class ArrowButtonPressEvent extends Event {
        public static ArrowButtonPressEvent create() {
            return EventPool.obtain(ArrowButtonPressEvent.class);
        }
    }
}
