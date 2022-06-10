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
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.input.Mouse;

public class Cursor {

    private static IDisplayObject idleCursor;
    private static IDisplayObject textCursor;

    public static void setIdleCursor(IDisplayObject cursor) {
        D2D2.setCursor(cursor);
        Cursor.idleCursor = cursor;
    }

    public static IDisplayObject getIdleCursor() {
        return idleCursor;
    }

    public static void setTextCursor(IDisplayObject cursor) {
        Cursor.textCursor = cursor;
    }

    public static IDisplayObject getTextCursor() {
        return Cursor.textCursor;
    }

    public static void setupDefaultIdleCursor() {
        setIdleCursor(new Sprite(ComponentAssets.MOUSE_CURSOR_IDLE));
    }

    public static void setupDefaultTextCursor() {
        Container container = new Container();
        Sprite sprite = new Sprite(ComponentAssets.MOUSE_CURSOR_TEXT);
        container.add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
        setTextCursor(container);
    }

    public static void switchToIdle() {
        if (idleCursor != null) {
            Mouse.setVisible(false);
            D2D2.setCursor(getIdleCursor());
        } else {
            D2D2.setCursor(null);
            Mouse.setVisible(true);
        }
    }

    public static void switchToText() {
        if (textCursor != null) {
            Mouse.setVisible(false);
            D2D2.setCursor(getTextCursor());
        } else {
            D2D2.setCursor(null);
            Mouse.setVisible(true);
        }
    }
}
