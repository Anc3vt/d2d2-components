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
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveManager;

import static com.ancevt.d2d2.components.ComponentAssets.CHECKBOX_CHECKED;
import static com.ancevt.d2d2.components.ComponentAssets.CHECKBOX_UNCHECKED;

public class Checkbox extends Component {

    private static final float PADDING = 7.0f;
    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private BitmapText label;
    private final Sprite sprite;
    private boolean checked;

    public Checkbox() {
        sprite = new Sprite(CHECKBOX_UNCHECKED);
        add(sprite);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Checkbox(String text) {
        this();
        label = new BitmapText(text);
        label.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        add(label, sprite.getWidth() + PADDING, 0);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        addEventListener(Checkbox.class, InteractiveEvent.DOWN, this::this_down);
    }

    private void this_down(Event event) {
        if (!isEnabled()) return;

        setChecked(!isChecked());
    }

    public void setText(String text) {
        if (text == null || text.length() == 0) {
            if (label != null) {
                label.removeFromParent();
                label = null;
            }
        } else {
            if (label == null) {
                label = new BitmapText(text);
                label.setBitmapFont(ComponentFont.getBitmapFontMiddle());
                add(label, sprite.getWidth() + PADDING, 0);
            }
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        fixMarkup();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        fixMarkup();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        fixMarkup();
    }

    private void fixMarkup() {
        sprite.setXY(PADDING, (getHeight() - sprite.getHeight()) / 2);
        if (label != null) {
            float h = label.getBitmapFont().getZeroCharHeight();
            label.setXY(PADDING + sprite.getWidth() + PADDING, (getHeight() - h) / 2 + 1);
            label.setSize(getWidth() - PADDING - sprite.getWidth() - PADDING, h);
        }
    }

    public String getText() {
        if (label == null) {
            return "";
        } else {
            return label.getText();
        }
    }

    public void setChecked(boolean checked) {
        if (checked == isChecked()) return;
        this.checked = checked;
        sprite.setTexture(checked ? CHECKBOX_CHECKED : CHECKBOX_UNCHECKED);
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) return;
        super.setEnabled(enabled);
        if (label != null) {
            label.setColor(enabled ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        }

        sprite.setColor(enabled ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED);
    }

    @Override
    public void update() {

    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        stage.setBackgroundColor(Color.of(0x112233));
        InteractiveManager.getInstance().setTabbingEnabled(true);
        ComponentAssets.init();

        for (int i = 0; i < 10; i++) {
            Checkbox checkbox = new Checkbox("Test checkbox #%d".formatted(i));
            stage.add(checkbox, 100, 100 + i * 30);
        }

        D2D2.loop();
    }

}
