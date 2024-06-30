package com.ancevt.d2d2.compas.component.standard

import com.ancevt.d2d2.compas.component.*

class StandardComponentFactory : ComponentFactory {
    override fun createButton(): Button {
        return StandardButton()
    }

    override fun createCheckbox(): Checkbox {
        return StandardCheckbox()
    }

    override fun createLabel(): Label {
        return StandardLabel()
    }

    override fun createPanel(): Panel {
        return StandardPanel()
    }

    override fun createTextField(): TextField {
        return StandardTextField()
    }
}