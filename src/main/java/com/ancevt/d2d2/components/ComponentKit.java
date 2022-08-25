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
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;

public class ComponentKit {

    private static String[] BUTTON_EX_2_TEXTURE_KEYS = {
            ComponentAssets.ROUNDED_9_SIDE_TOP_LEFT,
            ComponentAssets.ROUNDED_9_SIDE_TOP,
            ComponentAssets.ROUNDED_9_SIDE_TOP_RIGHT,
            ComponentAssets.ROUNDED_9_SIDE_LEFT,
            ComponentAssets.ROUNDED_9_SIDE_CENTER,
            ComponentAssets.ROUNDED_9_SIDE_RIGHT,
            ComponentAssets.ROUNDED_9_SIDE_BOTTOM_LEFT,
            ComponentAssets.ROUNDED_9_SIDE_BOTTOM,
            ComponentAssets.ROUNDED_9_SIDE_BOTTOM_RIGHT
    };

    public static ButtonEx createButtonEx() {
        ButtonEx buttonEx = new ButtonEx();
        buttonEx.setSize(75, 30);
        buttonEx.setColorText(Color.WHITE);
        return buttonEx;
    }

    public static ButtonEx createButtonEx2() {
        ButtonEx buttonEx = new ButtonEx();
        buttonEx.setBackgroundTextures(BUTTON_EX_2_TEXTURE_KEYS);
        buttonEx.setColorBackground(Color.of(0x505050));
        buttonEx.setSize(75, 24);
        buttonEx.setColorText(Color.WHITE);
        return buttonEx;
    }

    public static TextInput createTextInput() {
        return new TextInput();
    }

    public static TextInput createTextInput2() {
        TextInput textInput = new TextInput();
        textInput.setHeight(21);
        textInput.setPadding(new Padding(15, 0, 15, 0));
        textInput.setColorBackground(Color.of(0x0D0D0D));
        //textInput.setColorFocusRect();
        textInput.setComponentFocusRectVisibleEnabled(true);
        return textInput;
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.init();

        var b1 = createButtonEx2();
        b1.setText("OK");
        stage.add(b1, 100, 250);

        var b2 = createButtonEx2();
        b2.setText("Cancel");
        stage.add(b2, 100, 280);

        var textInput = createTextInput2();
        textInput.setText("Text input");
        stage.add(textInput, 200, 250);

        D2D2.loop();
    }
}
