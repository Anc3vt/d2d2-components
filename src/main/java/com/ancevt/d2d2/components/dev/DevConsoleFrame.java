/**
 * Copyright (C) 2025 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2.components.dev;

import com.ancevt.commons.fs.IsolatedDirectory;
import com.ancevt.commons.fs.IsolatedDirectoryDictionaryUtil;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.components.Frame;
import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.scene.Group;
import com.ancevt.d2d2.scene.Node;
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

    public DevConsoleFrame(BiConsumer<DevConsole, Node> debugFunction) {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle("DevConsole");
        setManualResizable(true);
        setMinSize(100, 100);

        console = DevConsole.init(debugFunction);

        console.addVariableListener("console.alpha", "0.75f", (s, convertableString) -> {
            setAlpha(convertableString.toFloatOrDefault(0.75f));
        });

        getContentPanel().addChild(console);

        addEventListener(this, CommonEvent.Resize.class, e -> onResize());
        initTilda();
        onResize();

        D2D2.stage().addEventListener(this, CommonEvent.Stop.class, this::stage_exitMainLoop);

        addEventListener(this, InputEvent.MouseUp.class, event -> console.textInput.focus());

        setX(20);



        loadState();
    }

    private void stage_exitMainLoop(CommonEvent.Stop event) {
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

            this.setPosition(
                    convert(map.get("x")).toFloatOrDefault(40),
                    convert(map.get("y")).toFloatOrDefault(40)
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
        D2D2.stage().addEventListener(this, InputEvent.KeyDown.class, e -> {
            if (e.getKeyCode() == KeyCode.TILDA && e.isShift()) {
                setVisible(!isVisible());
                Timer.setTimeout(10, t -> {
                    String text = console.textInput.getText();
                    if (text.endsWith("`") ||
                            text.endsWith("ё") ||
                            text.endsWith("Ё") ||
                            text.endsWith("~")) {
                        text = text.substring(0, text.length() - 1);
                        console.textInput.setText(text);
                    }
                });
                if (getX() > D2D2.stage().getWidth() || getX() < -getWidth()) {
                    setX(10);
                }
                if (getY() > D2D2.stage().getHeight() || getY() < 0) {
                    setY(10);
                }
            }
        });

        if (hasParent()) {
            Group parent = getParent();
            removeFromParent();
            parent.addChild(this);
        }
    }

    public DevConsoleFrame() {
        this((devConsole, displayObject) -> {
        });
    }

    private void onResize() {
        console.setSize(getContentPanel().getWidth() - 20, getContentPanel().getHeight() - 48);
    }

    public static DevConsole init(BiConsumer<DevConsole, Node> debugFunction) {
        DevConsoleFrame devConsoleFrame = new DevConsoleFrame(debugFunction);
        D2D2.stage().addChild(devConsoleFrame);
        return devConsoleFrame.getConsole();
    }

    public static DevConsole init() {
        return init((devConsole, displayObject) -> {
        });
    }

}
