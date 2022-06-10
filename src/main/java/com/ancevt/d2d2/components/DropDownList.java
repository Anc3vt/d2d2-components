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
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.Combined9Sprites;
import com.ancevt.d2d2.interactive.DragUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ancevt.d2d2.D2D2.init;
import static com.ancevt.d2d2.D2D2.loop;
import static com.ancevt.d2d2.D2D2.stage;

public class DropDownList<T> extends Component {

    private static final float DEFAULT_WIDTH = 150.0f;
    private static final float DEFAULT_HEIGHT = 24.0f;

    private final PlainRect bg;
    private final Combined9Sprites borders;

    private final BitmapText bitmapText;
    private final Sprite arrow;

    private final Map<T, Item<T>> itemMap;
    private final List<Item<T>> itemList;
    private final List<Item<T>> displayedItemList;
    private final ScrollPane scrollPane;

    private Item<T> selectedItem;

    private boolean open;

    public DropDownList() {
        itemList = new ArrayList<>();
        itemMap = new HashMap<>();
        displayedItemList = new ArrayList<>();
        scrollPane = new ScrollPane();
        scrollPane.setItemHeight(DEFAULT_HEIGHT);

        bg = new PlainRect(BACKGROUND_COLOR);
        add(bg);

        borders = new Combined9Sprites(
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_CENTER),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTexture(ComponentAssets.RECT_BORDER_9_SIDE_BOTTOM_RIGHT)
        );
        borders.setColor(FOREGROUND_COLOR);
        add(borders);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontSmall());
        add(bitmapText);

        arrow = new Sprite(ComponentAssets.DROP_DOWN_LIST_ARROW);
        add(arrow);

        addEventListener(Event.RESIZE, this::this_resize);
        addEventListener(InteractiveEvent.DOWN, this::this_down);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_resize(Event event) {
        borders.setSize(getWidth(), getHeight());
        bg.setSize(getWidth(), getHeight());
        arrow.setXY(getWidth() - arrow.getWidth() - 10, (getHeight() - arrow.getHeight()) / 2);
        bitmapText.setXY(10, (getHeight() - bitmapText.getTextHeight()) / 2);
        //bitmapText.setWidth(getWidth() - 10 - arrow.getWidth() - 10 - 10);
        bitmapText.setAutosize(true);
        cutText(bitmapText, getWidth());
    }

    private long oldTime;

    private void this_down(Event event) {
        if (Math.abs(oldTime - System.currentTimeMillis()) > 10) {
            if (open) {
                close();
            } else {
                open();
            }
        }
    }

    private void open() {
        oldTime = System.currentTimeMillis();
        scrollPane.clear();
        scrollPane.setItemHeight(Item.HEIGHT);
        scrollPane.setWidth(getWidth());
        scrollPane.setPadding(new Padding(0, 0, 0, 0));

        arrow.setScaleY(-1.0f);
        arrow.setXY(getWidth() - arrow.getWidth() - 10, (getHeight() - arrow.getHeight()) / 2 + arrow.getHeight());

        float y = 0;
        for (Item<T> item : itemList) {
            scrollPane.addScrollableItem(item);
            displayedItemList.add(item);
            y += Item.HEIGHT;
        }

        scrollPane.setScrollPosition(0);

        stage().add(scrollPane, getAbsoluteX(), getAbsoluteY() + getHeight());

        if (y > stage().getHeight()) {
            scrollPane.setY(0);
            scrollPane.setHeight(stage().getHeight());
        } else {
            scrollPane.setHeight(y);

            if (scrollPane.getY() + scrollPane.getHeight() > stage().getHeight()) {
                scrollPane.setY(getAbsoluteY() - scrollPane.getHeight());
            }

            if (scrollPane.getY() < 0) scrollPane.setY(0);
        }

        stage().removeEventListener(this, InputEvent.MOUSE_DOWN);
        stage().addEventListener(this, InputEvent.MOUSE_DOWN, event -> {
            var e = (InputEvent) event;

            if (e.getX() < scrollPane.getX() || e.getX() > scrollPane.getX() + scrollPane.getWidth()
                    || e.getY() < scrollPane.getY() || e.getY() > scrollPane.getY() + scrollPane.getHeight()) {
                stage().removeEventListener(this, InputEvent.MOUSE_DOWN);
                close();
            }
        });

        open = true;
    }

    private void close() {
        stage().removeEventListener(this, InputEvent.MOUSE_DOWN);
        oldTime = System.currentTimeMillis();
        scrollPane.removeFromParent();
        displayedItemList.forEach(Item::removeFromParent);
        displayedItemList.clear();
        arrow.setScaleY(1.0f);
        arrow.setXY(getWidth() - arrow.getWidth() - 10, (getHeight() - arrow.getHeight()) / 2);
        open = false;
    }

    public void setBitmapFont(BitmapFont bitmapFont) {
        bitmapText.setBitmapFont(bitmapFont);
    }

    public BitmapFont getBitmapFont() {
        return bitmapText.getBitmapFont();
    }

    public void clear() {
        if (open) close();
        itemMap.clear();
        itemList.clear();
        bitmapText.setText("");
    }

    public void addItem(String text, T object) {
        Item<T> item = new Item<T>(this, text, object);
        itemMap.put(object, item);
        itemList.add(item);
    }

    public void removeItem(T object) {
        if (open) close();
        itemList.remove(itemMap.remove(object));
    }

    public void setItem(T object) {
        Item<T> item = itemMap.get(object);
        if (item == null) throw new IllegalStateException("No such item \"%s\"".formatted(object.toString()));
        bitmapText.setText(item.getText());
        selectedItem = item;
        cutText(bitmapText, getWidth());
        dispatchEvent(Event.builder().type(Event.CHANGE).build());
    }

    public T getSelectedItem() {
        return selectedItem != null ? selectedItem.getObject() : null;
    }

    @Override
    public void dispose() {
        super.dispose();
        itemMap.values().forEach(Item::dispose);
        itemMap.clear();
        itemList.clear();
        displayedItemList.forEach(Item::removeFromParent);
        displayedItemList.clear();
    }

    @Override
    public void update() {

    }

    private static void cutText(BitmapText bitmapText, float width) {
        String text = bitmapText.getText();
        if (bitmapText.getTextWidth() > width) {
            int length = (int) ((width / bitmapText.getCharWidth()) - 5);
            if (length > 0 && length < text.length()) {
                bitmapText.setText(text);
            }
            bitmapText.setText(text);
        }
    }

    private static class Item<T> extends Component {

        private static final float HEIGHT = 24.0f;

        private final String text;
        private final T object;
        private final DropDownList<T> dropDownList;
        private final PlainRect bg;
        private final BitmapText bitmapText;

        public Item(DropDownList<T> dropDownList, String text, T object) {
            this.dropDownList = dropDownList;
            this.text = text;
            this.object = object;

            bg = new PlainRect(BACKGROUND_COLOR);
            add(bg);

            bitmapText = new BitmapText();
            bitmapText.setBitmapFont(dropDownList.getBitmapFont());
            bitmapText.setText(text);
            cutText(bitmapText, getWidth());
            add(bitmapText);

            addEventListener(Event.RESIZE, this::this_resize);
            addEventListener(InteractiveEvent.DOWN, this::this_down);

            setSize(dropDownList.getWidth(), HEIGHT);
        }

        private void this_resize(Event event) {
            bg.setSize(getWidth(), getHeight());
            bitmapText.setAutosize(true);
            bitmapText.setXY(10, (getHeight() - bitmapText.getTextHeight()) / 2);
            cutText(bitmapText, getWidth());
        }

        private void this_down(Event event) {
            dropDownList.setItem(object);
            dropDownList.close();
        }

        public String getText() {
            return text;
        }

        public T getObject() {
            return object;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "text='" + text + '\'' +
                    ", object=" + object +
                    ", dropDownList=" + dropDownList +
                    '}';
        }
    }

    public static void main(String[] args) {
        Stage stage = init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.init();

        Frame panel = new Frame();
        panel.setSize(500, 500);
        stage.add(panel, 100, 250);

        DropDownList<Integer> dropDownList = new DropDownList<>();
        dropDownList.setPushEventsUp(false);
        for (int i = 0; i < 10; i++) {
            String text = Math.random() + "";
            dropDownList.addItem(text, i);
        }
        panel.add(dropDownList, 10, 50);

        DropDownList<Integer> dropDownList1 = new DropDownList<>();
        dropDownList1.setPushEventsUp(false);
        for (int i = 0; i < 10; i++) {
            String text = Math.random() + "";
            dropDownList1.addItem(text, i);
        }
        panel.add(dropDownList1, 10, 80);

        DragUtil.enableDrag(panel);


        loop();
    }
}



















