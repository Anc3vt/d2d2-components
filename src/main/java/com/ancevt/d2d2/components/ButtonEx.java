package com.ancevt.d2d2.components;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IColored;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.Combined9Sprites;
import com.ancevt.d2d2.interactive.InteractiveManager;

import static com.ancevt.d2d2.D2D2.init;
import static com.ancevt.d2d2.D2D2.loop;

public class ButtonEx extends Component implements IColored {

    private static final float DEFAULT_WIDTH = 30.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private final Combined9Sprites combined9Sprites;
    private final Combined9Sprites selectedBorder;

    private Sprite iconSprite;
    private BitmapText bitmapText;
    private boolean toggleMode;
    private boolean selected;

    public ButtonEx() {
        combined9Sprites = new Combined9Sprites(
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_CENTER),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTexture(ComponentAssets.BUTTON_9_SIDE_BOTTOM_RIGHT)
        );

        combined9Sprites.setEnabled(false);
        add(combined9Sprites);

        selectedBorder = new Combined9Sprites(
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_TOP_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_CENTER),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_RIGHT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM_LEFT),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM),
                D2D2.getTextureManager().getTexture(ComponentAssets.BORDER_9_SIDE_BOTTOM_RIGHT)
        );

        selectedBorder.setEnabled(false);
        selectedBorder.setVisible(false);
        selectedBorder.setColor(TOGGLE_BUTTON_PUSHED_IN_BORDER_COLOR);
        add(selectedBorder);

        addEventListener(Button.class, InteractiveEvent.DOWN, event -> {
            if (toggleMode) {
                setSelected(!isSelected());
            } else {
                combined9Sprites.setY(1);
                if (bitmapText != null) bitmapText.moveY(1);
                if (iconSprite != null) iconSprite.moveY(1);
            }
        });

        addEventListener(Button.class, InteractiveEvent.UP, event -> {
            combined9Sprites.setY(0);
            if (bitmapText != null) bitmapText.moveY(0);
            if (iconSprite != null) iconSprite.moveY(0);
            update();
            combined9Sprites.setColor(HOVER_FOREGROUND_COLOR);
        });

        addEventListener(Button.class, InteractiveEvent.HOVER, event -> combined9Sprites.setColor(HOVER_FOREGROUND_COLOR));
        addEventListener(Button.class, InteractiveEvent.OUT, event -> combined9Sprites.setColor(FOREGROUND_COLOR));

        addEventListener(Event.RESIZE, this::this_resize);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCorrespondingColors();
    }

    private void this_resize(Event event) {
        combined9Sprites.setSize(getWidth(), getHeight());
        selectedBorder.setSize(getWidth(), getHeight());
    }

    public void setToggleMode(boolean toggleMode) {
        this.toggleMode = toggleMode;
        update();
    }

    public boolean isToggleMode() {
        return toggleMode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedBorder.setVisible(selected);
        dispatchEvent(Event.builder().type(Event.CHANGE).build());
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setCorrespondingColors();
    }

    private void setCorrespondingColors() {
        if (bitmapText != null) bitmapText.setColor(isEnabled() ? TEXT_COLOR : TEXT_COLOR_DISABLED);
        if (iconSprite != null) {
            iconSprite.setColor(isEnabled() ? Color.WHITE : Color.DARK_GRAY);
            iconSprite.setAlpha(isEnabled() ? 1.0f : 0.5f);
        }
        combined9Sprites.setColor(isEnabled() ? FOREGROUND_COLOR : FOREGROUND_COLOR_DISABLED);
    }

    public void setText(Object text) {
        if (bitmapText == null) {
            bitmapText = new BitmapText();
            bitmapText.setBitmapFont(Font.getBitmapFont());
            bitmapText.setMulticolorEnabled(true);
            add(bitmapText);
        }

        bitmapText.setText("" + text);
        update();
    }

    public String getText() {
        return bitmapText.getText();
    }

    public String getPlainText() {
        return bitmapText.getPlainText();
    }

    public void setIcon(Texture texture) {
        if (iconSprite == null) {
            iconSprite = new Sprite();
            add(iconSprite);
        }
        iconSprite.setTexture(texture);
        update();
    }

    public Texture getIcon() {
        return iconSprite == null ? null : iconSprite.getTexture();
    }

    @Override
    public void setColor(Color color) {
        combined9Sprites.setColor(color);
    }

    @Override
    public void setColor(int rgb) {
        combined9Sprites.setColor(rgb);
    }

    @Override
    public Color getColor() {
        return combined9Sprites.getColor();
    }

    @Override
    public void update() {
        if (iconSprite != null && bitmapText != null) {
            throw new IllegalStateException("Can't set both text and icon");
        }

        if (iconSprite != null) {
            iconSprite.setScale(10f, 10f);
            while (iconSprite.getWidth() * iconSprite.getScaleX() > getWidth() || iconSprite.getHeight() * iconSprite.getScaleY() > getHeight()) {
                iconSprite.toScale(0.9f, 0.9f);
            }
            float sw = iconSprite.getWidth() * iconSprite.getScaleX();
            float sh = iconSprite.getHeight() * iconSprite.getScaleY();
            float w = getWidth();
            float h = getHeight();
            iconSprite.setXY((w - sw) / 2, (h - sh) / 2);
        }

        if (bitmapText != null) {
            bitmapText.setSize(getWidth(), getHeight());
            float w = getWidth();
            float h = getHeight();
            float tw = bitmapText.getTextWidth();
            float th = bitmapText.getCharHeight();
            bitmapText.setXY((w - tw) / 2, (h - th) / 2 + 1);
        }
    }

    public static void main(String[] args) {
        Stage stage = init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.load();

        InteractiveManager.getInstance().setTabbingEnabled(true);

        ButtonEx b1 = new ButtonEx();
        b1.setText("Text in button");
        b1.setSize(150, 30);
        b1.setTabbingEnabled(true);
        b1.addEventListener(InteractiveEvent.DOWN, event -> {
            System.out.println("b1 DOWN");
        });

        ButtonEx b2 = new ButtonEx();
        b2.setToggleMode(true);
        b2.setIcon(D2D2.getTextureManager().getTexture("satellite"));
        b2.setSize(60, 60);
        b2.setTabbingEnabled(true);
        b2.addEventListener(Event.CHANGE, event -> {
            System.out.println(b2.isSelected());
        });

        ButtonEx b3 = new ButtonEx();
        b3.setToggleMode(true);
        b3.setIcon(D2D2.getTextureManager().getTexture("satellite"));
        b3.setSize(60, 60);
        b3.setEnabled(false);
        b3.setTabbingEnabled(true);
        b3.addEventListener(Event.CHANGE, event -> {
            System.out.println(b3.isSelected());
        });

        Button oldPlainButton = new Button("Plain button");
        oldPlainButton.setWidth(150);
        oldPlainButton.setTabbingEnabled(true);

        stage.add(b1, 100, 250);
        stage.add(b2, 400, 250);
        stage.add(b3, 500, 250);
        stage.add(oldPlainButton, 100, 400);

        loop();
    }
}
