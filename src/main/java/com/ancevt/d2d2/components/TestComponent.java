package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;

public class TestComponent extends Panel {

    private final BitmapTextEx uiText;

    public TestComponent() {
        uiText = new BitmapTextEx("test !!!! text");
        add(uiText);

        addEventListener(TestComponent.class, ComponentEvent.FOCUS_IN, this::this_focusIn);
        addEventListener(TestComponent.class, ComponentEvent.FOCUS_OUT, this::this_focusOut);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        uiText.setColor(enabled ? Color.WHITE : Color.LIGHT_GRAY);
    }

    private void this_focusIn(Event event) {
        uiText.setText("this_focusIn");
    }

    private void this_focusOut(Event event) {
        uiText.setText("this_focusOut");
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(1000, 600, "(floating)"));
        stage.setBackgroundColor(Color.DARK_GRAY);

        TestComponent comp1 = new TestComponent();
        TestComponent comp2 = new TestComponent();

        stage.add(comp1, 50, 100);
        stage.add(comp2, 500, 100);

        BitmapTextEx uiText = new BitmapTextEx("Test root text");
        stage.add(uiText);

        D2D2.loop();
    }
}
