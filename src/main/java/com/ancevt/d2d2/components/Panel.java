package com.ancevt.d2d2.components;

import com.ancevt.d2d2.common.PlainRect;

public class Panel extends Component {

    private final PlainRect bg;

    public Panel() {
        bg = new PlainRect();
        add(bg);
        bg.setAlpha(D2D2Components.PANEL_BG_ALPHA);
        bg.setColor(D2D2Components.PANEL_BG_COLOR);
        setSize(D2D2Components.DEFAULT_PANEL_WIDTH, D2D2Components.DEFAULT_PANEL_HEIGHT);
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
