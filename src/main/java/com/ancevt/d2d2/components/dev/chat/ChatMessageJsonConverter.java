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

import com.ancevt.d2d2.display.Color;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageJsonConverter {

    private static final Gson gson = JsonEngine.gson();

    public static ChatMessage jsonToChatMessage(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        int id = jsonObject.get("id").getAsInt();
        int playerId = jsonObject.get("playerId").getAsInt();
        String playerName = getStringOrNull(jsonObject, "playerName");
        String playerColorHex = getStringOrNull(jsonObject, "playerColorHex");
        String text = getStringOrNull(jsonObject, "text");
        String textColorHex = getStringOrNull(jsonObject, "textColorHex");

        Color playerColor = (playerColorHex != null) ? Color.of(playerColorHex) : Color.WHITE;
        Color textColor = (textColorHex != null) ? Color.of(textColorHex) : Color.WHITE;

        if (playerName == null) {
            return new ChatMessage(id, text, textColor);
        } else {
            return new ChatMessage(id, playerId, playerName, playerColor, text, textColor);
        }
    }

    public static String chatMessageToJson(ChatMessage chatMessage) {
        JsonObject object = new JsonObject();
        object.addProperty("id", chatMessage.getId());
        object.addProperty("playerId", chatMessage.getPlayerId());
        addPropertyIfNotNull(object, "playerName", chatMessage.getPlayerName());
        addPropertyIfNotNull(object, "playerColorHex", chatMessage.getPlayerColor() != null ? chatMessage.getPlayerColor().toHexString() : null);
        addPropertyIfNotNull(object, "text", chatMessage.getText());
        addPropertyIfNotNull(object, "textColorHex", chatMessage.getTextColor() != null ? chatMessage.getTextColor().toHexString() : null);
        return gson.toJson(object);
    }

    private static void addPropertyIfNotNull(JsonObject object, String key, String value) {
        if (value != null) {
            object.addProperty(key, value);
        }
    }

    private static String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return (element != null && !element.isJsonNull()) ? element.getAsString() : null;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class JsonEngine {
        private static Gson gson;

        private static Gson gson() {
            if (gson == null) {
                gson = new GsonBuilder().create();
            }
            return gson;
        }
    }
}
