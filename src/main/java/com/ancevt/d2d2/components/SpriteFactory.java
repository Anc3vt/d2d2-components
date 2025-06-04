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
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.texture.TextureRegion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ancevt.d2d2.D2D2.getEngine;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpriteFactory {
    private static final Map<String, TextureRegion> textureCacheFiles = new HashMap<>();

    public static Sprite createSprite(String assetPath) {
        return getEngine().getNodeFactory().createSprite(assetPath);
    }

    public static Sprite createSprite(String assetPath, int textureX, int textureY, int textureWidth, int textureHeight) {
        return getEngine().getNodeFactory().createSprite(
                textureCacheFiles.computeIfAbsent(
                        "%s_%d".formatted(
                                assetPath,
                                Objects.hash(
                                        assetPath,
                                        textureX,
                                        textureY,
                                        textureWidth,
                                        textureHeight
                                )),
                        key -> D2D2.getTextureManager()
                                .loadTexture(assetPath)
                                .createTextureRegion(textureX, textureY, textureWidth, textureHeight)
                )
        );
    }


    public static Sprite createSpriteByTextureKey(String textureKey) {
        return getEngine().getNodeFactory().createSprite(D2D2.getTextureManager().getTextureRegion(textureKey));
    }

    public static Sprite createEmptySprite() {
        return getEngine().getNodeFactory().createSprite();
    }

    public static void clearCache() {
        textureCacheFiles.clear();
    }

}
