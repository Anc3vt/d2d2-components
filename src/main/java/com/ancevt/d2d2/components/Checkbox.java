package com.ancevt.d2d2.components;

import com.ancevt.d2d2.display.Sprite;

import static com.ancevt.d2d2.components.D2D2ComponentAssets.CHECKBOX_CHECKED_DISABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.CHECKBOX_UNCHECKED_DISABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.CHECKBOX_CHECKED_ENABLED;
import static com.ancevt.d2d2.components.D2D2ComponentAssets.CHECKBOX_UNCHECKED_ENABLED;

public class Checkbox extends Component {

    private static final float PADDING = 7.0f;

    private BitmapTextEx label;
    private final Sprite sprite;
    private boolean checked;

    public Checkbox() {
        sprite = new Sprite(CHECKBOX_UNCHECKED_ENABLED);
        add(sprite);
    }

    public Checkbox(String text) {
        this();
        label = new BitmapTextEx(text);
        add(label, sprite.getWidth() + PADDING, 0);
    }

    public void setText(String text) {
        if (text == null || text.length() == 0) {
            if (label != null) {
                label.removeFromParent();
                label = null;
            }
        } else {
            if (label == null) {
                label = new BitmapTextEx(text);
                add(label, sprite.getWidth() + PADDING, 0);
            }
        }
    }

    public String getText() {
        if (label == null) {
            return "";
        } else {
            return label.getText();
        }
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        setCorrespondingTexture();
    }

    public boolean isChecked() {
        return checked;
    }

    private void setCorrespondingTexture() {
        if (isEnabled()) {
            sprite.setTexture(isChecked() ? CHECKBOX_CHECKED_ENABLED : CHECKBOX_UNCHECKED_ENABLED);
        } else {
            sprite.setTexture(isChecked() ? CHECKBOX_CHECKED_DISABLED : CHECKBOX_UNCHECKED_DISABLED);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

}
