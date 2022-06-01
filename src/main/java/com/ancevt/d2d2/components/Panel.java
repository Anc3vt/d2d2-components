package com.ancevt.d2d2.components;

import com.ancevt.d2d2.common.PlainRect;

public class Panel extends Component {

    private static final float DEFAULT_WIDTH = 400.0f;
    private static final float DEFAULT_HEIGHT = 200.0f;

    private final PlainRect bg;

    public Panel() {
        bg = new PlainRect();
        add(bg);
        bg.setAlpha(Component.PANEL_BG_ALPHA);
        bg.setColor(Component.BACKGROUND_COLOR);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bg.setSize(width, height);
    }

}
