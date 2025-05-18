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

package com.ancevt.d2d2.components.menu;

import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.scene.Node;
import com.ancevt.d2d2.scene.shape.RectangleShape;

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
        item.addEventListener(this, InputEvent.MouseHover.class, this::item_hover);
        item.addEventListener(this, InputEvent.MouseDown.class, this::item_down);
        items.add(item);
        return this;
    }

    public Menu addItem(Object text, Runnable action) {
        MenuItem item = new MenuItem(this);
        item.setText(text);
        item.addEventListener(this, InputEvent.MouseHover.class, this::item_hover);
        item.addEventListener(this, InputEvent.MouseDown.class, this::item_down);
        items.add(item);
        item.setAction(action);
        return this;
    }

    private void item_down(InputEvent.MouseDown event) {
        MenuItem item = event.targetAs(MenuItem.class);
        if (item.getAction() != null) item.getAction().run();
    }

    private void item_hover(InputEvent.MouseHover event) {
        MenuItem item = event.targetAs(MenuItem.class);
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
        stage().addChild(this, x, y);

        if (getX() + getWidth() > stage().getWidth()) {
            setX(stage().getWidth() - getWidth());
        }

        if (getY() + getHeight() > stage().getHeight()) {
            setY(y - getHeight());
        }

        stage().addEventListener(this, InputEvent.MouseDown.class, event -> {
            deactivate(this);
            stage().removeEventListener(this, InputEvent.MouseDown.class);
        });

        activeRootMenu = this;

        return this;
    }

    public Menu activate(MenuItem fromItem) {
        update();
        float x = fromItem.getGlobalX() + fromItem.getWidth();
        float y = fromItem.getGlobalY();
        stage().addChild(this, x, y);

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
        displayedItems.forEach(Node::removeFromParent);
        displayedItems.clear();

        float y = 0;
        for (MenuItem item : items) {
            if (item == null) {
                RectangleShape separatorBg = new RectangleShape(getWidth(), 6, MENU_BACKGROUND_COLOR);
                addChild(separatorBg, 0, y);
                RectangleShape separator = new RectangleShape(getWidth() - 8, 2, MENU_SEPARATOR_COLOR);
                addChild(separator, 4, y + 2);
                y += 6;
            } else {
                addChild(item, 0, y);
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
