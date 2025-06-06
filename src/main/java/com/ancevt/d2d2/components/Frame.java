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
import com.ancevt.d2d2.event.NodeEvent;
import com.ancevt.d2d2.event.StageEvent;
import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.interactive.Combined9Sprites;
import com.ancevt.d2d2.scene.interactive.DragUtil;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import lombok.Getter;

import static com.ancevt.d2d2.D2D2.getStage;

public class Frame extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private static final float DEFAULT_TITLE_HEIGHT = 25.0f;

    private static final float RESIZE_SPREAD = 8.0f;

    private final FrameTitle frameTitle;
    private final RectangleShape bg1;
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
        bg1 = new RectangleShape();
        bg1.setAlpha(backgroundAlpha);
        bg1.setColor(colorBackground1);
        addChild(bg1);

        frameTitle = new FrameTitle();
        frameTitle.setTextColor(Component.INACTIVE_FRAME_TITLE_COLOR);
        addChild(frameTitle);

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
        addChild(borders);

        setComponentFocusRectVisibleEnabled(false);

        addEventListener(Frame.class, NodeEvent.AddToScene.class, this::this_addToStage);
        addEventListener(Frame.class, CommonEvent.Resize.class, this::this_resize);
        addEventListener(Frame.class, CommonEvent.Activate.class, this::this_activate);
        addEventListener(Frame.class, CommonEvent.Deactivate.class, this::this_deactivate);

        addChild(contentPanel, getPadding().getLeft(), frameTitle.getY() + frameTitle.getHeight() + getPadding().getTop());

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        setDragEnabled(true);
    }


    public float getTitleHeight() {
        return frameTitle.getHeight();
    }

    private void this_activate(CommonEvent.Activate event) {
        frameTitle.setTextColor(Component.ACTIVE_FRAME_TITLE_COLOR);
    }

    private void this_deactivate(CommonEvent.Deactivate event) {
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
            addEventListener("manualResize", StageEvent.Tick.class, this::this_manualResizeEachFrame);
            addEventListener("manualResize", InputEvent.MouseDown.class, this::this_manualResizeDown);
            addEventListener("manualResize", InputEvent.MouseUp.class, this::this_manualResizeUp);
            addEventListener("manualResize", InputEvent.MouseDrag.class, this::this_manualResizeDrag);
            addEventListener("manualResize", InputEvent.MouseOut.class, this::this_manualResizeOut);
        } else {
            removeEventListener("manualResize", StageEvent.Tick.class);
            removeEventListener("manualResize", InputEvent.MouseDown.class);
            removeEventListener("manualResize", InputEvent.MouseUp.class);
            removeEventListener("manualResize", InputEvent.MouseDrag.class);
            removeEventListener("manualResize", InputEvent.MouseOut.class);
        }
    }

    private void this_manualResizeOut(InputEvent.MouseOut event) {
        Cursor.switchToIdle();
        frameTitle.setInteractionEnabled(true);
    }

    private void this_manualResizeDown(InputEvent.MouseDown event) {
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
            dispatchEvent(ResizeStartEvent.create());
        }
    }

    private void this_manualResizeDrag(Event event) {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        if (manualResizeRight) {
            setWidth(mouseX - getGlobalX() + 1);
        }
        if (manualResizeBottom) {
            setHeight(mouseY - getGlobalY() + 1);
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
            dispatchEvent(ResizeCompleteEvent.create());
        }
    }

    private void this_manualResizeEachFrame(Event event) {
        if (isResizeCursorOnBottomRight()) {
            Cursor.switchToResize(45.0f);
            frameTitle.setInteractionEnabled(true);
        } else if (isResizeCursorOnBottomLeft()) {
            Cursor.switchToResize(140.0f);
            frameTitle.setInteractionEnabled(true);
        } else if (isResizeCursorOnTopLeft()) {
            Cursor.switchToResize(220.f);
            frameTitle.setInteractionEnabled(false);
        } else if (isResizeCursorOnTopRight()) {
            Cursor.switchToResize(320.0f);
            frameTitle.setInteractionEnabled(false);
        } else if (isResizeCursorOnRight()) {
            Cursor.switchToResize(0.0f);
            frameTitle.setInteractionEnabled(true);
        } else if (isResizeCursorOnBottom()) {
            Cursor.switchToResize(90.0f);
            frameTitle.setInteractionEnabled(true);
        } else if (isResizeCursorOnLeft()) {
            frameTitle.setInteractionEnabled(true);
            Cursor.switchToResize(180.0f);
        } else if (isResizeCursorOnTop()) {
            Cursor.switchToResize(270.0f);
            frameTitle.setInteractionEnabled(false);
        } else if (isResizeCursorInCenter()) {
            Cursor.switchToIdle();
            frameTitle.setInteractionEnabled(true);
        }
    }

    private boolean isResizeCursorInCenter() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getGlobalX() + RESIZE_SPREAD &&
                mouseX < getGlobalX() + getWidth() - RESIZE_SPREAD &&
                mouseY > getGlobalY() + RESIZE_SPREAD &&
                mouseY < getGlobalY() + getHeight() - RESIZE_SPREAD;

    }

    private boolean isResizeCursorOnRight() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseY > getGlobalY() && mouseY < getGlobalY() + getHeight() &&
                mouseX >= getGlobalX() + getWidth() - RESIZE_SPREAD && mouseX <= getGlobalX() + getWidth();
    }

    private boolean isResizeCursorOnBottomRight() {
        return isResizeCursorOnBottom() && isResizeCursorOnRight();
    }

    private boolean isResizeCursorOnBottom() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getGlobalX() && mouseX < getGlobalX() + getWidth() &&
                mouseY >= getGlobalY() + getHeight() - RESIZE_SPREAD && mouseY <= getGlobalY() + getHeight();
    }

    private boolean isResizeCursorOnBottomLeft() {
        return isResizeCursorOnBottom() && isResizeCursorOnLeft();
    }

    private boolean isResizeCursorOnLeft() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseY > getGlobalY() && mouseY < getGlobalY() + getHeight() &&
                mouseX > getGlobalX() && mouseX < getGlobalX() + RESIZE_SPREAD;
    }

    private boolean isResizeCursorOnTopLeft() {
        return isResizeCursorOnTop() && isResizeCursorOnLeft();
    }

    private boolean isResizeCursorOnTop() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();

        return mouseX > getGlobalX() && mouseX < getGlobalX() + getWidth() &&
                mouseY > getGlobalY() && mouseY < getGlobalY() + RESIZE_SPREAD;
    }

    private boolean isResizeCursorOnTopRight() {
        return isResizeCursorOnTop() && isResizeCursorOnRight();
    }

    private void this_addToStage(Event event) {
        removeEventListener(Frame.class, NodeEvent.AddToScene.class);
        FrameManager.getInstance().activateFrame(this);
        //center();
    }

    public void center() {
        this.setPosition((getStage().getWidth() - getWidth()) / 2, (getStage().getHeight() - getHeight()) / 2);
    }

    private void this_resize(Event event) {
        frameTitle.setSize(getWidth(), DEFAULT_TITLE_HEIGHT);

        frameTitle.setAlpha(backgroundAlpha);

        bg1.setSize(getWidth(), getHeight() - frameTitle.getHeight());
        bg1.setPosition(0, frameTitle.getHeight());
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

    @EventPooled
    public static class ResizeStartEvent extends Event {
        public static ResizeStartEvent create() {
            return EventPool.obtain(ResizeStartEvent.class);
        }
    }

    @EventPooled
    public static class ResizeCompleteEvent extends Event {
        public static ResizeCompleteEvent create() {
            return EventPool.obtain(ResizeCompleteEvent.class);
        }
    }

}

