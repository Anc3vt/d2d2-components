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

import com.ancevt.d2d2.display.texture.Texture;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2ComponentAssets {

    public static final String WARNING_ICON = "d2d2-components-warning-icon";
    public static final String ARROW_BUTTON = "d2d2-components-arrow-button";
    public static final String BUTTON_LEFT_PART_ENABLED = "d2d2-components-button-left-part-enabled";
    public static final String BUTTON_RIGHT_PART_ENABLED = "d2d2-components-button-right-part-enabled";
    public static final String BUTTON_MIDDLE_PART_ENABLED = "d2d2-components-button-middle-part-enabled";
    public static final String BUTTON_LEFT_PART_DISABLED = "d2d2-components-button-left-part-disabled";
    public static final String BUTTON_RIGHT_PART_DISABLED = "d2d2-components-button-right-part-disabled";
    public static final String BUTTON_MIDDLE_PART_DISABLED = "d2d2-components-button-middle-part-disabled";
    public static final String MOUSE_CURSOR = "d2d2-components-mouse-cursor-idle";
    public static final String CHECKBOX_UNCHECKED_ENABLED = "d2d2-components-checkbox-unchecked-enabled";
    public static final String CHECKBOX_CHECKED_ENABLED = "d2d2-components-checkbox-checked-enabled";
    public static final String CHECKBOX_UNCHECKED_DISABLED = "d2d2-components-checkbox-unchecked-disabled";
    public static final String CHECKBOX_CHECKED_DISABLED = "d2d2-components-checkbox-checked-disabled";

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-components.inf");
    }

    public static Texture getTexture(String textureKey) {
        return getTextureManager().getTexture(textureKey);
    }
}
