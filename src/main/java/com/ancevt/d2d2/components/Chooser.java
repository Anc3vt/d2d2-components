/**
 * Copyright (C) 2025 the original author or authors.
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

import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.ContainerImpl;
import com.ancevt.d2d2.scene.text.Text;
import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

public class Chooser<T> extends ContainerImpl {

    private static final float DEFAULT_WIDTH = 180;

    private final ArrowButton buttonLeft;
    private final ArrowButton buttonRight;
    private final Text text;
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
        buttonLeft.addEventListener(ArrowButton.ArrowButtonEvent.ARROW_BUTTON_PRESS, this::buttonLeft_arrowButtonPress);

        buttonRight = new ArrowButton();
        buttonRight.setDirection(1);
        buttonRight.addEventListener(ArrowButton.ArrowButtonEvent.ARROW_BUTTON_PRESS, this::buttonRight_arrowButtonPress);

        text = new Text();
        text.setFont(ComponentFont.getFontMiddle());

        buttonApply = new Button("Apply");
        buttonApply.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, this::applyButton_buttonPressed);

        addChild(text);

        addChild(buttonLeft);
        addChild(buttonRight);
        addChild(buttonApply);

        setWidth(DEFAULT_WIDTH);
    }

    private void applyButton_buttonPressed(Event event) {
        setCurrentItemAsSelected();
    }

    private void setCurrentItemAsSelected() {
        selectedItemPair = items.get(index);
        buttonApply.setEnabled(false);
        text.setColor(Color.WHITE);
        dispatchEvent(ChooserEvent.builder().type(ChooserEvent.CHOOSER_APPLY).build());
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
        dispatchEvent(ChooserEvent.builder()
                .type(ChooserEvent.CHOOSER_SWITCH)
                .build());
    }

    private void next() {
        setIndex(getIndex() + 1);
        dispatchEvent(ChooserEvent.builder()
                .type(ChooserEvent.CHOOSER_SWITCH)
                .build());
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

        text.setText(items.get(index).getFirst());
        text.setWidth(getWidth());

        float width = getWidth() - buttonLeft.getWidth() * 3;

        if (text.getTextWidth() > width) {
            String text = this.text.getText();
            int l = (int) (width / this.text.getCharWidth()) - 5;
            if (l <= text.length()) {
                try {
                    text = text.substring(0, l).concat("...");
                } catch (StringIndexOutOfBoundsException e) {
                    text = "[!]";
                }
            }
            this.text.setText(text);
        }

        float w = text.getTextWidth() + 8;
        text.setX((getWidth() - w) / 2);

        buttonApply.setEnabled(selectedItemPair != items.get(index));
        text.setColor(selectedItemPair == items.get(index) ? Color.LIGHT_GREEN : Color.WHITE);
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
        buttonApply.setEnabled(b);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ChooserEvent extends Event {
        public static final String CHOOSER_APPLY = "chooserApply";
        public static final String CHOOSER_SWITCH = "chooserSwitch";
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class Pair<T1, T2> {
        public final T1 first;
        public final T2 second;
    }

}
