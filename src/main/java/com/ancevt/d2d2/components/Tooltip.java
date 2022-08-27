/**
 * Copyright (C) 2022 the original author or authors.
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
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.interactive.Combined9Sprites;
import com.ancevt.d2d2.interactive.DragUtil;

import static com.ancevt.d2d2.D2D2.getTextureManager;
import static com.ancevt.d2d2.D2D2.init;
import static com.ancevt.d2d2.D2D2.loop;

public class Tooltip extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 200.0f;

    private static Tooltip tooltip;

    private final PlainRect bg;
    private final Combined9Sprites borders;
    private final BitmapText bitmapText;
    private final Sprite sprite;
    private float maxImageWidth;
    private float maxImageHeight;

    private Tooltip() {
        bg = new PlainRect(BACKGROUND_COLOR);
        add(bg);

        setEnabled(false);

        borders = new Combined9Sprites(
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_CENTER),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_RIGHT)
        );
        borders.setColor(FOREGROUND_COLOR);
        add(borders);

        sprite = new Sprite();
        add(sprite, 10, 10);

        bitmapText = new BitmapText(ComponentFont.getBitmapFontMiddle());
        bitmapText.setMulticolorEnabled(true);
        bitmapText.setAutosize(true);
        add(bitmapText, 10, 10);

        addEventListener(Tooltip.class, Event.RESIZE, this::this_resize);
        addEventListener(Tooltip.class, Event.ADD_TO_STAGE, this::this_addToStage);

        setPushEventsUp(false);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_addToStage(Event event) {
        if (tooltip != null && tooltip != this) tooltip.removeFromParent();
    }

    private void this_resize(Event event) {
        bg.setSize(getWidth(), getHeight());
        borders.setSize(getWidth(), getHeight());
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
        rebuild();
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public void setText(Object text) {
        bitmapText.setText("" + text);
        rebuild();
    }

    private void rebuild() {
        float spriteWidth = sprite.getTexture() == null ? 0.0f : sprite.getWidth() * sprite.getScaleX();
        float spriteHeight = sprite.getTexture() == null ? 0.0f : sprite.getHeight() * sprite.getScaleY();

        bitmapText.setX(10 + spriteWidth + 10);

        float textWidth = bitmapText.getWidth();
        float textHeight = bitmapText.getHeight();

        float w = 10.0f + spriteWidth + 10.0f + textWidth + 10;
        float h = 10.0f + spriteHeight + 10.0f;

        if (textHeight > spriteHeight) {
            h = 10.0f + textHeight + 10.0f;
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

        if(sprite.getTexture() != null) {
            if (maxImageWidth != 0) {
                while (sprite.getWidth() * sprite.getScaleX() > maxImageWidth) {
                    sprite.toScale(0.09f, 0.09f);
                }
            }

            if (maxImageHeight != 0) {
                while (sprite.getHeight() * sprite.getScaleY() > maxImageHeight) {
                    sprite.toScale(0.09f, 0.09f);
                }
            }
        }

        rebuild();
    }

    public void setMaxImageSize(float width, float height) {
        maxImageWidth = width;
        maxImageHeight = height;
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

    public static void main(String[] args) {
        Stage stage = init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.init();

        Tooltip tooltip = Tooltip.createTooltip();
        tooltip.setTexture(getTextureManager().getTexture("satellite"));
        tooltip.setText("""
                #This is a tooltip ϕϕϕϕϕϕϕ
                                
                <FF8000>Second line
                                
                <BBBBBB>Third line
                One more line
                And again""");
        tooltip.setImageScale(2f);

        stage.addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;

            if(e.getKeyCode() == KeyCode.SPACE) {
                D2D2.setSmoothMode(!D2D2.isSmoothMode());
                System.out.println(D2D2.isSmoothMode());
            }
        });

        ButtonEx buttonEx = new ButtonEx();
        buttonEx.setIcon(getTextureManager().getTexture("satellite"));
        buttonEx.setSize(60, 60);
        buttonEx.setTooltip(tooltip);
        buttonEx.setPushEventsUp(false);

        DragUtil.enableDrag(buttonEx);

        stage.add(buttonEx, 100, 250);

        loop();
    }
}




























