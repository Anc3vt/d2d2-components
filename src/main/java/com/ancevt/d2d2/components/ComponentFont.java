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
import com.ancevt.d2d2.engine.lwjgl.LwjglEngine;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;

public class ComponentFont {

    private static final String FONT_ASSET_MIDDLE = "terminus/Terminus-16-Bold";
    private static final String FONT_ASSET_SMALL = "terminus/Terminus-12";


    //private static final String FONT_ASSET_MIDDLE_GLOW = "Terminus-16-Bold-Glow";
    private static final String FONT_ASSET_MIDDLE_GLOW = "TerminusTTF-Bold-4.49.3-16";
    private static BitmapFont bitmapFontMiddle;
    private static BitmapFont bitmapFontMiddleGlow;
    private static BitmapFont bitmapFontSmall;

    public static BitmapFont getBitmapFontMiddle() {
        if (bitmapFontMiddle == null) {
            bitmapFontMiddle = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE);
            bitmapFontMiddle.setPaddingTop(-3f);
        }
        return bitmapFontMiddle;
    }

    public static BitmapFont getBitmapFontMiddleGlow() {
        if (bitmapFontMiddleGlow == null) {
            bitmapFontMiddleGlow = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_MIDDLE_GLOW);
            bitmapFontMiddleGlow.setPaddingTop(-3f);
        }
        return bitmapFontMiddleGlow;
    }

    public static BitmapFont getBitmapFontSmall() {
        if (bitmapFontSmall == null) {
            bitmapFontSmall = D2D2.bitmapFontManager().loadBitmapFont(FONT_ASSET_SMALL);
            bitmapFontSmall.setPaddingTop(-1f);
        }
        return bitmapFontSmall;
    }

    public static void main(String[] args) {
        Stage stage = D2D2.directInit(new LwjglEngine(800, 600, "(floating)"));
        StarletSpace.haveFun();

        BitmapText bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        bitmapText.setMulticolorEnabled(true);
        bitmapText.setAutosize(true);

        bitmapText.setText("""
                #<FFFF00>Yellow line
                <0000FF>BLUE LINE
                <FFFFFF>White line
                
                Copyright (C) 2022 the original author or authors.
                See the notice.md file distributed with this work for additional
                information regarding copyright ownership.
                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at
                http://www.apache.org/licenses/LICENSE-2.0
                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.""");

        stage.add(bitmapText, 100, 250);

        D2D2.loop();
    }
}
