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
package com.ancevt.d2d2.components.menu;

import com.ancevt.d2d2.display.shape.RectangleShape;
import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2.D2D2.stage;

public class Menu extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private static Menu activeRootMenu;

    private final List<MenuItem> items;
    private final List<MenuItem> displayedItems;

    private Menu activeChildMenu;

    private boolean backwardDirection;

    public Menu() {
        items = new ArrayList<>();
        displayedItems = new ArrayList<>();

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Menu addItem(Object text, Menu childMenu) {
        MenuItem item = new MenuItem(this);
        item.setText(text);
        item.setChildMenu(childMenu);
        item.addEventListener(this, InteractiveEvent.HOVER, this::item_hover);
        item.addEventListener(this, InteractiveEvent.DOWN, this::item_down);
        items.add(item);
        return this;
    }

    public Menu addItem(Object text, Runnable action) {
        MenuItem item = new MenuItem(this);
        item.setText(text);
        item.addEventListener(this, InteractiveEvent.HOVER, this::item_hover);
        item.addEventListener(this, InteractiveEvent.DOWN, this::item_down);
        items.add(item);
        item.setAction(action);
        return this;
    }

    private void item_down(Event event) {
        MenuItem item = (MenuItem) event.getSource();
        if (item.getAction() != null) item.getAction().run();
    }

    private void item_hover(Event event) {
        MenuItem item = (MenuItem) event.getSource();
        if (item.hasChildMenu()) {
            if (activeChildMenu != null) deactivate(activeChildMenu);
            activeChildMenu = item.getChildMenu().activate(item);
        }
    }

    public Menu addSeparator() {
        items.add(MenuItem.SEPARATOR);
        return this;
    }

    public Menu activate() {
        if (activeChildMenu != null) {
            deactivate(activeRootMenu);
        }

        update();
        float x = Mouse.getX();
        float y = Mouse.getY() + 1;
        stage().add(this, x, y);

        if (getX() + getWidth() > stage().getWidth()) {
            setX(stage().getWidth() - getWidth());
        }

        if (getY() + getHeight() > stage().getHeight()) {
            setY(y - getHeight());
        }

        stage().addEventListener(this, InteractiveEvent.DOWN, event -> {
            deactivate(this);
            stage().removeEventListener(this, InteractiveEvent.DOWN);
        });

        activeRootMenu = this;

        return this;
    }

    public Menu activate(MenuItem fromItem) {
        update();
        float x = fromItem.getAbsoluteX() + fromItem.getWidth();
        float y = fromItem.getAbsoluteY();
        stage().add(this, x, y);

        if (getY() + getHeight() > stage().getHeight()) {
            setY(y - getHeight() + MenuItem.HEIGHT);
        }

        if (getX() + getWidth() > stage().getWidth() || fromItem.getParentMenu().backwardDirection) {
            setX(fromItem.getParentMenu().getX() - getWidth());
            backwardDirection = true;
        }

        return this;
    }

    private static void deactivate(Menu menu) {
        menu.removeFromParent();
        menu.items.forEach(menuItem -> {
            if (menuItem != MenuItem.SEPARATOR && menuItem.hasChildMenu()) {
                deactivate(menuItem.getChildMenu());
            }
        });
    }

    public static Menu createMenu() {
        return new Menu();
    }

    @Override
    public void update() {
        displayedItems.forEach(IDisplayObject::removeFromParent);
        displayedItems.clear();

        float y = 0;
        for (MenuItem item : items) {
            if (item == null) {
                RectangleShape separatorBg = new RectangleShape(getWidth(), 6, MENU_BACKGROUND_COLOR);
                add(separatorBg, 0, y);
                RectangleShape separator = new RectangleShape(getWidth() - 8, 2, MENU_SEPARATOR_COLOR);
                add(separator, 4, y + 2);
                y += 6;
            } else {
                add(item, 0, y);
                displayedItems.add(item);
                y += item.getHeight();
            }
        }

        interactiveArea.setHeight(y);
    }

    @Override
    public void dispose() {
        super.dispose();
        items.forEach(MenuItem::dispose);
    }

}
