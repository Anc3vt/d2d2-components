package com.ancevt.d2d2.compas.component.standard

import com.ancevt.d2d2.D2D2
import com.ancevt.d2d2.compas.component.TextField
import com.ancevt.d2d2.display.Color
import com.ancevt.d2d2.display.interactive.Combined9Sprites
import com.ancevt.d2d2.display.shape.RectangleShape
import com.ancevt.d2d2.display.text.Text
import com.ancevt.d2d2.event.Event
import com.ancevt.d2d2.event.InteractiveEvent

class StandardTextField : Combined9Sprites(
    D2D2.textureManager().loadTexture("compas-atlas.png").createTextureClip(48, 0, 24, 24),
    8,
    8
), TextField {

    companion object {
        private const val DEFAULT_WIDTH = 200f
        private const val DEFAULT_HEIGHT = 100f
    }

    private var text = Text(StandardFontProducer.getFont())

    private val caret = StandardTextFieldCaret(1f, text.font.zeroCharHeight.toFloat())

    init {
        text.isMultiline = true
        text.isAutosize = false
        text.lineSpacing = -8f
        text.isWordWrap = false

        addChild(text, 5f, 5f)

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)

        addEventListener(this, InteractiveEvent.DOWN, ::onMouseDown)
        addEventListener(this, InteractiveEvent.KEY_TYPE, ::onKeyType)
        isEnabled = true

        addChild(caret)
    }

    private fun onKeyType(event: Event) {
        val e = event as InteractiveEvent

        val keyType = e.keyType
        insertText(keyType)

    }

    private fun insertText(textToInsert: String) {
        if (textToInsert.isEmpty()) {
            setText(textToInsert)
        }
        val index: Int = getCharIndexAtXY((caret.x * absoluteScaleX).toInt(), (caret.y * absoluteScaleY).toInt())
        println(index)


        //setText(getText().substring(0, index) + textToInsert + getText().substring(index))
    }

    private fun onMouseDown(event: Event) {
        val e = event as InteractiveEvent
        val index = getCharIndexAtXY(e.x, e.y)

        if (index >= 0) {
            val char = text.text[index]
            println(char)

            val pos = getPositionOfIndex(index - 1)
            caret.setXY(pos.x, pos.y)
        }


        //println("${e.x} ${e.y}")
    }

    private fun getPositionOfIndex(index: Int): Position {
        val font = text.font

        var x = 0
        var y = 0
        var i = 0

        for (char in text.plainText) {
            val charInfo = font.getCharInfo(char)

            if (char == '\n') {
                x = 0
                y += (charInfo.height() + text.lineSpacing).toInt()
            }
            x += charInfo.width()

            if (index == i) return Position(
                x.toFloat() + text.x,
                y.toFloat() + text.y
            )
            i++
        }

        return Position(0f, 0f)
    }

    private fun getCharIndexAtXY(tx: Int, ty: Int): Int {
        val font = text.font

        var x = 0
        var y = 0
        var index = 0

        for (char in text.plainText) {
            val charInfo = font.getCharInfo(char)

            if (char == '\n') {
                x = 0
                y += (charInfo.height() + text.lineSpacing).toInt()
            }

            if (tx - text.x * absoluteScaleX > x * absoluteScaleX &&
                ty - text.y * absoluteScaleY > y * absoluteScaleY &&
                tx - text.y * absoluteScaleX < (x + charInfo.width()) * absoluteScaleX &&
                ty - text.y * absoluteScaleY < (y + charInfo.height()) * absoluteScaleY
            ) {
                return index
            }

            x += charInfo.width()
            index++
        }

        return -1
    }

    override fun setText(text: String) {
        this.text.text = text
    }

    override fun getText(): String {
        return text.text
    }

    override fun setSize(width: Float, height: Float) {
        text.setSize(width, height)
        super.setSize(width, height)
    }

    override fun setWidth(value: Float) {
        text.width = value
        super.setWidth(value)
    }

    override fun setHeight(value: Float) {
        text.height = value
        super.setHeight(value)
    }

    override fun setColor(color: Color?) {
        text.color = color
    }

    override fun getColor(): Color {
        return text.color
    }
}

data class Position(val x: Float, val y: Float)

class StandardTextFieldCaret(width: Float, height: Float) : RectangleShape() {

    private var counter = 0

    init {
        setSize(width, height)
        setColor(Color.WHITE)
    }

    override fun onLoopUpdate() {
        super.onLoopUpdate()

        counter++

        if (counter < 20) {
            alpha = 0f
        } else {
            alpha = 1f
        }

        if (counter >= 40) {
            counter = 0
        }
    }

}