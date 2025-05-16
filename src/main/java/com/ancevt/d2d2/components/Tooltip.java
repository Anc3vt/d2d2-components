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
import com.ancevt.d2d2.event.SceneEvent;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.SpriteFactory;
import com.ancevt.d2d2.scene.interactive.Combined9Sprites;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.text.BitmapText;
import com.ancevt.d2d2.scene.texture.TextureRegion;


public class Tooltip extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 200.0f;

    private static Tooltip tooltip;

    private final RectangleShape bg;
    private final Combined9Sprites borders;
    private final BitmapText bitmapText;
    private final Sprite spriteBg;
    private final Sprite sprite;
    private float maxImageWidth;
    private float maxImageHeight;

    private Tooltip() {
        bg = new RectangleShape(1, 1, BACKGROUND_COLOR);
        addChild(bg);

        setEnabled(false);

        borders = new Combined9Sprites(
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_TOP_LEFT),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_TOP),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_TOP_RIGHT),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_LEFT),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_CENTER),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_RIGHT),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_LEFT),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM),
                D2D2.textureManager().getTextureRegion(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_RIGHT)
        );
        borders.setColor(FOREGROUND_COLOR);
        addChild(borders);

        spriteBg = SpriteFactory.createSpriteByTextureKey(ComponentAssets.TOOLTIP_IMAGE_BACKGROUND);
        addChild(spriteBg, 10, 10);
        spriteBg.setColor(Color.of(0x111111));
        spriteBg.setVisible(false);

        sprite = SpriteFactory.createEmptySprite();
        addChild(sprite, 10, 10);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getFontMiddle());
        bitmapText.setMulticolor(true);
        bitmapText.setAutosize(true);
        addChild(bitmapText, 10, 10);

        addEventListener(Tooltip.class, CommonEvent.Resize.class, this::this_resize);
        addEventListener(Tooltip.class, SceneEvent.AddToScene.class, this::this_addToStage);

        setPushEventsUp(false);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_addToStage(SceneEvent.AddToScene event) {
        if (tooltip != null && tooltip != this) tooltip.removeFromParent();
    }

    private void this_resize(CommonEvent.Resize event) {
        bg.setSize(getWidth(), getHeight());
        borders.setSize(getWidth(), getHeight());
    }

    public void setImageBackgroundVisible(boolean value) {
        spriteBg.setVisible(value);
    }

    public boolean isImageBackgroundVisible() {
        return spriteBg.isVisible();
    }

    public void setImage(TextureRegion textureRegion) {
        sprite.setTextureRegion(textureRegion);
        rebuild();
    }

    public TextureRegion getImage() {
        return sprite.getTextureRegion();
    }

    public void setText(String text) {
        this.bitmapText.setText(text);
        rebuild();
    }

    private void rebuild() {
        float spriteWidth = sprite.getTextureRegion() == null ? 0.0f : sprite.getWidth() * sprite.getScaleX();
        float spriteHeight = sprite.getTextureRegion() == null ? 0.0f : sprite.getHeight() * sprite.getScaleY();

        bitmapText.setX(10 + spriteWidth + 10);

        float textWidth = bitmapText.getWidth();
        float textHeight = bitmapText.getHeight();

        float w = 10.0f + spriteWidth + 10.0f + textWidth + 10;
        float h = 10.0f + spriteHeight + 10.0f;

        if (textHeight > spriteHeight) {
            h = 10.0f + textHeight + 10.0f;
        }

        handleMaxSize();

        if (sprite.getTextureRegion() != null && isImageBackgroundVisible()) {
            spriteBg.setRepeat((sprite.getWidth() * sprite.getScaleX()) / 8, (sprite.getHeight() * sprite.getScaleY()) / 8);
        }

        setSize(w, h);
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setImageScale(float scale) {
        sprite.setScale(scale, scale);
        rebuild();
    }

    public void setMaxImageSize(float width, float height) {
        maxImageWidth = width;
        maxImageHeight = height;
        rebuild();
    }

    private void handleMaxSize() {
        sprite.setScale(10f);

        if (sprite.getTextureRegion() != null) {
            if (maxImageWidth != 0) {
                while (sprite.getWidth() * sprite.getScaleX() > maxImageWidth) {
                    sprite.scale(0.99f, 0.99f);
                }
            }

            if (maxImageHeight != 0) {
                while (sprite.getHeight() * sprite.getScaleY() > maxImageHeight) {
                    sprite.scale(0.99f, 0.99f);
                }
            }
        }
    }

    public float getMaxImageWidth() {
        return maxImageWidth;
    }

    public float getMaxImageHeight() {
        return maxImageHeight;
    }

    @Override
    public void update() {

    }

    public static Tooltip createTooltip() {
        tooltip = new Tooltip();
        return tooltip;
    }
}
