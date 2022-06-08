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
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.interactive.Combined9Sprites;
import com.ancevt.d2d2.interactive.DragUtil;

import static com.ancevt.d2d2.D2D2.stage;

public class Frame extends Component {

    private static final float DEFAULT_WIDTH = 600.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private static final float TITLE_HEIGHT = 25.0f;
    private static final float BACKGROUND_2_HEIGHT = 47.0f;

    private final BitmapText titleBitmapText;
    private final PlainRect titleBg;
    private final PlainRect bg1;
    private final PlainRect bg2;
    private final Combined9Sprites borders;

    private Color colorBackground1 = Color.of(0x161A1D);
    private Color colorBackground2 = Color.of(0x000306);
    private Color colorBorder = Color.of(0x7E7E7E);
    private Color colorTitleBackground = Color.of(0x505050);
    private float backgroundAlpha = 0.9f;

    public Frame() {
        bg1 = new PlainRect();
        bg1.setAlpha(backgroundAlpha);
        bg1.setColor(colorBackground1);
        add(bg1);

        titleBg = new PlainRect(colorTitleBackground);
        add(titleBg);

        titleBitmapText = new BitmapText();
        titleBitmapText.setBitmapFont(ComponentFont.getBitmapFont());
        titleBitmapText.setMulticolorEnabled(true);
        add(titleBitmapText, 10, 10);

        bg2 = new PlainRect((colorBackground2));
        add(bg2);

        borders = new Combined9Sprites(new String[]{
                ComponentAssets.RECT_BORDER_9_SIDE_TOP_LEFT,
                ComponentAssets.RECT_BORDER_9_SIDE_TOP,
                ComponentAssets.RECT_BORDER_9_SIDE_TOP_RIGHT,
                ComponentAssets.RECT_BORDER_9_SIDE_LEFT,
                ComponentAssets.RECT_BORDER_9_SIDE_CENTER,
                ComponentAssets.RECT_BORDER_9_SIDE_RIGHT,
                ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_LEFT,
                ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM,
                ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_RIGHT
        });
        borders.setColor(colorBorder);
        add(borders);

        setFocusRectEnabled(false);

        addEventListener(Frame.class, Event.ADD_TO_STAGE, this::this_addToStage);
        addEventListener(Event.RESIZE, this::this_resize);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_addToStage(Event event) {
        removeEventListener(Frame.class, Event.ADD_TO_STAGE);
        center();
    }

    public void center() {
        setXY((stage().getWidth() - getWidth()) / 2, (stage().getHeight() - getHeight()) / 2);
    }

    private void this_resize(Event event) {
        titleBg.setSize(getWidth(), TITLE_HEIGHT);

        bg1.setSize(getWidth(), getHeight() - TITLE_HEIGHT - BACKGROUND_2_HEIGHT);
        bg1.setXY(0, TITLE_HEIGHT);

        bg2.setSize(getWidth(), BACKGROUND_2_HEIGHT);
        bg2.setXY(0, TITLE_HEIGHT + bg1.getHeight());

        titleBg.setColor(colorTitleBackground);
        bg1.setColor(colorBackground1);
        bg2.setColor(colorBackground2);

        titleBg.setAlpha(backgroundAlpha);
        bg1.setAlpha(backgroundAlpha);
        bg2.setAlpha(backgroundAlpha);

        titleBitmapText.setX((getWidth() - titleBitmapText.getTextWidth()) / 2);
        titleBitmapText.setY((TITLE_HEIGHT - titleBitmapText.getTextHeight()) / 2 + 1);
        borders.setSize(getWidth(), getHeight());
    }

    public Color getColorBackground1() {
        return colorBackground1;
    }

    public void setColorBackground1(Color colorBackground1) {
        this.colorBackground1 = colorBackground1;
        this_resize(null);
    }

    public Color getColorBackground2() {
        return colorBackground2;
    }

    public void setColorBackground2(Color colorBackground2) {
        this.colorBackground2 = colorBackground2;
        this_resize(null);
    }

    public Color getColorBorder() {
        return colorBorder;
    }

    public void setColorBorder(Color colorBorder) {
        this.colorBorder = colorBorder;
        this_resize(null);
    }

    public Color getColorTitleBackground() {
        return colorTitleBackground;
    }

    public void setColorTitleBackground(Color colorTitleBackground) {
        this.colorTitleBackground = colorTitleBackground;
        this_resize(null);
    }

    public float getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        this_resize(null);
    }

    public void setTitle(String title) {
        titleBitmapText.setText(title);
        this_resize(null);
    }

    public String getTitle() {
        return titleBitmapText.getText();
    }

    @Override
    public void update() {

    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.load();

        Frame panel = new Frame();
        panel.setTitle("Title");

        DragUtil.enableDrag(panel);

        stage.add(panel, 100, 100);

        D2D2.loop();
    }
}
