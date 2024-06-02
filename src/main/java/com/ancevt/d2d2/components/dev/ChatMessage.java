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
package com.ancevt.d2d2.components.dev;

import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.SimpleContainer;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;
import lombok.Getter;

public class ChatMessage extends SimpleContainer {

    private static final float DEFAULT_WIDTH = 1000;
    public static final float DEFAULT_HEIGHT = 16;

    @Getter
    private final int id;
    @Getter
    private final String text;
    @Getter
    private final Color textColor;
    final BitmapText bitmapText;

    @Getter
    private boolean multicolorEnabled;

    public ChatMessage(int id, String messageText, Color textColor) {
        this.id = id;
        this.text = messageText;
        this.textColor = textColor;
        bitmapText = new BitmapText();
        bitmapText.setText(messageText);
        bitmapText.setBitmapFont(getBitmapFont());
        bitmapText.setSpacing(-0.5f);
        bitmapText.setWidth(DEFAULT_WIDTH);
        bitmapText.setHeight(DEFAULT_HEIGHT);
        bitmapText.setColor(textColor);
        bitmapText.setVertexBleedingFix(0);
        bitmapText.setWordWrap(false);


        addChild(bitmapText);
    }

    public void setMulticolorEnabled(boolean multicolorEnabled) {
        bitmapText.setMulticolor(multicolorEnabled);
        this.multicolorEnabled = multicolorEnabled;
    }

    public BitmapFont getBitmapFont() {
        return ComponentFont.getBitmapFontMiddleGlow();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", textUiText=" + bitmapText +
            ", textColor=" + textColor +
            '}';
    }
}

