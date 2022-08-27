package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;

public class Panel extends Component {

    private final float DEFAULT_WIDTH = 200.0f;
    private final float DEFAULT_HEIGHT = 200.0f;

    private final PlainRect bg;

    public Panel() {
        setTabbingEnabled(false);

        bg = new PlainRect(Color.BLACK);
        bg.setAlpha(Component.PANEL_BG_ALPHA);
        add(bg);

        addEventListener(Panel.class, Event.RESIZE, this::this_resize);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_resize(Event event) {
        bg.setSize(getWidth(), getHeight());
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();

        Panel panel = new Panel();

        stage.add(panel, 100, 200);

        D2D2.loop();
    }
}
