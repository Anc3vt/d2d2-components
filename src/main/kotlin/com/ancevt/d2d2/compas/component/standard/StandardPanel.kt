package com.ancevt.d2d2.compas.component.standard

import com.ancevt.d2d2.D2D2
import com.ancevt.d2d2.compas.component.Component
import com.ancevt.d2d2.compas.component.Panel
import com.ancevt.d2d2.display.interactive.Combined9Sprites

class StandardPanel : Combined9Sprites(
    D2D2.textureManager().loadTexture("compas-atlas.png").createTextureClip(0, 0, 24, 24),
    8,
    8
), Panel {

    companion object {
        private const val DEFAULT_WIDTH = 100f
        private const val DEFAULT_HEIGHT = 100f
    }

    init {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    }

    private val components = mutableListOf<Component>()

    override fun addComponent(component: Component) {
        components.add(component)
        addChild(component)
    }

    override fun removeComponent(component: Component) {
        components.remove(component)
        removeChild(component)
    }

}