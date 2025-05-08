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
import com.ancevt.d2d2.scene.shape.RectangleShape;

import java.util.ArrayList;
import java.util.List;

public class ScrollPane extends Component {

    private static final float DEFAULT_WIDTH = 300.0f;
    private static final float DEFAULT_HEIGHT = 300.0f;
    private static final float DEFAULT_ITEM_HEIGHT = 30.0f;

    public static final int DEFAULT_SCROLL_STEP = 4;

    private final RectangleShape bg;
    private final Scrollbar scrollbar;
    private final List<Component> components;

    private int scrollPosition;
    private int scrollStep;

    private float itemHeight;

    public ScrollPane() {
        bg = new RectangleShape(1, 1, BACKGROUND_COLOR);
        bg.setAlpha(PANEL_BG_ALPHA);
        addChild(bg);

        scrollStep = DEFAULT_SCROLL_STEP;

        components = new ArrayList<>();

        scrollbar = new Scrollbar();
        scrollbar.addEventListener(ScrollPane.class, CommonEvent.Change.class, this::scrollbar_change);
        addChild(scrollbar);

        addEventListener(ScrollPane.class, CommonEvent.Resize.class, this::this_resize);
        addEventListener(ScrollPane.class, InputEvent.MouseWheel.class, this::this_wheel);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setItemHeight(DEFAULT_ITEM_HEIGHT);
        setComponentFocusRectVisibleEnabled(false);

        setPushEventsUp(true);
        scrollbar.setPushEventsUp(true);
    }

    private void this_resize(CommonEvent.Resize event) {
        bg.setSize(getWidth(), getHeight());
        scrollbar.setX(getWidth() - scrollbar.getWidth());
        scrollbar.setHeight(getHeight());
        rebuild();
    }

    private void scrollbar_change(CommonEvent.Change event) {
        int position = (int) ((components.size() - getHeight() / itemHeight) * scrollbar.getScrollValue());
        setScrollPositionInternal(position);
        rebuild();
    }

    private void setScrollPositionInternal(int scrollPosition) {
        this.scrollPosition = scrollPosition;

        if (this.scrollPosition < 0) {
            this.scrollPosition = 0;
        }

        int max = (int) (components.size() - getHeight() / itemHeight);
        if (this.scrollPosition >= max) {
            this.scrollPosition = max;
        }
    }

    private void this_wheel(InputEvent.MouseWheel event) {
        scroll(-event.delta() * scrollStep);
    }

    public void clear() {
        components.clear();
        rebuild();
    }

    public void setBackgroundVisible(boolean backgroundVisible) {
        bg.setVisible(backgroundVisible);
    }

    public boolean isBackgroundVisible() {
        return bg.isVisible();
    }

    public void setScrollStep(int scrollStep) {
        this.scrollStep = scrollStep;
    }

    public int getScrollStep() {
        return scrollStep;
    }

    public void scroll(int step) {
        setScrollPosition(getScrollPosition() + step);

        float pos = scrollPosition;
        float count = components.size();

        float value = pos / (count - getHeight() / itemHeight);

        scrollbar.setScrollValue(value);
    }

    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
        rebuild();
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public void addScrollableItem(Component component) {
        components.add(component);
        component.removeEventListener(ScrollPane.class, NodeEvent.Remove.class);
        component.addEventListener(ScrollPane.class, NodeEvent.Remove.class, event -> focus());
        setScrollPosition(Integer.MAX_VALUE);
        scrollbar.setScrollValue(1.0f);
        rebuild();
    }

    public void removeScrollableItem(Component component) {
        components.remove(component);
        setScrollPosition(0);
        rebuild();
    }

    public void removeScrollableItem(int index) {
        components.remove(index).removeFromParent();
        setScrollPosition(0);
        rebuild();
    }

    public Component getScrollableItem(int index) {
        return components.get(index);
    }

    public int getScrollableItemCount() {
        return components.size();
    }

    public float getTotalScrollableHeight() {
        return components.size() * itemHeight;
    }

    public void setScrollPosition(int scrollPosition) {
        setScrollPositionInternal(scrollPosition);

        scrollbar.setScrollValue((float) this.scrollPosition / (float) components.size());

        rebuild();
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public void update() {
        rebuild();
    }

    private void rebuild() {
        components.forEach(Component::removeFromParent);

        float y = 0.0f;
        for (int i = scrollPosition; y < getHeight() - itemHeight && i < components.size(); i++) {
            if (i < 0) continue;
            Component item = components.get(i);
            addChild(item, 0, y);
            item.move(getPadding().getLeft(), getPadding().getTop());
            item.setSize(
                    getWidth() - getPadding().getRight() - getPadding().getLeft(),
                    getItemHeight() - getPadding().getBottom() - getPadding().getTop()
            );
            y += itemHeight;
        }

        float val = getHeight() / getTotalScrollableHeight();

        scrollbar.removeFromParent();
        if (val < 1.0f) {
            addChild(scrollbar);
        }

        scrollbar.setRectLength(getHeight() * val);
    }
}
