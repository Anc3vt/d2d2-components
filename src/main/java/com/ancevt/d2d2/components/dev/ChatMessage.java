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

package com.ancevt.d2d2.components.dev;

import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.ContainerImpl;
import com.ancevt.d2d2.scene.text.Font;
import com.ancevt.d2d2.scene.text.Text;
import lombok.Getter;

public class ChatMessage extends ContainerImpl {

    private static final float DEFAULT_WIDTH = 1000;
    public static final float DEFAULT_HEIGHT = 16;

    @Getter
    private final int id;
    @Getter
    private final String textString;
    @Getter
    private final Color textColor;
    final Text text;

    @Getter
    private boolean multicolorEnabled;

    public ChatMessage(int id, String messageText, Color textColor) {
        this.id = id;
        textString = messageText;
        this.textColor = textColor;
        text = new Text();
        text.setText(messageText);
        text.setFont(getFont());
        text.setSpacing(-0.5f);
        text.setWidth(DEFAULT_WIDTH);
        text.setHeight(DEFAULT_HEIGHT);
        text.setColor(textColor);
        text.setVertexBleedingFix(0);
        text.setWordWrap(false);


        addChild(text);
    }

    public void setMulticolorEnabled(boolean multicolorEnabled) {
        text.setMulticolor(multicolorEnabled);
        this.multicolorEnabled = multicolorEnabled;
    }

    public Font getFont() {
        return ComponentFont.getFontMiddleGlow();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", textUiText=" + text +
            ", textColor=" + textColor +
            '}';
    }
}

