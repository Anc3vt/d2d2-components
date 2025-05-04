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

import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.text.Text;

public class FrameTitle extends Component {

    private static final float DEFAULT_TITLE_HEIGHT = 25.0f;
    private static final Color DEFAULT_TITLE_BACKGROUND_COLOR = Color.of(0x505050);

    private final RectangleShape bg;
    private final Text text;

    public FrameTitle() {
        setComponentFocusRectVisibleEnabled(false);
        setTabbingEnabled(false);

        bg = new RectangleShape(1, 1, DEFAULT_TITLE_BACKGROUND_COLOR);
        addChild(bg);

        text = new Text();
        text.setFont(ComponentFont.getFontMiddle());
        text.setMulticolor(true);
        addChild(text, 10, 10);

        addEventListener(CommonEvent.Resize.class, this::this_resize);

        setSize(getWidth(), DEFAULT_TITLE_HEIGHT);
    }

    private void this_resize(CommonEvent.Resize event) {
        text.setX((getWidth() - text.getTextWidth()) / 2);
        text.setY((DEFAULT_TITLE_HEIGHT - text.getTextHeight()) / 2);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bg.setSize(width, height);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bg.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bg.setHeight(height);
    }

    public void setTextColor(Color color) {
        text.setColor(color);
    }

    public Color getTextColor() {
        return text.getColor();
    }

    public void setBackgroundColor(Color color) {
        bg.setColor(color);
    }

    public Color getBackgroundColor() {
        return bg.getColor();
    }

    public void setText(String text) {
        this.text.setText(text);
        applyResize();
    }

    public String getText() {
        return text.getText();
    }

}
