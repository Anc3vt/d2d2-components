package com.ancevt.d2d2.compas.component.standard

import com.ancevt.d2d2.asset.Assets
import com.ancevt.d2d2.display.text.Font
import com.ancevt.d2d2.display.text.TrueTypeFontBuilder

object StandardFontProducer {

    private lateinit var font: Font

    fun getFont():Font {
        if(!::font.isInitialized) {
            font = TrueTypeFontBuilder()
                .inputStream(Assets.getAsset("fonts/OpirusOpikRegular-RgDv.ttf"))
                .fontSize(32)
                .spacingX(2)
                .spacingY(2)
                .build()
        }

        return font
    }
}