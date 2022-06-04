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

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveContainer;
import org.jetbrains.annotations.NotNull;

abstract public class Component extends InteractiveContainer {

    public static final Color FOREGROUND_COLOR = Color.GRAY;
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color MENU_BACKGROUND_COLOR = Color.BLACK;
    public static final Color MENU_HOVER_BACKGROUND_COLOR = Color.of(0x111111);
    public static final Color MENU_SEPARATOR_COLOR = Color.of(0x1111111);
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR_DISABLED = Color.GRAY;
    public static final Color FOREGROUND_COLOR_DISABLED = Color.DARK_GRAY;
    public static final Color FOCUS_RECT_COLOR = Color.YELLOW;
    public static final float FOCUS_RECT_ALPHA = 0.75f;
    public static final Color HOVER_FOREGROUND_COLOR = Color.of(0xBBBBBB);
    public static final Color TOGGLE_BUTTON_PUSHED_IN_BORDER_COLOR = Color.of(0x8080FF);
    public static final float FOCUS_RECT_BORDER_WIDTH = 1.0f;
    public static final float PANEL_BG_ALPHA = 0.75f;
    private static final float DEFAULT_PADDING_LEFT = 2.0f;
    private static final float DEFAULT_PADDING_TOP = 2.0f;
    private static final float DEFAULT_PADDING_RIGHT = 2.0f;
    private static final float DEFAULT_PADDING_BOTTOM = 2.0f;

    private final BorderedRect focusRect;
    private boolean focusRectEnabled;
    private Padding padding;

    public Component() {
        super.setEnabled(true);

        focusRectEnabled = true;

        focusRect = new BorderedRect(0, 0, null, FOCUS_RECT_COLOR);
        focusRect.setBorderWidth(FOCUS_RECT_BORDER_WIDTH);
        focusRect.setAlpha(FOCUS_RECT_ALPHA);

        addEventListener(Component.class, InteractiveEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(Component.class, InteractiveEvent.FOCUS_OUT, this::this_focusOut);

        padding = new Padding(DEFAULT_PADDING_LEFT, DEFAULT_PADDING_TOP, DEFAULT_PADDING_RIGHT, DEFAULT_PADDING_BOTTOM);

        setTabbingEnabled(true);
    }

    private void this_focusIn(Event event) {
        var e = (InteractiveEvent) event;
        if (focusRectEnabled && !e.isByMouseDown() && !focusRect.hasParent()) add(focusRect);
    }

    private void this_focusOut(Event event) {
        focusRect.removeFromParent();
    }

    public void setPadding(@NotNull Padding padding) {
        padding.setComponent(this);
        this.padding = padding;
        update();
    }

    public @NotNull Padding getPadding() {
        return padding;
    }

    public void setFocusRectEnabled(boolean focusRectEnabled) {
        this.focusRectEnabled = focusRectEnabled;
        if (!focusRectEnabled) {
            focusRect.removeFromParent();
        }
    }

    public boolean isFocusRectEnabled() {
        return focusRectEnabled;
    }

    abstract public void update();

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        focusRect.setSize(width, height);
        update();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        focusRect.setWidth(width);
        update();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        focusRect.setHeight(height);
        update();
    }

    public void disposeOnRemoveFromStage() {
        addEventListener(Component.class, Event.REMOVE_FROM_STAGE, event -> dispose());
    }

    @Override
    public void dispose() {
        super.dispose();
        removeEventListener(Component.class, Event.REMOVE_FROM_STAGE);
        removeEventListener(Component.class, InteractiveEvent.FOCUS_IN);
        removeEventListener(Component.class, InteractiveEvent.FOCUS_OUT);
    }
}
