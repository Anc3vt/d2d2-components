package com.ancevt.d2d2.components;

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;

public class FrameTitle extends Component {

    private static final float DEFAULT_TITLE_HEIGHT = 25.0f;
    private static final Color DEFAULT_TITLE_BACKGROUND_COLOR = Color.of(0x505050);

    private final PlainRect bg;
    private final BitmapText bitmapText;

    public FrameTitle() {
        setComponentFocusRectVisibleEnabled(false);
        setTabbingEnabled(false);

        bg = new PlainRect(DEFAULT_TITLE_BACKGROUND_COLOR);
        add(bg);

        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        bitmapText.setMulticolorEnabled(true);
        add(bitmapText, 10, 10);

        addEventListener(Event.RESIZE, this::this_resize);

        setSize(getWidth(), DEFAULT_TITLE_HEIGHT);
    }

    private void this_resize(Event event) {
        bitmapText.setX((getWidth() - bitmapText.getTextWidth()) / 2);
        bitmapText.setY((DEFAULT_TITLE_HEIGHT - bitmapText.getTextHeight()) / 2);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bg.setSize(width, height);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bg.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bg.setHeight(height);
    }

    public void setTextColor(Color color) {
        bitmapText.setColor(color);
    }

    public Color getTextColor() {
        return bitmapText.getColor();
    }

    public void setBackgroundColor(Color color) {
        bg.setColor(color);
    }

    public Color getBackgroundColor() {
        return bg.getColor();
    }

    public void setText(String text) {
        bitmapText.setText(text);
        applyResize();
    }

    public String getText() {
        return bitmapText.getText();
    }

}
