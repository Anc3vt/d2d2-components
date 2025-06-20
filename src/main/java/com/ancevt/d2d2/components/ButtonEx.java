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
import com.ancevt.d2d2.scene.Colored;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.interactive.Combined9Sprites;
import com.ancevt.d2d2.scene.text.BitmapText;
import com.ancevt.d2d2.scene.texture.TextureRegion;

public class ButtonEx extends Component implements Colored {

    private static final float DEFAULT_WIDTH = 30.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private final Combined9Sprites bg;
    private final Combined9Sprites selectedBorder;

    private Color colorTogglePushedInBorder = Color.of(0x8080FF);
    private Color colorHoverBackground = Color.of(0xBBBBBB);
    private Color colorBackground = Color.GRAY;
    private Color colorBackgroundDisabled = Color.DARK_GRAY;
    private Color colorText = Color.WHITE;
    private Color colorTextDisabled = Color.GRAY;

    private Sprite iconSprite;
    private BitmapText bitmapText;
    private boolean toggleMode;
    private boolean selected;

    public ButtonEx() {
        setPushEventsUp(false);

        bg = new Combined9Sprites(
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_TOP),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_CENTER),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BUTTON_9_SIDE_BOTTOM_RIGHT)
        );

        bg.setInteractionEnabled(false);
        addChild(bg);

        selectedBorder = new Combined9Sprites(
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_TOP),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_CENTER),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTextureRegion(ComponentAssets.BORDER_9_SIDE_BOTTOM_RIGHT)
        );

        selectedBorder.setInteractionEnabled(false);
        selectedBorder.setVisible(false);
        selectedBorder.setColor(colorTogglePushedInBorder);
        addChild(selectedBorder);

        addEventListener(Button.class, InputEvent.MouseDown.class, event -> {
            if (toggleMode) {
                setSelected(!isSelected());
                dispatchEvent(CommonEvent.Change.create());
            } else {
                bg.setY(1);
                if (bitmapText != null) bitmapText.moveY(1);
                if (iconSprite != null) iconSprite.moveY(1);
            }
        });

        addEventListener(Button.class, InputEvent.MouseUp.class, e -> {
            if (e.isOnArea()) {
                dispatchEvent(CommonEvent.Action.create());
            }

            bg.setY(0);
            if (bitmapText != null) bitmapText.moveY(0);
            if (iconSprite != null) iconSprite.moveY(0);
            update();
            bg.setColor(e.isOnArea() ? colorHoverBackground : colorBackground);
        });

        addEventListener(Button.class, InputEvent.MouseHover.class, event -> bg.setColor(colorHoverBackground));
        addEventListener(Button.class, InputEvent.MouseOut.class, event -> bg.setColor(colorBackground));

        addEventListener(CommonEvent.Resize.class, this::this_resize);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCorrespondingColors();
    }

    public void setBackgroundTextures(String[] textureKeys) {
        bg.setTextures(textureKeys);
    }

    public void setSelectedBorderTextures(String[] textureKeys) {
        selectedBorder.setTextures(textureKeys);
    }

    private void this_resize(CommonEvent.Resize e) {
        bg.setSize(getWidth(), getHeight());
        selectedBorder.setSize(getWidth(), getHeight());
    }

    public Color getColorTogglePushedInBorder() {
        return colorTogglePushedInBorder;
    }

    public void setColorTogglePushedInBorder(Color colorTogglePushedInBorder) {
        this.colorTogglePushedInBorder = colorTogglePushedInBorder;

        if (isSelected()) {
            selectedBorder.setColor(colorTogglePushedInBorder);
        }
    }

    public Color getColorHoverBackground() {
        return colorHoverBackground;
    }

    public void setColorHoverBackground(Color colorHoverBackground) {
        this.colorHoverBackground = colorHoverBackground;
    }

    public Color getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(Color colorBackground) {
        this.colorBackground = colorBackground;
        if (isInteractionEnabled()) bg.setColor(colorBackground);
    }

    public Color getColorBackgroundDisabled() {
        return colorBackgroundDisabled;
    }

    public void setColorBackgroundDisabled(Color colorBackgroundDisabled) {
        this.colorBackgroundDisabled = colorBackgroundDisabled;
        if (!isInteractionEnabled()) bg.setColor(colorBackgroundDisabled);
    }

    public Color getColorText() {
        return colorText;
    }

    public void setColorText(Color colorText) {
        this.colorText = colorText;
        if (isInteractionEnabled() && bitmapText != null) bitmapText.setColor(colorText);
    }

    public Color getColorTextDisabled() {
        return colorTextDisabled;
    }

    public void setColorTextDisabled(Color colorTextDisabled) {
        this.colorTextDisabled = colorTextDisabled;
        if (!isInteractionEnabled() && bitmapText != null) bitmapText.setColor(colorTextDisabled);
    }

    public void setToggleMode(boolean toggleMode) {
        this.toggleMode = toggleMode;
        update();
    }

    public boolean isToggleMode() {
        return toggleMode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedBorder.setVisible(selected);
        dispatchEvent(CommonEvent.Change.create());
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setInteractionEnabled(boolean enabled) {
        super.setInteractionEnabled(enabled);
        setCorrespondingColors();
    }

    private void setCorrespondingColors() {
        if (bitmapText != null) bitmapText.setColor(isInteractionEnabled() ? colorText : colorTextDisabled);
        if (iconSprite != null) {
            iconSprite.setColor(isInteractionEnabled() ? Color.WHITE : Color.DARK_GRAY);
            iconSprite.setAlpha(isInteractionEnabled() ? 1.0f : 0.5f);
        }
        bg.setColor(isInteractionEnabled() ? colorBackground : colorBackgroundDisabled);
    }

    public void setText(String text) {
        if (this.bitmapText == null) {
            this.bitmapText = new BitmapText();
            this.bitmapText.setBitmapFont(ComponentFont.getFontMiddle());
            this.bitmapText.setMulticolor(true);
            addChild(this.bitmapText);
        }

        this.bitmapText.setText("" + text);
        update();
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setIcon(TextureRegion textureRegion) {
        if (iconSprite == null) {
            iconSprite = SpriteFactory.createEmptySprite();
            addChild(iconSprite);
        }
        iconSprite.setTextureRegion(textureRegion);
        update();
    }

    public TextureRegion getIcon() {
        return iconSprite == null ? null : iconSprite.getTextureRegion();
    }

    @Override
    public void setColor(Color color) {
        bg.setColor(color);
    }

    @Override
    public void setColor(int rgb) {
        bg.setColor(rgb);
    }

    @Override
    public Color getColor() {
        return bg.getColor();
    }

    @Override
    public void update() {
        if (iconSprite != null && bitmapText != null) {
            throw new IllegalStateException("Can't set both text and icon");
        }

        if (iconSprite != null) {
            iconSprite.setScale(10f, 10f);
            while (iconSprite.getWidth() * iconSprite.getScaleX() > getWidth() || iconSprite.getHeight() * iconSprite.getScaleY() > getHeight()) {
                iconSprite.scale(0.9f, 0.9f);
            }
            float sw = iconSprite.getWidth() * iconSprite.getScaleX();
            float sh = iconSprite.getHeight() * iconSprite.getScaleY();
            float w = getWidth();
            float h = getHeight();
            iconSprite.setPosition((w - sw) / 2, (h - sh) / 2);
        }
        if (bitmapText != null) {
            bitmapText.setSize(getWidth(), getHeight());
            float w = getWidth();
            float h = getHeight();
            float tw = bitmapText.getTextWidth();
            float th = bitmapText.getCharHeight();
            bitmapText.setPosition((w - tw) / 2, (h - th) / 2 - 3);
        }
    }
}
