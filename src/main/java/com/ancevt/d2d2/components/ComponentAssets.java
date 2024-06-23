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
import com.ancevt.d2d2.display.texture.TextureClip;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ComponentAssets {

    public static final String MOUSE_CURSOR_IDLE = "d2d2-components-mouse-cursor-idle";
    public static final String MOUSE_CURSOR_TEXT = "d2d2-components-mouse-cursor-text";
    public static final String MOUSE_CURSOR_RESIZE = "d2d2-components-mouse-cursor-resize";
    public static final String ARROW_BUTTON = "d2d2-components-arrow-button";
    public static final String BUTTON_LEFT_PART = "d2d2-components-button-left-part";
    public static final String BUTTON_RIGHT_PART = "d2d2-components-button-right-part";
    public static final String BUTTON_MIDDLE_PART = "d2d2-components-button-middle-part";
    public static final String CHECKBOX_UNCHECKED = "d2d2-components-checkbox-unchecked";
    public static final String CHECKBOX_CHECKED = "d2d2-components-checkbox-checked";
    public static final String MENU_CHILD_ARROW = "d2d2-components-menu-child-arrow";
    public static final String DROP_DOWN_LIST_ARROW = "d2d2-components-drop-down-list-arrow";
    public static final String TOOLTIP_IMAGE_BACKGROUND = "d2d2-components-tooltip-image-bg";

    public static final String BUTTON_9_SIDE_TOP_LEFT = "d2d2-components-button-9-side-top-left";
    public static final String BUTTON_9_SIDE_TOP = "d2d2-components-button-9-side-top";
    public static final String BUTTON_9_SIDE_TOP_RIGHT = "d2d2-components-button-9-side-top-right";
    public static final String BUTTON_9_SIDE_LEFT = "d2d2-components-button-9-side-left";
    public static final String BUTTON_9_SIDE_CENTER = "d2d2-components-button-9-side-center";
    public static final String BUTTON_9_SIDE_RIGHT = "d2d2-components-button-9-side-right";
    public static final String BUTTON_9_SIDE_BOTTOM_LEFT = "d2d2-components-button-9-side-bottom-left";
    public static final String BUTTON_9_SIDE_BOTTOM = "d2d2-components-button-9-side-bottom";
    public static final String BUTTON_9_SIDE_BOTTOM_RIGHT = "d2d2-components-button-9-side-bottom-right";

    public static final String BORDER_9_SIDE_TOP_LEFT = "d2d2-components-border-9-side-top-left";
    public static final String BORDER_9_SIDE_TOP = "d2d2-components-border-9-side-top";
    public static final String BORDER_9_SIDE_TOP_RIGHT = "d2d2-components-border-9-side-top-right";
    public static final String BORDER_9_SIDE_LEFT = "d2d2-components-border-9-side-left";
    public static final String BORDER_9_SIDE_CENTER = "d2d2-components-border-9-side-center";
    public static final String BORDER_9_SIDE_RIGHT = "d2d2-components-border-9-side-right";
    public static final String BORDER_9_SIDE_BOTTOM_LEFT = "d2d2-components-border-9-side-bottom-left";
    public static final String BORDER_9_SIDE_BOTTOM = "d2d2-components-border-9-side-bottom";
    public static final String BORDER_9_SIDE_BOTTOM_RIGHT = "d2d2-components-border-9-side-bottom-right";

    public static final String RECT_BORDER_9_SIDE_TOP_LEFT = "d2d2-components-rborder-9-side-top-left";
    public static final String RECT_BORDER_9_SIDE_TOP = "d2d2-components-rborder-9-side-top";
    public static final String RECT_BORDER_9_SIDE_TOP_RIGHT = "d2d2-components-rborder-9-side-top-right";
    public static final String RECT_BORDER_9_SIDE_LEFT = "d2d2-components-rborder-9-side-left";
    public static final String RECT_BORDER_9_SIDE_CENTER = "d2d2-components-rborder-9-side-center";
    public static final String RECT_BORDER_9_SIDE_RIGHT = "d2d2-components-rborder-9-side-right";
    public static final String RECT_BORDER_9_SIDE_BOTTOM_LEFT = "d2d2-components-rborder-9-side-bottom-left";
    public static final String RECT_BORDER_9_SIDE_BOTTOM = "d2d2-components-rborder-9-side-bottom";
    public static final String RECT_BORDER_9_SIDE_BOTTOM_RIGHT = "d2d2-components-rborder-9-side-bottom-right";

    public static final String ROUNDED_9_SIDE_TOP_LEFT = "d2d2-components-rounded-9-side-top-left";
    public static final String ROUNDED_9_SIDE_TOP = "d2d2-components-rounded-9-side-top";
    public static final String ROUNDED_9_SIDE_TOP_RIGHT = "d2d2-components-rounded-9-side-top-right";
    public static final String ROUNDED_9_SIDE_LEFT = "d2d2-components-rounded-9-side-left";
    public static final String ROUNDED_9_SIDE_CENTER = "d2d2-components-rounded-9-side-center";
    public static final String ROUNDED_9_SIDE_RIGHT = "d2d2-components-rounded-9-side-right";
    public static final String ROUNDED_9_SIDE_BOTTOM_LEFT = "d2d2-components-rounded-9-side-bottom-left";
    public static final String ROUNDED_9_SIDE_BOTTOM = "d2d2-components-rounded-9-side-bottom";
    public static final String ROUNDED_9_SIDE_BOTTOM_RIGHT = "d2d2-components-rounded-9-side-bottom-right";

    @Getter
    private static boolean initialized;

    public static void init() {
        if (!initialized) {
            D2D2.textureManager().loadTextureDataInfo("d2d2-components.inf");
            //Cursor.setDefaultCursorTheme();
            initialized = true;
        } else {
            log.warn("Components already initialized");
        }
    }

    public static TextureClip getTexture(String textureKey) {
        return D2D2.textureManager().getTextureClip(textureKey);
    }
}
