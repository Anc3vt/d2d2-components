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
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.text.BitmapText;
import lombok.Getter;

import static com.ancevt.d2d2.components.ComponentAssets.*;

public class Button extends Component {

    private static final float DEFAULT_WIDTH = 80;
    private static final String DEFAULT_TEXT = "Button";

    private final Sprite leftPart;
    private final Sprite rightPart;
    private final Sprite middlePart;
    private final BitmapText bitmapText;

    public Button() {
        this(DEFAULT_TEXT);
    }

    public Button(String text) {
        leftPart = SpriteFactory.createSpriteByTextureKey(BUTTON_LEFT_PART);
        rightPart = SpriteFactory.createSpriteByTextureKey(BUTTON_RIGHT_PART);
        middlePart = SpriteFactory.createSpriteByTextureKey(BUTTON_MIDDLE_PART);

        this.bitmapText = new BitmapText();
        this.bitmapText.setBitmapFont(ComponentFont.getFontMiddle());

        addEventListener(Button.class, InputEvent.MouseDown.class, event -> {
            leftPart.setY(1);
            middlePart.setY(1);
            rightPart.setY(1);
            fixTextXY();
            this.bitmapText.moveY(1);
            dispatchEvent(ButtonPressEvent.create());
        });

        addEventListener(Button.class, InputEvent.MouseUp.class, event -> {
            leftPart.setY(0);
            middlePart.setY(0);
            rightPart.setY(0);
            fixTextXY();
        });

        addEventListener(Button.class, InputEvent.MouseHover.class, event -> {
            Color color = HOVER_FOREGROUND_COLOR;
            leftPart.setColor(color);
            rightPart.setColor(color);
            middlePart.setColor(color);
        });

        addEventListener(Button.class, InputEvent.MouseOut.class, event -> {
            Color color = FOREGROUND_COLOR;
            leftPart.setColor(color);
            rightPart.setColor(color);
            middlePart.setColor(color);
        });

        addChild(leftPart);
        addChild(middlePart);
        addChild(rightPart);

        middlePart.setVertexBleedingFix(0d);
        middlePart.setTextureBleedingFix(0d);

        addChild(this.bitmapText);

        setSize(DEFAULT_WIDTH, leftPart.getHeight());
        setText(text);

        setCorrespondingColors();
    }

    public void setInteractionEnabled(boolean enabled) {
        if (enabled == isInteractionEnabled()) return;
        super.setInteractionEnabled(enabled);
        setCorrespondingColors();
    }

    private void setCorrespondingColors() {
        bitmapText.setColor(isInteractionEnabled() ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        Color color = isInteractionEnabled() ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED;
        leftPart.setColor(color);
        rightPart.setColor(color);
        middlePart.setColor(color);
    }

    public void setText(String text) {
        this.bitmapText.setText(text);
        fixTextXY();
    }

    public String getText() {
        return bitmapText.getText();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setWidth(width);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        middlePart.setX(leftPart.getTextureRegion().getWidth());
        middlePart.setScaleX(width - leftPart.getTextureRegion().getWidth() - rightPart.getTextureRegion().getWidth());
        rightPart.setX(leftPart.getTextureRegion().getWidth() + middlePart.getScaleX());

        fixTextXY();
    }

    private void fixTextXY() {
        float w = bitmapText.getTextWidth() - 5;
        float h = bitmapText.getBitmapFont().getZeroCharHeight();
        bitmapText.setPosition((getWidth() - w) / 2, (getHeight() - h) / 2);
    }

    @Override
    public void update() {

    }

    @Getter
    @EventPooled
    public static class ButtonPressEvent extends Event {
        public static ButtonPressEvent create() {
            return EventPool.obtain(ButtonPressEvent.class);
        }
    }
}
