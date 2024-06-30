package com.ancevt.d2d2.compas.component.standard

import com.ancevt.d2d2.D2D2
import com.ancevt.d2d2.compas.component.Button
import com.ancevt.d2d2.display.interactive.Combined9Sprites

class StandardButton : Combined9Sprites(
    D2D2.textureManager().loadTexture("compas-atlas.png").createTextureClip(24, 0, 24, 24),
    8,
    8
), Button {
    override fun setText(text: String) {
        TODO("Not yet implemented")
    }

    override fun getText(): String {
        TODO("Not yet implemented")
    }

    override fun setSize(width: Float, height: Float) {
        TODO("Not yet implemented")
    }

    override fun setWidth(value: Float) {
        TODO("Not yet implemented")
    }

    override fun setHeight(value: Float) {
        TODO("Not yet implemented")
    }



}