package com.ancevt.d2d2.components.menu;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.Component;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.input.MouseButton;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.commons.concurrent.Async.runLater;
import static com.ancevt.d2d2.D2D2.stage;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Menu extends Component {

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;

    private final List<MenuItem> items;
    private final List<MenuItem> displayedItems;

    private Menu activeChildMenu;

    public Menu() {
        items = new ArrayList<>();
        displayedItems = new ArrayList<>();

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Menu addItem(Object text, Menu childMenu) {
        MenuItem item = new MenuItem(this);
        item.setText(text);
        item.setChildMenu(childMenu);
        item.addEventListener(this, InteractiveEvent.HOVER, this::item_hover);
        runLater(10, MILLISECONDS, () -> item.addEventListener(this, InteractiveEvent.DOWN, this::item_down));
        items.add(item);
        return this;
    }

    public Menu addItem(Object text, Runnable action) {
        MenuItem item = new MenuItem(this);
        item.setText(text);
        item.addEventListener(this, InteractiveEvent.HOVER, this::item_hover);
        runLater(10, MILLISECONDS, () -> item.addEventListener(this, InteractiveEvent.DOWN, this::item_down));
        items.add(item);
        item.setAction(action);
        return this;
    }

    private void item_down(Event event) {
        MenuItem item = (MenuItem) event.getSource();
        if (item.getAction() != null) item.getAction().run();
    }

    private void item_hover(Event event) {
        MenuItem item = (MenuItem) event.getSource();
        if (item.hasChildMenu()) {
            if (activeChildMenu != null) deactivate(activeChildMenu);
            activeChildMenu = item.getChildMenu().activate(item);
        }
    }

    public Menu addSeparator() {
        items.add(MenuItem.SEPARATOR);
        return this;
    }

    public Menu activate() {
        update();
        float x = Mouse.getX();
        float y = Mouse.getY();
        stage().add(this, x, y);

        if (getX() + getWidth() > stage().getWidth()) {
            setX(stage().getWidth() - getWidth());
        }

        if (getY() + getHeight() > stage().getHeight()) {
            setY(y - getHeight());
        }

        stage().addEventListener(this, InputEvent.MOUSE_DOWN, event -> {
            runLater(10, MILLISECONDS, () -> deactivate(this));
            stage().removeEventListener(this, InputEvent.MOUSE_DOWN);
        });

        return this;
    }

    public Menu activate(MenuItem fromItem) {
        update();
        float x = fromItem.getAbsoluteX() + fromItem.getWidth();
        float y = fromItem.getAbsoluteY();
        stage().add(this, x, y);

        if (getY() + getHeight() > stage().getHeight()) {
            setY(y - getHeight() + MenuItem.HEIGHT);
        }

        if (getX() + getWidth() > stage().getWidth()) {
            setX(fromItem.getParentMenu().getX() - getWidth());
        }

        return this;
    }

    private static void deactivate(Menu menu) {
        menu.removeFromParent();
        menu.items.forEach(menuItem -> {
            if (menuItem != MenuItem.SEPARATOR && menuItem.hasChildMenu()) {
                deactivate(menuItem.getChildMenu());
            }
        });
    }

    public static Menu createMenu() {
        return new Menu();
    }

    @Override
    public void update() {
        displayedItems.forEach(IDisplayObject::removeFromParent);
        displayedItems.clear();

        float y = 0;
        for (MenuItem item : items) {
            if (item == null) {
                PlainRect separatorBg = new PlainRect(getWidth(), 6, MENU_BACKGROUND_COLOR);
                add(separatorBg, 0, y);
                PlainRect separator = new PlainRect(getWidth() - 8, 2, MENU_SEPARATOR_COLOR);
                add(separator, 4, y + 2);
                y += 6;
            } else {
                add(item, 0, y);
                displayedItems.add(item);
                y += item.getHeight();
            }
        }

        interactiveArea.setHeight(y);
    }

    @Override
    public void dispose() {
        super.dispose();
        items.forEach(MenuItem::dispose);
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        stage.setBackgroundColor(Color.of(0x001122));

        ComponentAssets.load();

        stage.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            var e = (InputEvent) event;

            if (e.getMouseButton() == MouseButton.RIGHT) {
                createMenu()
                        .addItem("first", () -> System.out.println("1"))
                        .addItem("second", () -> System.out.println("2"))
                        .addSeparator()
                        .addItem("third", createMenu()
                                .addItem("second level first", () -> System.out.println("2"))
                                .addItem("second level second", () -> System.out.println("3"))
                                .addItem("second level third", () -> System.out.println("4"))
                        )
                        .addItem("fourth", createMenu()
                                .addItem("second level first", () -> System.out.println("5"))
                                .addItem("second level second", () -> System.out.println("6"))
                                .addItem("second level third", () -> System.out.println("7"))
                        ).activate();
            }

        });

        D2D2.loop();
    }
}































