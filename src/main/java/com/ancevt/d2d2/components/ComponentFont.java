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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.scene.text.Font;

public class ComponentFont {

    private static final String FONT_ASSET_MIDDLE = "terminus/Terminus-16-Bold";
    private static final String FONT_ASSET_SMALL = "terminus/Terminus-12";

    //private static final String FONT_ASSET_MIDDLE_GLOW = "Terminus-16-Bold-Glow";
    private static final String FONT_ASSET_MIDDLE_GLOW = "TerminusTTF-Bold-4.49.3-16";
    private static Font fontMiddle;
    private static Font fontMiddleGlow;
    private static Font fontSmall;

    public static Font getFontMiddle() {
        if (fontMiddle == null) {
            fontMiddle = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE);
            fontMiddle.setPaddingTop(-3f);
        }
        return fontMiddle;
    }

    public static Font getFontMiddleGlow() {
        if (fontMiddleGlow == null) {
            fontMiddleGlow = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE_GLOW);
            fontMiddleGlow.setPaddingTop(-3f);
        }
        return fontMiddleGlow;
    }

    public static Font getFontSmall() {
        if (fontSmall == null) {
            fontSmall = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_SMALL);
            fontSmall.setPaddingTop(-1f);
        }
        return fontSmall;
    }
}
