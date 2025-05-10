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

import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.SceneEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Group;
import com.ancevt.d2d2.scene.Node;
import com.ancevt.d2d2.scene.interactive.InteractiveGroup;
import com.ancevt.d2d2.scene.shape.BorderedRectangle;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.ancevt.d2d2.D2D2.root;

abstract public class Component extends InteractiveGroup {

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

        addEventListener(Component.class, InputEvent.FocusIn.class, this::this_focusIn);
        addEventListener(Component.class, InputEvent.FocusOut.class, this::this_focusOut);

        padding = new Padding(DEFAULT_PADDING_LEFT, DEFAULT_PADDING_TOP, DEFAULT_PADDING_RIGHT, DEFAULT_PADDING_BOTTOM);

        setTabbingEnabled(false);
    }

    private void this_focusIn(InputEvent.FocusIn e) {
        if (componentFocusRectEnabled && !e.byMouseDown() && !componentFocusRect.hasParent())
            addChild(componentFocusRect);

        FrameManager.getInstance().activateFrame(getFrame());
    }

    private void this_focusOut(InputEvent.FocusOut e) {
        componentFocusRect.removeFromParent();
    }

    public void setTooltip(Tooltip tooltip) {
        if (this.tooltip != null)
            this.tooltip.removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class);

        this.tooltip = tooltip;

        removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseHover.class);
        removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class);

        if (tooltip != null) {
            addEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseHover.class, event -> {

                AtomicBoolean tooltipCancelHover = new AtomicBoolean(false);

                removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseDown.class);
                addEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseDown.class, event1 -> {
                    tooltipCancelHover.set(true);
                    removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseDown.class);
                    tooltip.removeFromParent();
                });

                Timer.setTimeout(1000, t -> {
                    if (!tooltipCancelHover.get() && isHovering()) {
                        root().addChild(tooltip, Mouse.getX(), Mouse.getY());
                        if (tooltip.getX() + tooltip.getWidth() > root().getWidth()) {
                            tooltip.setX(root().getWidth() - tooltip.getWidth());
                        }

                        if (tooltip.getY() + tooltip.getHeight() > root().getHeight()) {
                            tooltip.setY(root().getHeight() - tooltip.getHeight());
                        }

                        tooltip.removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class);
                        tooltip.addEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class, event1 -> {
                            tooltip.removeFromParent();
                        });
                    }
                });
            });

            addEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class, event -> {
                Timer.setTimeout(1000, t -> {
                    if (!isHovering() && !tooltip.isHovering()) {
                        tooltip.removeEventListener(Component.class + "" + Tooltip.class, InputEvent.MouseOut.class);
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
        dispatchEvent(CommonEvent.Resize.create(width, height));
        componentFocusRect.setSize(width, height);
        update();
    }

    @Override
    public void setWidth(float width) {
        if (maxWidth != 0.0f && width > maxWidth) width = maxWidth;
        if (minWidth != 0.0f && width < minWidth) width = minWidth;

        super.setWidth(width);
        dispatchEvent(CommonEvent.Resize.create(width, getHeight()));
        componentFocusRect.setWidth(width);
        update();
    }

    @Override
    public void setHeight(float height) {
        if (maxHeight != 0.0f && height > maxHeight) height = maxHeight;
        if (minHeight != 0.0f && height < minHeight) height = minHeight;

        super.setHeight(height);
        dispatchEvent(CommonEvent.Resize.create(getWidth(), height));
        componentFocusRect.setHeight(height);
        update();
    }

    public void applyResize() {
        dispatchEvent(CommonEvent.Resize.create(getWidth(), getHeight()));
    }

    public void disposeOnRemoveFromStage() {
        addEventListener(Component.class, SceneEvent.RemoveFromScene.class, event -> dispose());
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
        Group group = this;
        while (group != null) {
            if (group instanceof Frame frame) return frame;
            group = group.getParent();
        }

        return null;
    }


    @Override
    public void dispose() {
        super.dispose();

        for (int i = 0; i < getNumChildren(); i++) {
            Node child = getChild(i);
            if (child instanceof Component component) {
                component.dispose();
            }
        }


    }

}
