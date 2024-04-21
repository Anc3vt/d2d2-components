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
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IColored;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.SpriteFactory;
import com.ancevt.d2d2.display.interactive.Combined9Sprites;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;

public class ButtonEx extends Component implements IColored {

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
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP_RIGHT),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_CENTER),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_RIGHT),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM),
            D2D2.textureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM_RIGHT)
        );

        bg.setEnabled(false);
        add(bg);

        selectedBorder = new Combined9Sprites(
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP_RIGHT),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_CENTER),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_RIGHT),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM_LEFT),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM),
            D2D2.textureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM_RIGHT)
        );

        selectedBorder.setEnabled(false);
        selectedBorder.setVisible(false);
        selectedBorder.setColor(colorTogglePushedInBorder);
        add(selectedBorder);

        addEventListener(Button.class, InteractiveEvent.DOWN, event -> {
            if (toggleMode) {
                setSelected(!isSelected());
                dispatchEvent(Event.builder()
                    .type(Event.CHANGE)
                    .build());
            } else {
                bg.setY(1);
                if (bitmapText != null) bitmapText.moveY(1);
                if (iconSprite != null) iconSprite.moveY(1);
            }
        });

        addEventListener(Button.class, InteractiveEvent.UP, event -> {
            var e = (InteractiveEvent) event;

            if (e.isOnArea()) {
                dispatchEvent(ComponentEvent.builder()
                    .type(ComponentEvent.ACTION)
                    .build());
            }

            bg.setY(0);
            if (bitmapText != null) bitmapText.moveY(0);
            if (iconSprite != null) iconSprite.moveY(0);
            update();
            bg.setColor(e.isOnArea() ? colorHoverBackground : colorBackground);
        });

        addEventListener(Button.class, InteractiveEvent.HOVER, event -> bg.setColor(colorHoverBackground));
        addEventListener(Button.class, InteractiveEvent.OUT, event -> bg.setColor(colorBackground));

        addEventListener(Event.RESIZE, this::this_resize);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCorrespondingColors();
    }

    public void setBackgroundTextures(String[] textureKeys) {
        bg.setTextures(textureKeys);
    }

    public void setSelectedBorderTextures(String[] textureKeys) {
        selectedBorder.setTextures(textureKeys);
    }

    private void this_resize(Event event) {
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
        if (isEnabled()) bg.setColor(colorBackground);
    }

    public Color getColorBackgroundDisabled() {
        return colorBackgroundDisabled;
    }

    public void setColorBackgroundDisabled(Color colorBackgroundDisabled) {
        this.colorBackgroundDisabled = colorBackgroundDisabled;
        if (!isEnabled()) bg.setColor(colorBackgroundDisabled);
    }

    public Color getColorText() {
        return colorText;
    }

    public void setColorText(Color colorText) {
        this.colorText = colorText;
        if (isEnabled() && bitmapText != null) bitmapText.setColor(colorText);
    }

    public Color getColorTextDisabled() {
        return colorTextDisabled;
    }

    public void setColorTextDisabled(Color colorTextDisabled) {
        this.colorTextDisabled = colorTextDisabled;
        if (!isEnabled() && bitmapText != null) bitmapText.setColor(colorTextDisabled);
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
        dispatchEvent(Event.builder().type(Event.CHANGE).build());
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setCorrespondingColors();
    }

    private void setCorrespondingColors() {
        if (bitmapText != null) bitmapText.setColor(isEnabled() ? colorText : colorTextDisabled);
        if (iconSprite != null) {
            iconSprite.setColor(isEnabled() ? Color.WHITE : Color.DARK_GRAY);
            iconSprite.setAlpha(isEnabled() ? 1.0f : 0.5f);
        }
        bg.setColor(isEnabled() ? colorBackground : colorBackgroundDisabled);
    }

    public void setText(Object text) {
        if (bitmapText == null) {
            bitmapText = new BitmapText();
            bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
            bitmapText.setMulticolorEnabled(true);
            add(bitmapText);
        }

        bitmapText.setText("" + text);
        update();
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setIcon(Texture texture) {
        if (iconSprite == null) {
            iconSprite = SpriteFactory.createSprite();
            add(iconSprite);
        }
        iconSprite.setTexture(texture);
        update();
    }

    public Texture getIcon() {
        return iconSprite == null ? null : iconSprite.getTexture();
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
            iconSprite.setXY((w - sw) / 2, (h - sh) / 2);
        }
        if (bitmapText != null) {
            bitmapText.setSize(getWidth(), getHeight());
            float w = getWidth();
            float h = getHeight();
            float tw = bitmapText.getTextWidth();
            float th = bitmapText.getCharHeight();
            bitmapText.setXY((w - tw) / 2, (h - th) / 2 - 3);
        }
    }
}
