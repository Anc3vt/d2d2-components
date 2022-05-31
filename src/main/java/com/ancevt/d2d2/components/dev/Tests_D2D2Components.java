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
package com.ancevt.d2d2.components.dev;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.D2D2ComponentAssets;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.interactive.InteractiveButton;

import static com.ancevt.d2d2.components.D2D2ComponentAssets.MOUSE_CURSOR;

public class Tests_D2D2Components {

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2ComponentAssets.load();
        InteractiveButton.setGlobalTabbingEnabled(true);
        DebugPanel.setEnabled(true);
        stage.setBackgroundColor(Color.BLACK);

        D2D2.setCursor(new Sprite(MOUSE_CURSOR));

        for (int i = 0; i < 5; i++) {
            Button button = new Button("Test " + i);
            button.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> {
                DebugPanel.show(Button.class.getName(), button.getName() + " pressed").ifPresent(debugPanel -> {
                    debugPanel.setY(button.getY());
                });
            });
            button.setWidth(100);
            stage.add(button, 20, 50 + i * 30);
        }

        D2D2.loop();
        DebugPanel.saveAll();
    }
}
