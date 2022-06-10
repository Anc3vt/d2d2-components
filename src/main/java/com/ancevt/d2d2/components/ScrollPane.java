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
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.DragUtil;

import java.util.ArrayList;
import java.util.List;

public class ScrollPane extends Component {

    private static final float DEFAULT_WIDTH = 300.0f;
    private static final float DEFAULT_HEIGHT = 300.0f;
    private static final float DEFAULT_ITEM_HEIGHT = 30.0f;

    public static final int DEFAULT_SCROLL_STEP = 4;

    private final PlainRect bg;
    private final Scrollbar scrollbar;
    private final List<Component> components;

    private int scrollPosition;
    private int scrollStep;

    private float itemHeight;

    public ScrollPane() {
        bg = new PlainRect(BACKGROUND_COLOR);
        bg.setAlpha(PANEL_BG_ALPHA);
        add(bg);

        scrollStep = DEFAULT_SCROLL_STEP;

        components = new ArrayList<>();

        scrollbar = new Scrollbar();
        scrollbar.addEventListener(ScrollPane.class, Event.CHANGE, this::scrollbar_change);
        add(scrollbar);

        addEventListener(ScrollPane.class, Event.RESIZE, this::this_resize);
        addEventListener(ScrollPane.class, InteractiveEvent.WHEEL, this::this_wheel);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setItemHeight(DEFAULT_ITEM_HEIGHT);
        setFocusRectEnabled(false);
    }

    private void this_resize(Event event) {
        bg.setSize(getWidth(), getHeight());
        scrollbar.setX(getWidth() - scrollbar.getWidth());
        scrollbar.setHeight(getHeight());
        rebuild();
    }

    private void scrollbar_change(Event event) {
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

    private void this_wheel(Event event) {
        var e = (InteractiveEvent) event;
        scroll(-e.getDelta() * scrollStep);
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
        component.removeEventListener(ScrollPane.class, Event.REMOVE);
        component.addEventListener(ScrollPane.class, Event.REMOVE, event -> focus());
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
        for (int i = scrollPosition; y < getHeight() && i < components.size(); i++) {
            if (i < 0) continue;
            Component item = components.get(i);
            add(item, 0, y);
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
            add(scrollbar);
        }

        scrollbar.setRectLength(getHeight() * val);
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        stage.setBackgroundColor(Color.DARK_GRAY);

        DebugPanel.setEnabled(true);
        ComponentAssets.init();

        ScrollPane scrollPane = new ScrollPane();

        scrollPane.setWidth(400);
        scrollPane.getPadding().setRight(10);

        DragUtil.enableDrag(scrollPane);

        stage.add(scrollPane, 100, 100);

        for (int i = 0; i < 10; i++) {
            Button button = new Button("Test " + i);
            button.setWidth(200);
            scrollPane.addScrollableItem(button);
        }

        DebugPanel.show("test", "").ifPresent(debugPanel -> {
            debugPanel.addButton("+", () -> {
                Button button = new Button("test" + (int) (Math.random() * 100));
                scrollPane.addScrollableItem(button);
            });
            debugPanel.addButton("-", () -> {
                scrollPane.removeScrollableItem(0);
            });
            debugPanel.addButton("0", () -> {
                scrollPane.setScrollPosition(0);
            });
        });

        D2D2.loop();
        DebugPanel.saveAll();
    }
}
