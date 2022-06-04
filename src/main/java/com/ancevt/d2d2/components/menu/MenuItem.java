package com.ancevt.d2d2.components.menu;

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.Font;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;

public class MenuItem extends Component {

    public static final MenuItem SEPARATOR = null;

    static final float HEIGHT = 30.0f;

    private static final Runnable STUB_ACTION = () -> {
    };

    private final PlainRect bg;
    private final BitmapText bitmapText;

    private IDisplayObject childMenuArrow;
    private Menu childMenu;
    private Runnable action;
    private final Menu parentMenu;

    public MenuItem(Menu parentMenu) {
        this.parentMenu = parentMenu;
        bg = new PlainRect();
        bg.setColor(MENU_BACKGROUND_COLOR);
        add(bg);

        action = STUB_ACTION;

        bitmapText = new BitmapText();
        bitmapText.setAutosize(true);
        bitmapText.setBitmapFont(Font.getBitmapFont());
        bitmapText.setMulticolorEnabled(true);
        add(bitmapText, 10, (HEIGHT - bitmapText.getTextHeight()) / 2 + 1);

        addEventListener(Event.RESIZE, this::this_resize);
        addEventListener(InteractiveEvent.HOVER, this::this_hover);
        addEventListener(InteractiveEvent.OUT, this::this_out);

        setSize(parentMenu.getWidth(), HEIGHT);
    }

    private void this_hover(Event event) {
        bg.setColor(MENU_HOVER_BACKGROUND_COLOR);
    }

    private void this_out(Event event) {
        bg.setColor(MENU_BACKGROUND_COLOR);
    }

    private void this_resize(Event event) {
        bg.setSize(getWidth(), getHeight());
    }

    public void setText(Object text) {
        bitmapText.setText("" + text);
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public Runnable getAction() {
        return action;
    }

    public void setChildMenu(Menu childMenu) {
        this.childMenu = childMenu;
        if (childMenuArrow != null) childMenuArrow.removeFromParent();

        if (childMenu != null) {
            childMenuArrow = new Sprite(ComponentAssets.MENU_CHILD_ARROW);
            add(childMenuArrow);
            update();
        }
    }

    public Menu getChildMenu() {
        return childMenu;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public boolean hasChildMenu() {
        return childMenu != null;
    }

    @Override
    public void dispose() {
        super.dispose();
        childMenu.dispose();
    }

    @Override
    public void update() {
        if (childMenuArrow != null) {
            childMenuArrow.setXY(getWidth() - childMenuArrow.getWidth() - 5, (HEIGHT - childMenuArrow.getHeight()) / 2);
        }
    }
}