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

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.interactive.Combined9Sprites;
import com.ancevt.d2d2.display.interactive.DragUtil;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.ancevt.d2d2.D2D2.stage;

public class Frame extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private static final float DEFAULT_TITLE_HEIGHT = 25.0f;

    private static final float RESIZE_SPREAD = 8.0f;

    private final FrameTitle frameTitle;
    private final PlainRect bg1;
    private final Combined9Sprites borders;

    @Getter
    private Color colorBackground1 = Color.of(0x161A1D);
    @Getter
    private Color colorBackground2 = Color.of(0x000306);
    @Getter
    private Color colorBorder = Color.of(0x7E7E7E);
    @Getter
    private float backgroundAlpha = 0.9f;
    @Getter
    private boolean dragEnabled;

    @Getter
    private boolean manualResizable;
    private boolean manualResizeRight;
    private boolean manualResizeBottom;
    private boolean manualResizeLeft;
    private float manualResizeX;
    private float manualResizeWidth;
    private boolean manualResizeTop;
    private float manualResizeY;
    private float manualResizeHeight;
    private boolean manualResizingNow;

    @Getter
    private final Component contentPanel = new Panel();

    public Frame() {
        bg1 = new PlainRect();
        bg1.setAlpha(backgroundAlpha);
        bg1.setColor(colorBackground1);
        add(bg1);

        frameTitle = new FrameTitle();
        frameTitle.setTextColor(Component.INACTIVE_FRAME_TITLE_COLOR);
        add(frameTitle);

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

        setComponentFocusRectVisibleEnabled(false);

        addEventListener(Frame.class, Event.ADD_TO_STAGE, this::this_addToStage);
        addEventListener(Frame.class, Event.RESIZE, this::this_resize);
        addEventListener(Frame.class, ComponentEvent.ACTIVATE, this::this_activate);
        addEventListener(Frame.class, ComponentEvent.DEACTIVATE, this::this_deactivate);

        add(contentPanel, getPadding().getLeft(), frameTitle.getY() + frameTitle.getHeight() + getPadding().getTop());

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        setDragEnabled(true);
    }


    public float getTitleHeight() {
        return frameTitle.getHeight();
    }

    private void this_activate(Event event) {
        frameTitle.setTextColor(Component.ACTIVE_FRAME_TITLE_COLOR);
    }

    private void this_deactivate(Event event) {
        frameTitle.setTextColor(Component.INACTIVE_FRAME_TITLE_COLOR);
    }

    public void setDragEnabled(boolean value) {
        if (dragEnabled == value) return;
        this.dragEnabled = value;

        if (dragEnabled) {
            DragUtil.enableDrag(this, frameTitle);
        } else {
            DragUtil.disableDrag(this, frameTitle);
        }
    }

    public void setManualResizable(boolean manualResizable) {
        if (manualResizable == this.manualResizable) return;
        this.manualResizable = manualResizable;

        if (manualResizable) {
            addEventListener("manualResize", Event.ENTER_FRAME, this::this_manualResizeEachFrame);
            addEventListener("manualResize", InteractiveEvent.DOWN, this::this_manualResizeDown);
            addEventListener("manualResize", InteractiveEvent.UP, this::this_manualResizeUp);
            addEventListener("manualResize", InteractiveEvent.DRAG, this::this_manualResizeDrag);
            addEventListener("manualResize", InteractiveEvent.OUT, this::this_manualResizeOut);
        } else {
            removeEventListener("manualResize", Event.ENTER_FRAME);
            removeEventListener("manualResize", InteractiveEvent.DOWN);
            removeEventListener("manualResize", InteractiveEvent.UP);
            removeEventListener("manualResize", InteractiveEvent.DRAG);
            removeEventListener("manualResize", InteractiveEvent.OUT);
        }
    }

    private void this_manualResizeOut(Event event) {
        Cursor.switchToIdle();
        frameTitle.setEnabled(true);
    }

    private void this_manualResizeDown(Event event) {
        manualResizeRight = isResizeCursorOnRight();
        manualResizeBottom = isResizeCursorOnBottom();
        if (isResizeCursorOnLeft()) {
            manualResizeX = Mouse.getX();
            manualResizeWidth = getWidth();
            manualResizeLeft = isResizeCursorOnLeft();
        }
        if (isResizeCursorOnTop()) {
            manualResizeY = Mouse.getY();
            manualResizeHeight = getHeight();
            manualResizeTop = isResizeCursorOnTop();
        }

        if (!isResizeCursorInCenter()) {
            manualResizingNow = true;
            dispatchEvent(FrameEvent.builder().type(FrameEvent.RESIZE_START).build());
        }
    }

    private void this_manualResizeDrag(Event event) {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        if (manualResizeRight) {
            setWidth(mouseX - getAbsoluteX() + 1);
        }
        if (manualResizeBottom) {
            setHeight(mouseY - getAbsoluteY() + 1);
        }
        if (manualResizeLeft) {
            float oldWidth = getWidth();
            setWidth(manualResizeWidth - (mouseX - manualResizeX));
            if (oldWidth != getWidth()) setX(mouseX - 1);
        }
        if (manualResizeTop) {
            float oldHeight = getHeight();
            setHeight(manualResizeHeight - (mouseY - manualResizeY));
            if (oldHeight != getHeight()) setY(mouseY - 1);
        }

        //dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    private void this_manualResizeUp(Event event) {
        manualResizeRight = manualResizeBottom = manualResizeLeft = manualResizeTop = false;
        Cursor.switchToIdle();

        if (manualResizingNow) {
            dispatchEvent(FrameEvent.builder().type(FrameEvent.RESIZE_COMPLETE).build());
        }
    }

    private void this_manualResizeEachFrame(Event event) {
        if (isResizeCursorOnBottomRight()) {
            Cursor.switchToResize(45.0f);
            frameTitle.setEnabled(true);
        } else if (isResizeCursorOnBottomLeft()) {
            Cursor.switchToResize(140.0f);
            frameTitle.setEnabled(true);
        } else if (isResizeCursorOnTopLeft()) {
            Cursor.switchToResize(220.f);
            frameTitle.setEnabled(false);
        } else if (isResizeCursorOnTopRight()) {
            Cursor.switchToResize(320.0f);
            frameTitle.setEnabled(false);
        } else if (isResizeCursorOnRight()) {
            Cursor.switchToResize(0.0f);
            frameTitle.setEnabled(true);
        } else if (isResizeCursorOnBottom()) {
            Cursor.switchToResize(90.0f);
            frameTitle.setEnabled(true);
        } else if (isResizeCursorOnLeft()) {
            frameTitle.setEnabled(true);
            Cursor.switchToResize(180.0f);
        } else if (isResizeCursorOnTop()) {
            Cursor.switchToResize(270.0f);
            frameTitle.setEnabled(false);
        } else if (isResizeCursorInCenter()) {
            Cursor.switchToIdle();
            frameTitle.setEnabled(true);
        }
    }

    private boolean isResizeCursorInCenter() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getAbsoluteX() + RESIZE_SPREAD &&
            mouseX < getAbsoluteX() + getWidth() - RESIZE_SPREAD &&
            mouseY > getAbsoluteY() + RESIZE_SPREAD &&
            mouseY < getAbsoluteY() + getHeight() - RESIZE_SPREAD;

    }

    private boolean isResizeCursorOnRight() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseY > getAbsoluteY() && mouseY < getAbsoluteY() + getHeight() &&
            mouseX >= getAbsoluteX() + getWidth() - RESIZE_SPREAD && mouseX <= getAbsoluteX() + getWidth();
    }

    private boolean isResizeCursorOnBottomRight() {
        return isResizeCursorOnBottom() && isResizeCursorOnRight();
    }

    private boolean isResizeCursorOnBottom() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getAbsoluteX() && mouseX < getAbsoluteX() + getWidth() &&
            mouseY >= getAbsoluteY() + getHeight() - RESIZE_SPREAD && mouseY <= getAbsoluteY() + getHeight();
    }

    private boolean isResizeCursorOnBottomLeft() {
        return isResizeCursorOnBottom() && isResizeCursorOnLeft();
    }

    private boolean isResizeCursorOnLeft() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseY > getAbsoluteY() && mouseY < getAbsoluteY() + getHeight() &&
            mouseX > getAbsoluteX() && mouseX < getAbsoluteX() + RESIZE_SPREAD;
    }

    private boolean isResizeCursorOnTopLeft() {
        return isResizeCursorOnTop() && isResizeCursorOnLeft();
    }

    private boolean isResizeCursorOnTop() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getAbsoluteX() && mouseX < getAbsoluteX() + getWidth() &&
            mouseY > getAbsoluteY() && mouseY < getAbsoluteY() + RESIZE_SPREAD;
    }

    private boolean isResizeCursorOnTopRight() {
        return isResizeCursorOnTop() && isResizeCursorOnRight();
    }

    private void this_addToStage(Event event) {
        removeEventListener(Frame.class, Event.ADD_TO_STAGE);
        FrameManager.getInstance().activateFrame(this);
        center();
    }

    public void center() {
        setXY((stage().getWidth() - getWidth()) / 2, (stage().getHeight() - getHeight()) / 2);
    }

    private void this_resize(Event event) {
        frameTitle.setSize(getWidth(), DEFAULT_TITLE_HEIGHT);

        frameTitle.setAlpha(backgroundAlpha);

        bg1.setSize(getWidth(), getHeight() - frameTitle.getHeight());
        bg1.setXY(0, frameTitle.getHeight());
        bg1.setColor(colorBackground1);
        bg1.setAlpha(backgroundAlpha);

        contentPanel.setSize(
            getWidth() - getPadding().getRight() * 2,
            getHeight() - getTitleHeight() - getPadding().getBottom() * 2
        );

        borders.setSize(getWidth(), getHeight());
    }

    public void setColorBackground1(Color colorBackground1) {
        this.colorBackground1 = colorBackground1;
        this_resize(null);
    }

    public void setColorBackground2(Color colorBackground2) {
        this.colorBackground2 = colorBackground2;
        this_resize(null);
    }

    public void setColorBorder(Color colorBorder) {
        this.colorBorder = colorBorder;
        this_resize(null);
    }

    public void setBackgroundAlpha(float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        this_resize(null);
    }

    public void setTitle(String title) {
        frameTitle.setText(title);
        this_resize(null);
    }

    public String getTitle() {
        return frameTitle.getText();
    }

    @Override
    public void update() {

    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class FrameEvent extends Event {
        public static final String RESIZE_START = "frameResizeStart";
        public static final String RESIZE_COMPLETE = "frameResizeComplete";
    }

}

