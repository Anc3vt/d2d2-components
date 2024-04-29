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

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.SpriteFactory;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;

public class MenuItem extends Component {

    public static final MenuItem SEPARATOR = null;

    static final float HEIGHT = 30.0f;

    private static final Runnable STUB_ACTION = () -> {
    };

    final PlainRect bg;
    private final BitmapText bitmapText;

    private Sprite childMenuArrow;
    private Menu childMenu;
    private Runnable action;
    private final Menu parentMenu;

    public MenuItem(Menu parentMenu) {
        this.parentMenu = parentMenu;
        bg = new PlainRect();
        bg.setColor(MENU_BACKGROUND_COLOR);
        add(bg);

        action = STUB_ACTION;

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        bitmapText.setMulticolor(true);
        add(bitmapText, 10, (HEIGHT - bitmapText.getTextHeight()) / 2);

        addEventListener(Event.RESIZE, this::this_resize);
        addEventListener(InteractiveEvent.HOVER, this::this_hover);
        addEventListener(InteractiveEvent.OUT, this::this_out);

        setSize(parentMenu.getWidth(), HEIGHT);
    }

    private void this_hover(Event event) {
        bg.setColor(MENU_HOVER_BACKGROUND_COLOR);
    }

    private void this_out(Event event) {
        bg.setColor(MENU_BACKGROUND_COLOR);
    }

    private void this_resize(Event event) {
        bg.setSize(getWidth(), getHeight());
    }

    public void setText(Object text) {
        bitmapText.setText("" + text);
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public Runnable getAction() {
        return action;
    }

    public void setChildMenu(Menu childMenu) {
        this.childMenu = childMenu;
        if (childMenuArrow != null) childMenuArrow.removeFromParent();

        if (childMenu != null) {
            childMenuArrow = SpriteFactory.createSprite(ComponentAssets.MENU_CHILD_ARROW);
            childMenuArrow.setColor(Color.LIGHT_GRAY);
            add(childMenuArrow);
            update();
        }
    }

    public Menu getChildMenu() {
        return childMenu;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public boolean hasChildMenu() {
        return childMenu != null;
    }

    @Override
    public void dispose() {
        super.dispose();
        childMenu.dispose();
    }

    @Override
    public void update() {
        if (childMenuArrow != null) {
            childMenuArrow.setXY(getWidth() - childMenuArrow.getWidth() - 5, (HEIGHT - childMenuArrow.getHeight()) / 2);
        }
        bitmapText.setWidth(getWidth());
    }
}
