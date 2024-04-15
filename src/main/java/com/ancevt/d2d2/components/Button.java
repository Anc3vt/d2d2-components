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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LwjglBackend;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.SpriteFactory;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.interactive.InteractiveManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_LEFT_PART;
import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_MIDDLE_PART;
import static com.ancevt.d2d2.components.ComponentAssets.BUTTON_RIGHT_PART;
import static com.ancevt.d2d2.components.ComponentAssets.MOUSE_CURSOR_IDLE;

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
        leftPart = SpriteFactory.createSprite(BUTTON_LEFT_PART);
        rightPart = SpriteFactory.createSprite(BUTTON_RIGHT_PART);
        middlePart = SpriteFactory.createSprite(BUTTON_MIDDLE_PART);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());

        addEventListener(Button.class, InteractiveEvent.DOWN, event -> {
            leftPart.setY(1);
            middlePart.setY(1);
            rightPart.setY(1);
            fixTextXY();
            bitmapText.moveY(1);
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

        add(leftPart);
        add(middlePart);
        add(rightPart);

        middlePart.setVertexBleedingFix(0d);
        middlePart.setTextureBleedingFix(0d);

        add(bitmapText);

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
        bitmapText.setColor(isEnabled() ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        Color color = isEnabled() ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED;
        leftPart.setColor(color);
        rightPart.setColor(color);
        middlePart.setColor(color);
    }

    public void setText(String text) {
        bitmapText.setText(text);
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

        middlePart.setX(leftPart.getTexture().width());
        middlePart.setScaleX(width - leftPart.getTexture().width() - rightPart.getTexture().width());
        rightPart.setX(leftPart.getTexture().width() + middlePart.getScaleX());

        fixTextXY();
    }

    private void fixTextXY() {
        float w = bitmapText.getTextWidth() - 5;
        float h = bitmapText.getBitmapFont().getZeroCharHeight();
        bitmapText.setXY((getWidth() - w) / 2, (getHeight() - h) / 2);
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

    public static void main(String[] args) {
        Stage stage = D2D2.directInit(new LwjglBackend(800, 600, "(floating)"));
        ComponentAssets.init();
        InteractiveManager.getInstance().setTabbingEnabled(true);
        DebugPanel.setEnabled(true);
        stage.setBackgroundColor(Color.BLACK);

        D2D2.setCursor(SpriteFactory.createSprite(MOUSE_CURSOR_IDLE));

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


        Button b = new Button("lu");
        b.addEventListener("asdasd", Event.LOOP_UPDATE, e -> {
            b.setXY(Mouse.getX(), Mouse.getY());
        });

        stage.add(b);


        D2D2.loop();
        DebugPanel.saveAll();
    }

}





























