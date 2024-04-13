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
package com.ancevt.d2d2.components.dev.chat;

import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;
import lombok.Getter;

import static java.lang.String.format;

public class ChatMessage extends Container {

    public static final float DEFAULT_WIDTH = 1000;
    public static final float DEFAULT_HEIGHT = 16;

    @Getter
    private final int id;
    @Getter
    private final int playerId;
    @Getter
    private final String playerName;
    @Getter
    private final Color playerColor;
    @Getter
    private final String text;
    @Getter
    private final Color textColor;
    private final BitmapText nameBitmapText;
    private final BitmapText bitmapText;

    public ChatMessage(int id, int playerId, String playerName, Color playerColor, String messageText, Color textColor) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerColor = playerColor == null ? Color.WHITE : playerColor;
        this.text = messageText;
        this.textColor = textColor == null ? Color.WHITE : textColor;
        nameBitmapText = new BitmapText();
        nameBitmapText.setBitmapFont(getBitmapFont());
        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(getBitmapFont());

        String playerNameToShow = format("%s(%d):", playerName, playerId);

        nameBitmapText.setColor(playerColor);
        bitmapText.setColor(textColor);
        nameBitmapText.setText(playerNameToShow);
        nameBitmapText.setSize(playerNameToShow.length() * ComponentFont.getBitmapFontMiddle().getCharInfo('0').width() + 10, 30);

        bitmapText.setText(messageText);
        bitmapText.setX(nameBitmapText.getWidth() + 8);
        bitmapText.setWidth(DEFAULT_WIDTH);
        bitmapText.setHeight(DEFAULT_HEIGHT);

        nameBitmapText.setHeight(DEFAULT_HEIGHT);
        nameBitmapText.setAutosize(true);

        bitmapText.setVertexBleedingFix(0);
        nameBitmapText.setVertexBleedingFix(0);

        add(nameBitmapText);
        add(bitmapText);
    }

    public ChatMessage(int id, String messageText, Color textColor) {
        this.id = id;
        this.playerId = 0;
        this.playerName = null;
        this.playerColor = null;
        this.text = messageText;
        this.textColor = textColor;
        nameBitmapText = null;
        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(getBitmapFont());

        bitmapText.setWidth(DEFAULT_WIDTH);
        bitmapText.setHeight(DEFAULT_HEIGHT);
        bitmapText.setColor(textColor);
        bitmapText.setText(messageText);

        bitmapText.setVertexBleedingFix(0);

        //add(new Glow8Shadows(bitmapText, Color.BLACK, 1.5f, 1f, 0f, 0f));
        add(bitmapText);
    }

    private static BitmapFont bitmapFont;

    public BitmapFont getBitmapFont() {
        return ComponentFont.getBitmapFontMiddleGlow();

//        if (bitmapFont == null) {
//            String fontPath = "d2d2ttf/terminus/TerminusTTF-Bold-4.49.3.ttf";
//            bitmapFont = new TtfBitmapFontBuilder()
//                .ttfAssetPath(fontPath)
//                .fontSize(24)
//                .spacingY(10)
//                .textAntialias(false)
//                .fractionalMetrics(FractionalMetrics.OFF)
//                .offsetY(5)
//                .build();
//        }
//        return bitmapFont;
    }

    public boolean isFromPlayer() {
        return playerName != null;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + id +
            ", playerId=" + playerId +
            ", playerName='" + playerName + '\'' +
            ", text='" + text + '\'' +
            ", nameUiText=" + nameBitmapText +
            ", textUiText=" + bitmapText +
            ", textColor=" + textColor +
            '}';
    }
}

