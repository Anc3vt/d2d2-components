/**
 * Copyright (C) 2025 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.scene.text.BitmapFont;

public class ComponentFont {

    private static final String FONT_ASSET_MIDDLE = "terminus/Terminus-16-Bold";
    private static final String FONT_ASSET_SMALL = "terminus/Terminus-12";

    //private static final String FONT_ASSET_MIDDLE_GLOW = "Terminus-16-Bold-Glow";
    private static final String FONT_ASSET_MIDDLE_GLOW = "TerminusTTF-Bold-4.49.3-16";
    private static BitmapFont bitmapFontMiddle;
    private static BitmapFont bitmapFontMiddleGlow;
    private static BitmapFont bitmapFontSmall;

    public static BitmapFont getFontMiddle() {
        if (bitmapFontMiddle == null) {
            bitmapFontMiddle = D2D2.getBitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE + ".png", FONT_ASSET_MIDDLE + ".bmf");
            bitmapFontMiddle.setPaddingTop(-3f);
        }
        return bitmapFontMiddle;
    }

    public static BitmapFont getFontMiddleGlow() {
        if (bitmapFontMiddleGlow == null) {
            bitmapFontMiddleGlow = D2D2.getBitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE_GLOW + ".png", FONT_ASSET_MIDDLE_GLOW + ".bmf");
            bitmapFontMiddleGlow.setPaddingTop(-3f);
        }
        return bitmapFontMiddleGlow;
    }

    public static BitmapFont getFontSmall() {
        if (bitmapFontSmall == null) {
            bitmapFontSmall = D2D2.getBitmapFontManager().loadBitmapFont(FONT_ASSET_SMALL + ".png", FONT_ASSET_SMALL + ".bmf");
            bitmapFontSmall.setPaddingTop(-1f);
        }
        return bitmapFontSmall;
    }
}
