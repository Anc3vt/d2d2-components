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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.interactive.InteractiveSprite;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.texture.TextureClip;

public class Scrollbar extends Component {

    private static final float DEFAULT_WIDTH = 6.0f;
    private static final float DEFAULT_HEIGHT = 200.0f;

    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 1.0f;
    private static final float ALPHA_SPEED = 0.1f;
    public static final int FADE_HOLD = 50;

    private final RectangleShape _debugRect;
    private final ScrollbarRect rect;
    private int fadeHold = FADE_HOLD;
    private boolean hovered;

    public Scrollbar() {
        setTabbingEnabled(false);

        _debugRect = new RectangleShape();
        _debugRect.setColor(Color.GRAY);
        //add(_debugRect);

        rect = new ScrollbarRect(this);
        rect.setColor(Color.WHITE);
        addChild(rect);

        addEventListener(Scrollbar.class, CommonEvent.Resize.class, this::this_resize);
        addEventListener(Scrollbar.class, InputEvent.MouseHover.class, this::this_hover);
        addEventListener(Scrollbar.class, InputEvent.MouseOut.class, this::this_out);
        addEventListener(Scrollbar.class, InputEvent.MouseWheel.class, this::this_wheel);

        setAlpha(MIN_ALPHA);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_wheel(InputEvent.MouseWheel event) {
        scroll(-event.delta());
    }

    private void scroll(int delta) {
        rect.moveY(delta * 25);
        rect.fixBounds();
        dispatchEvent(CommonEvent.Change.create());
        fadeIn();
    }

    public void setScrollValue(float scrollValue) {
        if (scrollValue == getScrollValue()) return;
        if (scrollValue < 0f) {
            scrollValue = 0f;
        } else if (scrollValue > 1.0f) {
            scrollValue = 1.0f;
        }
        rect.setY(scrollValue * (getHeight() - rect.getHeight()));
        fadeIn();
    }

    public float getScrollValue() {
        return rect.getY() / (getHeight() - rect.getHeight());
    }

    @Override
    public void onExitFrame() {
        super.onExitFrame();

        if (fadeHold > 0) {
            setAlpha(getAlpha() + ALPHA_SPEED);
            if (getAlpha() >= MAX_ALPHA) setAlpha(MAX_ALPHA);
        } else {
            if (getAlpha() > MIN_ALPHA) {
                setAlpha(getAlpha() - ALPHA_SPEED);
                if (getAlpha() <= MIN_ALPHA) setAlpha(MIN_ALPHA);
            }
        }

        if (fadeHold > 0 && !hovered) fadeHold--;
    }

    private void fadeIn() {
        fadeHold = FADE_HOLD;
    }

    private void this_out(InputEvent.MouseOut event) {
        hovered = false;
    }

    private void this_hover(InputEvent.MouseHover event) {
        fadeIn();
        hovered = true;
    }

    private void this_resize(CommonEvent.Resize event) {
        _debugRect.setSize(getWidth(), getHeight());
        rect.setWidth(getWidth());
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {
        rect.dispose();
        super.dispose();
    }

    public void setRectLength(float rectLength) {
        rect.setHeight(rectLength);
    }

    public float getRectLength() {
        return rect.getHeight();
    }

    private static class ScrollbarRect extends InteractiveSprite {

        private static final float DEFAULT_HEIGHT = 60.0f;
        private static final String ASSET_1X1 = "d2d2-core-1x1.png";
        private static TextureClip textureClip;

        private final Scrollbar scrollbar;

        private int oldY;

        private static TextureClip get1x1Texture() {
            if (textureClip != null) return textureClip;
            return textureClip = D2D2.textureManager().loadTexture(ASSET_1X1).createTextureClip();
        }

        public ScrollbarRect(Scrollbar scrollbar) {
            super(get1x1Texture());
            this.scrollbar = scrollbar;
            setHeight(DEFAULT_HEIGHT);

            setPushEventsUp(true);

            addEventListener(ScrollbarRect.class, InputEvent.MouseDown.class, this::this_down);
            addEventListener(ScrollbarRect.class, InputEvent.MouseDrag.class, this::this_drag);
            addEventListener(ScrollbarRect.class, InputEvent.MouseWheel.class, scrollbar::dispatchEvent);
        }

        private void this_down(InputEvent.MouseDown e) {
            oldY = (int) (e.y() + getY());
        }

        private void this_drag(InputEvent.MouseDrag e) {
            final int ty = (int) (e.y() + getY());
            moveY(ty - oldY);
            oldY = ty;
            fixBounds();
            scrollbar.fadeIn();
            scrollbar.dispatchEvent(CommonEvent.Change.create());
        }

        private void fixBounds() {
            if (getY() < 0) {
                setY(0);
            } else if (getY() > scrollbar.getHeight() - getHeight()) {
                setY(scrollbar.getHeight() - getHeight());
            }
        }

        @Override
        public float getWidth() {
            return getScaleX();
        }

        @Override
        public float getHeight() {
            return getScaleY();
        }

        public void setSize(float width, float height) {
            setScale(width, height);
        }

        public void setWidth(float width) {
            setScaleX(width);
        }

        public void setHeight(float height) {
            setScaleY(height);
        }

    }
}
