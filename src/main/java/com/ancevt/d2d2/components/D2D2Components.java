package com.ancevt.d2d2.components;

import com.ancevt.d2d2.display.texture.Texture;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2Components {

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-components.inf");
    }

    public static Texture getArrowButtonTexture() {
        return getTextureManager().getTexture("d2d2-components-arrow-button");
    }

    public static Texture getButtonLeftPartTexture() {
        return getTextureManager().getTexture("d2d2-components-button-left-part");
    }

    public static Texture getButtonMiddlePartTexture() {
        return getTextureManager().getTexture("d2d2-components-button-middle-part");
    }

    public static Texture getButtonRightPartTexture() {
        return getTextureManager().getTexture("d2d2-components-button-right-part");
    }
}
