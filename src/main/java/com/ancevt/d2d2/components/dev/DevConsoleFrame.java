package com.ancevt.d2d2.components.dev;

import com.ancevt.commons.fs.IsolatedDirectory;
import com.ancevt.commons.fs.IsolatedDirectoryDictionaryUtil;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.components.ComponentEvent;
import com.ancevt.d2d2.components.Frame;
import com.ancevt.d2d2.display.IContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.event.LifecycleEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.ancevt.commons.string.ConvertableString.convert;

public class DevConsoleFrame extends Frame {

    private static final float DEFAULT_WIDTH = 500f;
    private static final float DEFAULT_HEIGHT = 300f;

    @Getter
    private final DevConsole console;

    public DevConsoleFrame(BiConsumer<DevConsole, IDisplayObject> debugFunction) {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle("DevConsole");
        setManualResizable(true);
        setMinSize(100, 100);

        console = DevConsole.init(debugFunction);

        console.addVariableListener("console.alpha", "0.75f", (s, convertableString) -> {
            setAlpha(convertableString.toFloatOrDefault(0.75f));
        });

        getContentPanel().add(console);

        addEventListener(this, ComponentEvent.RESIZE, e -> onResize());
        initTilda();
        onResize();

        D2D2.stage().addEventListener(this, LifecycleEvent.EXIT_MAIN_LOOP, this::stage_exitMainLoop);

        addEventListener(this, InteractiveEvent.UP, event -> {
            console.textInput.focus();
        });

        loadState();
    }

    private void stage_exitMainLoop(Event event) {
        saveState();
    }

    private void loadState() {
        IsolatedDirectory dir = console.getIsolatedDirectory();

        dir.checkExists("framestate").ifPresent(filename -> {
            Map<String, Object> map = IsolatedDirectoryDictionaryUtil.read(dir, filename);
            setSize(
                convert(map.get("w")).toFloatOrDefault(DEFAULT_WIDTH),
                convert(map.get("h")).toFloatOrDefault(DEFAULT_HEIGHT)
            );

            setXY(
                convert(map.get("x")).toFloatOrDefault(10),
                convert(map.get("y")).toFloatOrDefault(10)
            );

            setAlpha(
                convert(map.get("alpha")).toFloatOrDefault(1f)
            );
        });
    }

    private void saveState() {
        IsolatedDirectoryDictionaryUtil.write(
            console.getIsolatedDirectory(),
            Map.of(
                "w", getWidth(),
                "h", getHeight(),
                "x", getX(),
                "y", getY(),
                "alpha", getAlpha()
            ),
            "framestate"
        );
    }

    private void initTilda() {
        D2D2.stage().addEventListener(this, InteractiveEvent.KEY_DOWN, event -> {
            InteractiveEvent e = event.casted();
            if (e.getKeyCode() == KeyCode.TILDA && e.isShift()) {
                setVisible(!isVisible());
                Timer.setTimeout(t -> {
                    String text = console.textInput.getText();
                    if (text.endsWith("`") ||
                        text.endsWith("ё") ||
                        text.endsWith("Ё") ||
                        text.endsWith("~")) {
                        text = text.substring(0, text.length() - 1);
                        console.textInput.setText(text);
                    }
                }, 10);
                setXY(10, 10);
            }
        });

        if (hasParent()) {
            IContainer parent = getParent();
            removeFromParent();
            parent.add(this);
        }
    }

    public DevConsoleFrame() {
        this((devConsole, displayObject) -> {});
    }

    private void onResize() {
        console.setSize(getContentPanel().getWidth() - 20, getContentPanel().getHeight() - 48);
    }

    public static DevConsole init(BiConsumer<DevConsole, IDisplayObject> debugFunction) {
        DevConsoleFrame devConsoleFrame = new DevConsoleFrame(debugFunction);
        D2D2.stage().add(devConsoleFrame);
        return devConsoleFrame.getConsole();
    }

    public static DevConsole init() {
        return init((devConsole, displayObject) -> {});
    }

}
