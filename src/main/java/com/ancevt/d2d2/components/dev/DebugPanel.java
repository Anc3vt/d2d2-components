package com.ancevt.d2d2.components.dev;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.MouseButton;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Group;
import com.ancevt.d2d2.scene.interactive.InteractiveGroup;
import com.ancevt.d2d2.scene.shape.BorderedRectangleShape;
import com.ancevt.d2d2.scene.text.BitmapText;
import com.ancevt.d2d2.time.Timer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugPanel extends InteractiveGroup {

    private static final int MIN_WIDTH = 20;
    private static final int MIN_HEIGHT = 20;

    private static final Map<String, DebugPanel> debugPanels = new HashMap<>();

    @Setter
    @Getter
    private static boolean enabled;
    private final String systemPropertyName;
    private final Timer timer;

    private int mouseButton;

    private int oldX;
    private int oldY;

    private final BorderedRectangleShape bg;
    private final BitmapText bitmapText;

    private final List<Button> buttonList = new ArrayList<>();
    private final Map<String, Button> buttonMap = new HashMap<>();

    private DebugPanel(String systemPropertyName) {
        this.systemPropertyName = systemPropertyName;
        bg = new BorderedRectangleShape(100, 100, Color.of(0x001122), Color.WHITE);
        addChild(bg);

        bitmapText = new BitmapText();
        bitmapText.setMulticolor(true);
        bitmapText.setSizeAs(bg);
        addChild(bitmapText);

        onMouseDown(this::mouseDown);
        onMouseDrag(this::mouseDrag);

        setSizeAs(bg);

        loadIfStateFileExists();

        timer = Timer.setInterval(200, timer -> {
            if (isOnScreen()) updateText();

            fixPosition();
        });

        debugPanels.put(systemPropertyName, this);

        stage.onStop(e -> saveAll());
    }

    private void fixPosition() {
        if (getX() + getWidth() > stage.getWidth()) {
            setX(stage.getWidth() - getWidth());
        }
        if (getY() + getHeight() > stage.getHeight()) {
            setY(stage.getHeight() - getHeight());
        }
        if (getX() < 0) {
            setX(0);
        }
        if (getY() < 0) {
            setY(0);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        timer.stop();
    }

    public DebugPanel set(Object value) {
        String valueString = String.valueOf(value);
        System.setProperty(systemPropertyName, valueString);
        updateText();
        return this;
    }

    public void updateText() {
        bitmapText.setText("<999999>" +
                systemPropertyName +
                ":\n<FFFFFF>" +
                System.getProperty(systemPropertyName));
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bg.setWidth(width);
        bitmapText.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bg.setHeight(height);
        bitmapText.setHeight(height);
    }

    private void mouseDown(InputEvent.MouseDown e) {
        mouseButton = e.getButton();

        oldX = (int) (e.getX() + getX());
        oldY = (int) (e.getY() + getY());

        Group parent = getParent();
        parent.removeChild(this);
        parent.addChild(this);

        focus();
    }

    private void mouseDrag(InputEvent.MouseDrag e) {
        if (mouseButton == MouseButton.RIGHT) {
            setSize(e.getX() / getScaleX() + 1, e.getY() / getScaleY() + 1);

            if (getWidth() < MIN_WIDTH) {
                setWidth(MIN_WIDTH);
            }
            if (getHeight() < MIN_HEIGHT) {
                setHeight(MIN_HEIGHT);
            }
            return;
        }

        final int tx = (int) (e.getX() + getX());
        final int ty = (int) (e.getY() + getY());

        move(tx - oldX, ty - oldY);

        oldX = tx;
        oldY = ty;
    }


    private void loadIfStateFileExists() {
        File f = file();
        if (f.exists()) {
            System.out.printf("DebugPanel \"%s\" loaded from file %s%n", systemPropertyName, f.getAbsolutePath());

            String string = readFromFile(f);
            JsonObject o = JsonParser.parseString(string).getAsJsonObject();
            float x = o.get("x").getAsFloat();
            float y = o.get("y").getAsFloat();
            float w = o.get("w").getAsFloat();
            float h = o.get("h").getAsFloat();
            String data = o.get("data").getAsString();

            bg.setSize(w, h);
            bitmapText.setSize(w, h);
            setSize(w, h);
            bitmapText.setText(data);
            setPosition(x, y);
        }
    }

    private void save() {
        JsonObject o = new JsonObject();
        o.addProperty("x", getX());
        o.addProperty("y", getY());
        o.addProperty("w", getWidth());
        o.addProperty("h", getHeight());
        o.addProperty("data", bitmapText.getText());
        saveToFile(file(), o.toString());
    }

    private File file() {
        return file(MD5.hash(systemPropertyName) + ".json");
    }

    private void saveToFile(File file, String string) {
        try {
            Files.writeString(
                    Path.of(file.getAbsolutePath()),
                    string,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            System.out.printf("DebugPanel \"%s\" saved to file %s%n", systemPropertyName, file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readFromFile(File file) {
        try {
            return Files.readString(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private static File directory() {
        File dir = new File(
                System.getProperty("user.home")
                        + File.separator
                        + ".d2d2-debug-panel"
                        + File.separator
                        + ApplicationMainClassNameExtractor.getMainClassName()
        );

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File file(String name) {
        return new File(directory().getAbsolutePath() + File.separator + name);
    }

    public DebugPanel addButton(String text, Runnable onPress) {
        if (!buttonMap.containsKey(text)) {
            Button button = new Button(text);
            button.pressFunction = onPress;
            addChild(button, buttonList.size() * (Button.DEFAULT_WIDTH + 1), -Button.DEFAULT_HEIGHT);
            buttonList.add(button);
            buttonMap.put(text, button);
        }

        return this;
    }

    public static void saveAll() {
        debugPanels.values().forEach(DebugPanel::save);
    }


    public static DebugPanel show(String systemPropertyName) {
        DebugPanel result = new DebugPanel(systemPropertyName);

        if (enabled) {
            D2D2.getStage().addChild(result);
        }

        return result;
    }


    public static class Button extends BorderedRectangleShape {

        private static final float DEFAULT_WIDTH = 50f;
        private static final float DEFAULT_HEIGHT = 12f;

        private final InteractiveGroup interactiveButton;

        private Runnable pressFunction;

        public Button(Object text) {
            super(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.of(0x112233), Color.WHITE);
            interactiveButton = new InteractiveGroup(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            BitmapText bitmapText = new BitmapText();
            bitmapText.setText(String.valueOf(text));
            interactiveButton.onMouseDown(e -> {
                if (pressFunction != null) {
                    pressFunction.run();
                }
            });
            addChild(interactiveButton);
            addChild(bitmapText, 2, -2);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApplicationMainClassNameExtractor {
        /**
         * Retrieves the fully qualified name of the main class of the application.
         *
         * @return The fully qualified name of the main class.
         * @throws ApplicationMainClassNameExtractor.MainClassNameExtractorException if the main class name cannot be extracted reliably.
         */
        public static String getMainClassName() throws ApplicationMainClassNameExtractor.MainClassNameExtractorException {
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
                Thread thread = entry.getKey();
                if (thread.getId() == 1) {
                    StackTraceElement[] stackTraceElements = entry.getValue();
                    for (int i = stackTraceElements.length - 1; i >= 0; i--) {
                        StackTraceElement stackTraceElement = stackTraceElements[i];
                        if (stackTraceElement.getMethodName().equals("main")) {
                            return stackTraceElement.getClassName();
                        }
                    }
                }
            }
            throw new ApplicationMainClassNameExtractor.MainClassNameExtractorException("Unable to extract application main class name");
        }

        public static class MainClassNameExtractorException extends RuntimeException {

            public MainClassNameExtractorException(String message) {
                super(message);
            }
        }
    }


    public class MD5 {
        public static byte[] hash(byte[] bytes) {
            try {
                return MessageDigest.getInstance("MD5").digest(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static String hash(String string) {
            return bytesToHex(hash(string.getBytes(StandardCharsets.UTF_8)));
        }

        public static String hashFile(String path) {
            try {
                return bytesToHex(hash(Files.readAllBytes(Paths.get(path))));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public static String hashFile(Path file) {
            try {
                return bytesToHex(hash(Files.readAllBytes(file)));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }
}
