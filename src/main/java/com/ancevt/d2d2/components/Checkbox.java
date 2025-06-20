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

package com.ancevt.d2d2.components;

import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.text.BitmapText;

import static com.ancevt.d2d2.components.ComponentAssets.CHECKBOX_UNCHECKED;

public class Checkbox extends Component {

    private static final float PADDING = 7.0f;
    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private BitmapText label;
    private final Sprite sprite;
    private boolean checked;

    public Checkbox() {
        sprite = SpriteFactory.createSpriteByTextureKey(CHECKBOX_UNCHECKED);
        addChild(sprite);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Checkbox(String text) {
        this();
        label = new BitmapText();
        label.setText(text);
        label.setBitmapFont(ComponentFont.getFontMiddle());
        addChild(label, sprite.getWidth() + PADDING, 0);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        addEventListener(Checkbox.class, InputEvent.MouseDown.class, this::this_down);
    }

    private void this_down(InputEvent.MouseDown e) {
        if (!isInteractionEnabled()) return;
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
                label = new BitmapText();
                label.setText(text);
                label.setBitmapFont(ComponentFont.getFontMiddle());
                addChild(label, sprite.getWidth() + PADDING, 0);
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
        sprite.setPosition(PADDING, (getHeight() - sprite.getHeight()) / 2);
        if (label != null) {
            float h = label.getBitmapFont().getZeroCharHeight();
            label.setPosition(PADDING + sprite.getWidth() + PADDING, (getHeight() - h) / 2 + 1);
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
        //sprite.setTextureRegion(checked ? CHECKBOX_CHECKED : CHECKBOX_UNCHECKED);
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setInteractionEnabled(boolean enabled) {
        if (enabled == isInteractionEnabled()) return;
        super.setInteractionEnabled(enabled);
        if (label != null) {
            label.setColor(enabled ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        }

        sprite.setColor(enabled ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED);
    }

    @Override
    public void update() {

    }
}
