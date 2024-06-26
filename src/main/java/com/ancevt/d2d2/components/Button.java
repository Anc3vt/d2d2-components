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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.SpriteFactory;
import com.ancevt.d2d2.display.text.Text;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_LEFT_PART;
import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_MIDDLE_PART;
import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_RIGHT_PART;

public class Button extends Component {

    private static final float DEFAULT_WIDTH = 80;
    private static final String DEFAULT_TEXT = "Button";

    private final Sprite leftPart;
    private final Sprite rightPart;
    private final Sprite middlePart;
    private final Text text;

    public Button() {
        this(DEFAULT_TEXT);
    }

    public Button(String text) {
        leftPart = SpriteFactory.createSpriteByTextureKey(BUTTON_LEFT_PART);
        rightPart = SpriteFactory.createSpriteByTextureKey(BUTTON_RIGHT_PART);
        middlePart = SpriteFactory.createSpriteByTextureKey(BUTTON_MIDDLE_PART);

        this.text = new Text();
        this.text.setFont(ComponentFont.getFontMiddle());

        addEventListener(Button.class, InteractiveEvent.DOWN, event -> {
            leftPart.setY(1);
            middlePart.setY(1);
            rightPart.setY(1);
            fixTextXY();
            this.text.moveY(1);
            dispatchEvent(ButtonEvent.builder().type(ButtonEvent.BUTTON_PRESSED).build());
        });

        addEventListener(Button.class, InteractiveEvent.UP, event -> {
            leftPart.setY(0);
            middlePart.setY(0);
            rightPart.setY(0);
            fixTextXY();
        });

        addEventListener(Button.class, InteractiveEvent.HOVER, event -> {
            Color color = HOVER_FOREGROUND_COLOR;
            leftPart.setColor(color);
            rightPart.setColor(color);
            middlePart.setColor(color);
        });

        addEventListener(Button.class, InteractiveEvent.OUT, event -> {
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

        addChild(this.text);

        setSize(DEFAULT_WIDTH, leftPart.getHeight());
        setText(text);

        setCorrespondingColors();
    }

    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) return;
        super.setEnabled(enabled);
        setCorrespondingColors();
    }

    private void setCorrespondingColors() {
        text.setColor(isEnabled() ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        Color color = isEnabled() ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED;
        leftPart.setColor(color);
        rightPart.setColor(color);
        middlePart.setColor(color);
    }

    public void setText(String text) {
        this.text.setText(text);
        fixTextXY();
    }

    public String getText() {
        return text.getText();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setWidth(width);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        middlePart.setX(leftPart.getTextureClip().getWidth());
        middlePart.setScaleX(width - leftPart.getTextureClip().getWidth() - rightPart.getTextureClip().getWidth());
        rightPart.setX(leftPart.getTextureClip().getWidth() + middlePart.getScaleX());

        fixTextXY();
    }

    private void fixTextXY() {
        float w = text.getTextWidth() - 5;
        float h = text.getFont().getZeroCharHeight();
        text.setXY((getWidth() - w) / 2, (getHeight() - h) / 2);
    }

    @Override
    public void update() {

    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ButtonEvent extends Event {
        public static final String BUTTON_PRESSED = "buttonPressed";
    }
}
