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
package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.SimpleContainer;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.SpriteFactory;
import com.ancevt.d2d2.input.Mouse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cursor {

    public static final int MODE_IDLE = 0;
    public static final int MODE_TEXT = 1;
    public static final int MODE_RESIZE = 2;

    @Getter
    private static DisplayObject idleCursor;
    private static DisplayObject textCursor;
    @Getter
    private static DisplayObject resizeCursor;

    @Getter
    private static int mode;

    public static void setIdleCursor(DisplayObject cursor) {
        D2D2.setCursor(cursor);
        Cursor.idleCursor = cursor;
    }

    public static void setTextCursor(DisplayObject cursor) {
        Cursor.textCursor = cursor;
    }

    public static DisplayObject getTextCursor() {
        return Cursor.textCursor;
    }

    private static void setResizeCursor(DisplayObject resizeCursor) {
        Cursor.resizeCursor = resizeCursor;
    }

    public static void setDefaultCursorTheme() {
        setIdleCursor(SpriteFactory.createSpriteByTextureKey(ComponentAssets.MOUSE_CURSOR_IDLE));

        SimpleContainer resizeCursorContainer = new SimpleContainer();
        Sprite resizeCursorSprite = SpriteFactory.createSpriteByTextureKey(ComponentAssets.MOUSE_CURSOR_RESIZE);
        resizeCursorSprite.setXY(-resizeCursorSprite.getWidth() / 2, -resizeCursorSprite.getHeight() / 2);
        resizeCursorContainer.addChild(resizeCursorSprite);
        setResizeCursor(resizeCursorContainer);

        SimpleContainer container = new SimpleContainer();
        Sprite textCursorSprite = SpriteFactory.createSpriteByTextureKey(ComponentAssets.MOUSE_CURSOR_TEXT);
        container.addChild(textCursorSprite, -textCursorSprite.getWidth() / 2, -textCursorSprite.getHeight() / 2);
        setTextCursor(container);
    }

    public static void switchToResize(float angle) {
        if (resizeCursor != null) resizeCursor.setRotation(angle);

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
