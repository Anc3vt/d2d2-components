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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.interactive.InteractiveContainer;

abstract public class Component extends InteractiveContainer {

    public static final Color DEFAULT_FOREGROUND_COLOR = Color.GRAY;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final Color DEFAULT_DISABLED_TEXT_COLOR = Color.GRAY;
    public static final Color DEFAULT_FOCUS_COLOR = Color.WHITE;
    public static final float PANEL_BG_ALPHA = 0.75f;

    private boolean disposed;
    private Color foregroundColor;
    private Color backgroundColor;
    private Color textColor;

    public Component() {
        addEventListener(Component.class, Event.ADD_TO_STAGE, this::this_addToStage);
        super.setEnabled(true);
        setTabbingEnabled(true);
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    private void this_addToStage(Event event) {
        ComponentManager.getInstance().registerComponent(this);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    public void dispose() {
        this.disposed = true;
        ComponentManager.getInstance().unregisterComponent(this);
        removeFromParent();
        removeEventListener(Component.class, Event.ADD_TO_STAGE);
        super.setEnabled(false);
    }

    public boolean isDisposed() {
        return disposed;
    }
}
