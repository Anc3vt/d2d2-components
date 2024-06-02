/**
 * Copyright (C) 2024 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.components.dev;

import com.ancevt.commons.string.ConvertableString;
import com.ancevt.commons.string.StringLimiter;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.text.BitmapText;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.function.BiConsumer;

public class DevConsole extends Console {

    private Container currentContainer = D2D2.stage();

    @Getter
    @Setter
    private BiConsumer<DevConsole, DisplayObject> debugFunction = (devConsole, o) -> {};

    private DevConsole() {
        addVariableListener("console.height", (varName, value) -> setHeight(value.toFloatOrDefault(getHeight())));
        addVariableListener("stage.bgcolor", (varName, value) -> D2D2.stage().setBackgroundColor(Color.of(value.toString())));
        addVariableListener("cid", (varName, value) -> {
            int id = value.toIntOrDefault(0);

            Container.findDisplayObjectById(D2D2.stage(), id).ifPresentOrElse(
                o -> {
                    currentContainer = (Container) o;
                    print(getPrompt().get());
                },
                () -> print("No such display object with id: " + id, Color.DARK_RED)
            );
        });

        setPrompt(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("<666666>");
            sb.append(currentContainer.getName());
            sb.append("<8080FF>(");
            sb.append(currentContainer.getDisplayObjectId());
            sb.append(")");
            sb.append("<666666>> ");
            return sb.toString();
        });

        addCommand("tree", "t", args -> {
            String typeFilters = args.get(String.class, "-t", "");
            print(treeString(currentContainer, typeFilters), Color.GRAY);
        });

        addCommand("cname", "c", args -> {
            String name = args.next(String.class, "");
            Container.findDisplayObjectByName(D2D2.stage(), name).ifPresentOrElse(
                o -> {
                    setVar("cid", "" + o.getDisplayObjectId());
                },
                () -> print("No such display object with name: " + name, Color.DARK_RED)
            );
        });

        addCommand("run", args -> {
            try {
                BashScriptRunner.runBashScript(args.get(String.class, "-s"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        addCommand("mem", args -> print(MemoryInfo.getMemoryInfo()));

        addCommand("gc", args -> {
            System.gc();
        });

        addCommand("debug", "d", args -> {

            if (!args.hasNext()) {
                debugFunction.accept(this, null);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                D2D2.engine().getDisplayManager().focusWindow();

                return;
            }

            ConvertableString cs = ConvertableString.convert(args.next(String.class, "0"));

            int id = cs.toIntOrSupply(() -> Container.findDisplayObjectByName(D2D2.stage(), cs.toString()).get().getDisplayObjectId());

            Container.findDisplayObjectById(D2D2.stage(), id)
                .ifPresentOrElse(
                    o -> {
                        debugFunction.accept(this, o);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        D2D2.engine().getDisplayManager().focusWindow();
                    },
                    () -> print("No such display object with id: " + id, Color.DARK_RED)
                );
        });

        setMulticolorEnabled(true);
    }

    public String treeString(Container container, String typeFilters) {
        TreeNode<DisplayObject> root = processFillNode(container, typeFilters);


        System.out.println(root.toTreeString());

        return root.toTreeString(treeNode -> {
            DisplayObject o = treeNode.getValue();

            String classSimpleName = o.getClass().getSimpleName();

            StringBuilder sb = new StringBuilder();

            if (o instanceof Container) {
                sb.append("<FF8000>");
            } else {
                sb.append("<FFFFFF>");
            }

            sb.append(classSimpleName);
            sb.append(" <8080FF>");
            sb.append(o.getDisplayObjectId());
            sb.append(" <228022>");
            sb.append(o.getName());

            if (o instanceof Container c) {
                sb.append(" <FFFFFF>size: <FF8000>");
                sb.append(c.getNumChildren());
            }

            if (o instanceof BitmapText bitmapText) {
                sb.append(" <666666>\"");
                sb.append(StringLimiter.limitString(bitmapText.getPlainText(), 100));
                sb.append("\"<FFFFFF>");
            }

            return sb.toString();
        });
    }

    private TreeNode<DisplayObject> processFillNode(DisplayObject o, String typeFilters) {
        TreeNode<DisplayObject> node = TreeNode.of(o);

        if (o instanceof Container c && c != this && c.getClass() != DevConsoleFrame.class) {
            int num = c.getNumChildren();
            for (int i = 0; i < num; i++) {
                DisplayObject child = c.getChild(i);
                if (checkTypeFiltersForClass(child.getClass(), typeFilters)) {
                    node.add(processFillNode(child, typeFilters));
                }
            }
        }

        return node;
    }

    public static boolean checkTypeFiltersForClass(Class<?> clazz, String typeFilters) {
        if (typeFilters == null || typeFilters.isEmpty()) return true;

        String[] keywords = typeFilters.split(",");

        for (String keyword : keywords) {
            Class<?> currentClass = clazz;
            while (currentClass != null) {
                if (currentClass.getName().contains(keyword)) {
                    return true;
                }
                Class<?>[] interfaces = currentClass.getInterfaces();
                for (Class<?> intf : interfaces) {
                    if (intf.getName().contains(keyword)) {
                        return true;
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        }
        return false;
    }


    public static DevConsole init(BiConsumer<DevConsole, DisplayObject> debugFunction) {
        DevConsole devConsole = new DevConsole();
        devConsole.setDebugFunction(debugFunction);
        D2D2.stage().addChild(devConsole, 10, 10);
        return devConsole;
    }

    public static DevConsole init() {
        return init((devConsole, o) -> {});
    }
}
