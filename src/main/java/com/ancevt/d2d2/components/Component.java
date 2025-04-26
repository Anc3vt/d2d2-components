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

import com.ancevt.d2d2.scene.shape.BorderedRectangle;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Container;
import com.ancevt.d2d2.scene.SceneEntity;
import com.ancevt.d2d2.scene.interactive.InteractiveContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.ancevt.d2d2.D2D2.stage;

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
    public static final Color HOVER_FOREGROUND_COLOR = Color.of(0xBBBBBB);
    public static final Color ACTIVE_FRAME_TITLE_COLOR = Color.WHITE;
    public static final Color INACTIVE_FRAME_TITLE_COLOR = Color.GRAY;

    public static final float FOCUS_RECT_ALPHA = 0.75f;
    public static final float FOCUS_RECT_BORDER_WIDTH = 1.0f;
    public static final float PANEL_BG_ALPHA = 0.9f;
    private static final float DEFAULT_PADDING_LEFT = 2.0f;
    private static final float DEFAULT_PADDING_TOP = 2.0f;
    private static final float DEFAULT_PADDING_RIGHT = 2.0f;
    private static final float DEFAULT_PADDING_BOTTOM = 2.0f;

    private final BorderedRectangle componentFocusRect;
    private boolean componentFocusRectEnabled;
    @Getter
    private Padding padding;
    @Getter
    private Tooltip tooltip;

    private float minWidth;
    private float minHeight;
    private float maxWidth;
    private float maxHeight;

    protected Component() {
        if (!ComponentAssets.isInitialized()) {
            ComponentAssets.init();
        }

        super.setEnabled(true);

        componentFocusRectEnabled = false;

        componentFocusRect = new BorderedRectangle(0, 0, null, FOCUS_RECT_COLOR);
        componentFocusRect.setBorderWidth(FOCUS_RECT_BORDER_WIDTH);
        componentFocusRect.setAlpha(FOCUS_RECT_ALPHA);

        addEventListener(Component.class, InteractiveEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(Component.class, InteractiveEvent.FOCUS_OUT, this::this_focusOut);

        padding = new Padding(DEFAULT_PADDING_LEFT, DEFAULT_PADDING_TOP, DEFAULT_PADDING_RIGHT, DEFAULT_PADDING_BOTTOM);

        setTabbingEnabled(false);
    }

    private void this_focusIn(Event event) {
        var e = (InteractiveEvent) event;
        if (componentFocusRectEnabled && !e.isByMouseDown() && !componentFocusRect.hasParent()) addChild(componentFocusRect);

        FrameManager.getInstance().activateFrame(getFrame());
    }

    private void this_focusOut(Event event) {
        componentFocusRect.removeFromParent();
    }

    public void setTooltip(Tooltip tooltip) {
        if (this.tooltip != null)
            this.tooltip.removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT);

        this.tooltip = tooltip;

        removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.HOVER);
        removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT);

        if (tooltip != null) {
            addEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.HOVER, event -> {

                AtomicBoolean tooltipCancelHover = new AtomicBoolean(false);

                removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.DOWN);
                addEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.DOWN, event1 -> {
                    tooltipCancelHover.set(true);
                    removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.DOWN);
                    tooltip.removeFromParent();
                });

                Timer.setTimeout(1000, t -> {
                    if (!tooltipCancelHover.get() && isHovering()) {
                        stage().addChild(tooltip, Mouse.getX(), Mouse.getY());
                        if (tooltip.getX() + tooltip.getWidth() > stage().getWidth()) {
                            tooltip.setX(stage().getWidth() - tooltip.getWidth());
                        }

                        if (tooltip.getY() + tooltip.getHeight() > stage().getHeight()) {
                            tooltip.setY(stage().getHeight() - tooltip.getHeight());
                        }

                        tooltip.removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT);
                        tooltip.addEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT, event1 -> {
                            tooltip.removeFromParent();
                        });
                    }
                });
            });

            addEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT, event -> {
                Timer.setTimeout(1000, t -> {
                    if (!isHovering() && !tooltip.isHovering()) {
                        tooltip.removeEventListener(Component.class + "" + Tooltip.class, InteractiveEvent.OUT);
                        tooltip.removeFromParent();
                    }
                });
            });
        }
    }

    public void setPadding(Padding padding) {
        padding.setComponent(this);
        this.padding = padding;
        update();
    }

    public void setComponentFocusRectVisibleEnabled(boolean focusRectEnabled) {
        this.componentFocusRectEnabled = focusRectEnabled;
        if (!focusRectEnabled) {
            componentFocusRect.removeFromParent();
        }
    }

    public boolean isComponentFocusRectVisibleEnabled() {
        return componentFocusRectEnabled;
    }

    public void update() {

    }

    @Override
    public void setSize(float width, float height) {
        if (maxWidth != 0.0f && width > maxWidth) width = maxWidth;
        if (minWidth != 0.0f && width < minWidth) width = minWidth;
        if (maxHeight != 0.0f && height > maxHeight) height = maxHeight;
        if (minHeight != 0.0f && height < minHeight) height = minHeight;

        super.setSize(width, height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        componentFocusRect.setSize(width, height);
        update();
    }

    @Override
    public void setWidth(float width) {
        if (maxWidth != 0.0f && width > maxWidth) width = maxWidth;
        if (minWidth != 0.0f && width < minWidth) width = minWidth;

        super.setWidth(width);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        componentFocusRect.setWidth(width);
        update();
    }

    @Override
    public void setHeight(float height) {
        if (maxHeight != 0.0f && height > maxHeight) height = maxHeight;
        if (minHeight != 0.0f && height < minHeight) height = minHeight;

        super.setHeight(height);
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
        componentFocusRect.setHeight(height);
        update();
    }

    public void applyResize() {
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    public void disposeOnRemoveFromStage() {
        addEventListener(Component.class, Event.REMOVE_FROM_STAGE, event -> dispose());
    }

    public void setMinWidth(float value) {
        minWidth = value;
        applyResize();
    }

    public float getMinWidth() {
        return minWidth;
    }

    public void setMinHeight(float value) {
        minHeight = value;
        applyResize();
    }

    public void setMinSize(float width, float height) {
        minWidth = width;
        minHeight = height;
        applyResize();
    }

    public void setMaxSize(float width, float height) {
        maxWidth = width;
        maxHeight = height;
        applyResize();
    }

    public Frame getFrame() {
        Container container = this;
        while (container != null) {
            if (container instanceof Frame frame) return frame;
            container = container.getParent();
        }

        return null;
    }


    @Override
    public void dispose() {
        super.dispose();

        for (int i = 0; i < getNumChildren(); i++) {
            SceneEntity child = getChild(i);
            if (child instanceof Component component) {
                component.dispose();
            }
        }


    }

}
