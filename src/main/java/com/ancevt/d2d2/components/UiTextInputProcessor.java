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
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class UiTextInputProcessor {

    public static UiTextInputProcessor INSTANCE = new UiTextInputProcessor();
    private static final EventListener inputEventListener = event -> {
        if (event instanceof InputEvent inputEvent) {
            switch (event.getType()) {
                case InputEvent.KEY_TYPE -> {
                    INSTANCE.keyType(inputEvent.getCodepoint(), inputEvent.getKeyType());
                }
                case InputEvent.KEY_UP, InputEvent.KEY_DOWN -> {
                    INSTANCE.key(
                            inputEvent.getKeyCode(),
                            inputEvent.getKeyChar(),
                            inputEvent.isControl(),
                            inputEvent.isShift(),
                            inputEvent.isAlt(),
                            event.getType().equals(InputEvent.KEY_DOWN)
                    );
                }
            }
        }
    };

    private final List<UiTextInput> uiTextInputs;
    private int index;
    private static boolean enabled;

    private UiTextInputProcessor() {
        uiTextInputs = new ArrayList<>();
    }

    public static void setEnabled(boolean enabled) {
        UiTextInputProcessor.enabled = enabled;

        D2D2.stage().removeEventListener(UiTextInputProcessor.class, InputEvent.KEY_DOWN);
        D2D2.stage().removeEventListener(UiTextInputProcessor.class, InputEvent.KEY_UP);
        D2D2.stage().removeEventListener(UiTextInputProcessor.class, InputEvent.KEY_TYPE);

        if (enabled) {
            D2D2.stage().addEventListener(UiTextInputProcessor.class, InputEvent.KEY_DOWN, inputEventListener);
            D2D2.stage().addEventListener(UiTextInputProcessor.class, InputEvent.KEY_UP, inputEventListener);
            D2D2.stage().addEventListener(UiTextInputProcessor.class, InputEvent.KEY_TYPE, inputEventListener);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public void resetFocus() {
        index = 0;
    }

    public void addTextInput(UiTextInput t) {
        uiTextInputs.add(t);
    }

    public void removeTextInput(UiTextInput t) {
        uiTextInputs.remove(t);
    }

    public void key(int code, char chr, boolean control, boolean shift, boolean alt, boolean down) {
        if (down) {
            if (code == KeyCode.TAB) {
                if (shift) focusPrev();
                else focusNext();
            }
        }

        uiTextInputs.stream().filter(UiTextInput::isFocused).findAny().ifPresent(
                t -> t.key(code, chr, control, shift, alt, down)
        );
    }

    public void keyType(int codepoint, String keyType) {
        uiTextInputs.stream().filter(UiTextInput::isFocused).findAny().ifPresent(
                t -> t.keyType(codepoint, keyType)
        );
    }

    public void focusPrev() {
        if (uiTextInputs.size() <= 1) return;

        index--;
        if (index < 0) index = uiTextInputs.size() - 1;

        focus(uiTextInputs.get(index));
    }

    public void focusNext() {
        if (uiTextInputs.size() <= 1) return;

        index++;
        if (index >= uiTextInputs.size()) index = 0;

        focus(uiTextInputs.get(index));
    }

    public void unfocus() {
        uiTextInputs.forEach(UiTextInput::focusLost);
        resetFocus();
    }

    public void focus(UiTextInput uiTextInput) {
        uiTextInputs.stream().filter(t -> t != uiTextInput).forEach(UiTextInput::focusLost);
        uiTextInput.setCaretPosition(Integer.MAX_VALUE);
        uiTextInput.focus();

        index = uiTextInputs.indexOf(uiTextInput);
    }

}
