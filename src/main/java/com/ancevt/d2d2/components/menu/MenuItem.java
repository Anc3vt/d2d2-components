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
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.components.SpriteFactory;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.text.BitmapText;

public class MenuItem extends Component {

    public static final MenuItem SEPARATOR = null;

    static final float HEIGHT = 30.0f;

    private static final Runnable STUB_ACTION = () -> {
    };

    final RectangleShape bg;
    private final BitmapText bitmapText;

    private Sprite childMenuArrow;
    private Menu childMenu;
    private Runnable action;
    private final Menu parentMenu;

    public MenuItem(Menu parentMenu) {
        this.parentMenu = parentMenu;
        bg = new RectangleShape();
        bg.setColor(MENU_BACKGROUND_COLOR);
        addChild(bg);

        action = STUB_ACTION;

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getFontMiddle());
        bitmapText.setMulticolor(true);
        addChild(bitmapText, 10, (HEIGHT - bitmapText.getTextHeight()) / 2);

        addEventListener(CommonEvent.Resize.class, this::this_resize);
        addEventListener(InputEvent.MouseHover.class, this::this_hover);
        addEventListener(InputEvent.MouseOut.class, this::this_out);

        setSize(parentMenu.getWidth(), HEIGHT);
    }

    private void this_hover(InputEvent.MouseHover event) {
        bg.setColor(MENU_HOVER_BACKGROUND_COLOR);
    }

    private void this_out(InputEvent.MouseOut event) {
        bg.setColor(MENU_BACKGROUND_COLOR);
    }

    private void this_resize(CommonEvent.Resize event) {
        bg.setSize(getWidth(), getHeight());
    }

    public void setText(Object text) {
        this.bitmapText.setText("" + text);
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
            childMenuArrow = SpriteFactory.createSpriteByTextureKey(ComponentAssets.MENU_CHILD_ARROW);
            childMenuArrow.setColor(Color.LIGHT_GRAY);
            addChild(childMenuArrow);
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
            childMenuArrow.setPosition(getWidth() - childMenuArrow.getWidth() - 5, (HEIGHT - childMenuArrow.getHeight()) / 2);
        }
        bitmapText.setWidth(getWidth());
    }
}
