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

    public static final int MODE_IDLE = 0;
    public static final int MODE_TEXT = 1;
    public static final int MODE_RESIZE = 2;

    private static IDisplayObject idleCursor;
    private static IDisplayObject textCursor;
    private static IDisplayObject resizeCursor;

    private static int mode;

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

    private static void setResizeCursor(IDisplayObject resizeCursor) {
        Cursor.resizeCursor = resizeCursor;
    }

    public static IDisplayObject getResizeCursor() {
        return resizeCursor;
    }

    public static void setDefaultCursorTheme() {
        setIdleCursor(new Sprite(ComponentAssets.MOUSE_CURSOR_IDLE));


        Container resizeCursorContainer = new Container();
        Sprite resizeCursorSprite = new Sprite(ComponentAssets.MOUSE_CURSOR_RESIZE);
        resizeCursorSprite.setXY(-resizeCursorSprite.getWidth() / 2, -resizeCursorSprite.getHeight() / 2);
        resizeCursorContainer.add(resizeCursorSprite);
        setResizeCursor(resizeCursorContainer);


        Container container = new Container();
        Sprite textCursorSprite = new Sprite(ComponentAssets.MOUSE_CURSOR_TEXT);
        container.add(textCursorSprite, -textCursorSprite.getWidth() / 2, -textCursorSprite.getHeight() / 2);
        setTextCursor(container);
    }

    public static int getMode() {
        return mode;
    }

    public static void switchToResize(float angle) {
        if(resizeCursor != null) resizeCursor.setRotation(angle);

        if (mode == MODE_RESIZE) return;
        mode = MODE_RESIZE;
        if (resizeCursor != null) {
            Mouse.setVisible(false);
            D2D2.setCursor(resizeCursor);
        } else {
            D2D2.setCursor(null);
            Mouse.setVisible(true);
        }
    }

    public static void switchToIdle() {
        if (mode == MODE_IDLE) return;
        mode = MODE_IDLE;
        if (idleCursor != null) {
            Mouse.setVisible(false);
            D2D2.setCursor(getIdleCursor());
        } else {
            D2D2.setCursor(null);
            Mouse.setVisible(true);
        }
    }

    public static void switchToText() {
        if (mode == MODE_TEXT) return;
        mode = MODE_TEXT;
        if (textCursor != null) {
            Mouse.setVisible(false);
            D2D2.setCursor(getTextCursor());
        } else {
            D2D2.setCursor(null);
            Mouse.setVisible(true);
        }
    }
}
