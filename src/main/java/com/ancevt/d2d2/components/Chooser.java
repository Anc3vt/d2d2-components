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

import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.BasicGroup;
import com.ancevt.d2d2.scene.text.BitmapText;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Chooser<T> extends BasicGroup {

    private static final float DEFAULT_WIDTH = 180;

    private final ArrowButton buttonLeft;
    private final ArrowButton buttonRight;
    private final BitmapText bitmapText;
    private final List<Pair<String, T>> items;
    private final Button buttonApply;
    private int index;
    private float width;
    private Pair<String, T> selectedItemPair;
    private boolean enabled;

    public Chooser() {
        items = new ArrayList<>();

        buttonLeft = new ArrowButton();
        buttonLeft.setDirection(-1);
        buttonLeft.addEventListener(ArrowButton.ArrowButtonPressEvent.class, this::buttonLeft_arrowButtonPress);

        buttonRight = new ArrowButton();
        buttonRight.setDirection(1);
        buttonRight.addEventListener(ArrowButton.ArrowButtonPressEvent.class, this::buttonRight_arrowButtonPress);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getFontMiddle());

        buttonApply = new Button("Apply");
        buttonApply.addEventListener(Button.ButtonPressEvent.class, this::applyButton_buttonPressed);

        addChild(bitmapText);

        addChild(buttonLeft);
        addChild(buttonRight);
        addChild(buttonApply);

        setWidth(DEFAULT_WIDTH);
    }

    private void applyButton_buttonPressed(Button.ButtonPressEvent e) {
        setCurrentItemAsSelected();
    }

    private void setCurrentItemAsSelected() {
        selectedItemPair = items.get(index);
        buttonApply.setInteractionEnabled(false);
        bitmapText.setColor(Color.WHITE);
        dispatchEvent(ApplyEvent.create());
    }

    public void setWidth(float width) {
        this.width = width;
        buttonRight.setX(width - buttonLeft.getWidth() - buttonRight.getWidth());
        buttonApply.setX(width + 10);
    }

    @Override
    public float getWidth() {
        return width;
    }

    private void buttonLeft_arrowButtonPress(Event event) {
        prev();
    }

    private void buttonRight_arrowButtonPress(Event event) {
        next();
    }

    private void prev() {
        setIndex(getIndex() - 1);
        dispatchEvent(SwitchEvent.create());
    }

    private void next() {
        setIndex(getIndex() + 1);
        dispatchEvent(SwitchEvent.create());
    }

    public void clear() {
        items.clear();
    }

    public void setCurrentItemByKey(String key) {
        for (int i = 0; i < items.size(); i++) {
            var p = items.get(i);
            if (key.equals(p.getFirst())) {
                selectedItemPair = p;
                setIndex(i);
                return;
            }
        }
    }

    public void setCurrentItemByValue(T value) {
        for (int i = 0; i < items.size(); i++) {
            var p = items.get(i);
            if (value == p.getSecond() || p.getSecond().equals(value)) {
                selectedItemPair = p;
                setIndex(i);
                return;
            }
        }
    }

    public void setIndex(int index) {
        this.index = index;
        if (this.index <= 0) {
            this.index = 0;
            buttonLeft.setEnabled(false);
        } else {
            buttonLeft.setEnabled(true);
        }

        if (this.index >= items.size() - 1) {
            this.index = items.size() - 1;
            buttonRight.setEnabled(false);
        } else {
            buttonRight.setEnabled(true);
        }

        bitmapText.setText(items.get(index).getFirst());
        bitmapText.setWidth(getWidth());

        float width = getWidth() - buttonLeft.getWidth() * 3;

        if (bitmapText.getTextWidth() > width) {
            String text = this.bitmapText.getText();
            int l = (int) (width / this.bitmapText.getCharWidth()) - 5;
            if (l <= text.length()) {
                try {
                    text = text.substring(0, l).concat("...");
                } catch (StringIndexOutOfBoundsException e) {
                    text = "[!]";
                }
            }
            this.bitmapText.setText(text);
        }

        float w = bitmapText.getTextWidth() + 8;
        bitmapText.setX((getWidth() - w) / 2);

        buttonApply.setInteractionEnabled(selectedItemPair != items.get(index));
        bitmapText.setColor(selectedItemPair == items.get(index) ? Color.LIGHT_GREEN : Color.WHITE);
    }

    public int getIndex() {
        return index;
    }

    public int getItemCount() {
        return items.size();
    }

    public T getSelectedItemObject() {
        return selectedItemPair.getSecond();
    }

    public void addItem(String key, T item) {
        var pair = Pair.of(key, item);
        items.add(pair);
        selectedItemPair = pair;
        setIndex(items.size() - 1);
    }

    public void dispose() {
        buttonLeft.dispose();
        buttonRight.dispose();
    }

    public void setEnabled(boolean b) {
        enabled = b;
        buttonApply.setInteractionEnabled(b);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Getter
    @EventPooled
    public static class ApplyEvent extends Event {
        public static ApplyEvent create() {
            return EventPool.obtain(ApplyEvent.class);
        }
    }

    @Getter
    @EventPooled
    public static class SwitchEvent extends Event {
        public static SwitchEvent create() {
            return EventPool.obtain(SwitchEvent.class);
        }
    }


    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class Pair<T1, T2> {
        public final T1 first;
        public final T2 second;
    }

}
