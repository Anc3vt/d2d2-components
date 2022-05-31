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
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;

public class UiText extends DisplayObjectContainer {

    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private IDisplayObject fore;
    private IDisplayObject shadow;

    private String text;
    private BitmapText bitmapText;
    private BitmapText bitmapTextShadow;
    private float width;
    private float height;
    private boolean shadowEnabled;
    private Color color;
    private boolean autoSize;
    private double vertexBleedingFix;

    public UiText() {
        color = DEFAULT_COLOR;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        shadowEnabled = true;
        text = "";
        //redraw();
    }

    public UiText(Object text) {
        this();
        setText(text);
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        redraw();
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public void setText(Object text) {
        this.text = String.valueOf(text);
        redraw();
    }

    public String getText() {
        return text;
    }

    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    public void setShadowEnabled(boolean shadowEnabled) {
        if (this.shadowEnabled == shadowEnabled) return;
        this.shadowEnabled = shadowEnabled;
        redraw();
    }

    public void append(String string) {
        setText(getText().concat(string));
    }

    public void setWidth(float width) {
        this.width = width;
        redraw();
    }

    public void setHeight(float height) {
        this.height = height;
        redraw();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        redraw();
    }

    public float getTextWidth() {
        return getCharWidth() + (getCharWidth() * text.length());
    }

    public float getCharWidth() {
        return getBitmapFont().getCharInfo('0').width();
    }

    private float getCharHeight() {
        return getBitmapFont().getCharInfo('0').height();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setColor(Color color) {
        if (this.color.equals(color)) return;
        this.color = color;
        redraw();
    }

    public Color getColor() {
        return color;
    }

    public void setVertexBleedingFix(double v) {
        vertexBleedingFix = v;
        if (bitmapTextShadow != null) bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
        if (bitmapText != null) bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
    }

    public double getVertexBleedingFix() {
        return vertexBleedingFix;
    }

    public void redraw() {
        if (shadow != null) {
            shadow.removeFromParent();
        }

        if (shadowEnabled) {
            bitmapTextShadow = new BitmapText(Font.getBitmapFont());
            bitmapTextShadow.setBounds(getWidth(), getHeight());
            bitmapTextShadow.setColor(Color.BLACK);
            bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
            bitmapTextShadow.setText(text);

            if (autoSize) {
                bitmapTextShadow.setBounds(getTextWidth(), getCharHeight());
            }

            shadow = bitmapTextShadow;//.toSprite();
            add(shadow, 0, -1);
        }

        if (fore != null) fore.removeFromParent();

        bitmapText = new BitmapText(Font.getBitmapFont());
        bitmapText.setBounds(getWidth(), getHeight());
        bitmapText.setColor(getColor());
        bitmapText.setVertexBleedingFix(vertexBleedingFix);
        bitmapText.setText(text);

        if (autoSize) {
            bitmapText.setBounds(getTextWidth(), getCharHeight());
        }

        fore = bitmapText;//.toSprite();

        add(fore, 1, 0);
    }

    public BitmapFont getBitmapFont() {
        return Font.getBitmapFont();
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        stage.add(new UiText("Hello world"), 100, 100);

        D2D2.loop();
    }
}
