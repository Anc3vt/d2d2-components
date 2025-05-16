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

import com.ancevt.commons.string.ConvertableString;
import com.ancevt.commons.string.StringLimiter;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Group;
import com.ancevt.d2d2.scene.Node;
import com.ancevt.d2d2.scene.text.BitmapText;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.function.BiConsumer;

public class DevConsole extends Console {

    private Group currentGroup = D2D2.root();

    @Getter
    @Setter
    private BiConsumer<DevConsole, Node> debugFunction = (devConsole, o) -> {
    };

    private DevConsole() {
        addVariableListener("console.height", (varName, value) -> setHeight(value.toFloatOrDefault(getHeight())));
        addVariableListener("stage.bgcolor", (varName, value) -> D2D2.root().setBackgroundColor(Color.of(value.toString())));
        addVariableListener("cid", (varName, value) -> {
            int id = value.toIntOrDefault(0);

            Group.findNodeById(D2D2.root(), id).ifPresentOrElse(
                    o -> {
                        currentGroup = (Group) o;
                        println(getPrompt().get());
                    },
                    () -> println("No such display object with id: " + id, Color.DARK_RED)
            );
        });

        setPrompt(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("<666666>");
            sb.append(currentGroup.getName());
            sb.append("<8080FF>(");
            sb.append(currentGroup.getNodeId());
            sb.append(")");
            sb.append("<666666>> ");
            return sb.toString();
        });

        addCommand("tree", "t", args -> {
            String typeFilters = args.get(String.class, "-t", "");
            println(treeString(currentGroup, typeFilters), Color.GRAY);
        });

        addCommand("cname", "c", args -> {
            String name = args.next(String.class, "");
            Group.findNodeByName(D2D2.root(), name).ifPresentOrElse(
                    o -> {
                        setVar("cid", "" + o.getNodeId());
                    },
                    () -> println("No such display object with name: " + name, Color.DARK_RED)
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

        addCommand("mem", args -> println(MemoryInfo.getMemoryInfo()));

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

                D2D2.engine().displayManager().focusWindow();

                return;
            }

            ConvertableString cs = ConvertableString.convert(args.next(String.class, "0"));

            int id = cs.toIntOrSupply(() -> Group.findNodeByName(D2D2.root(), cs.toString()).get().getNodeId());

            Group.findNodeById(D2D2.root(), id)
                    .ifPresentOrElse(
                            o -> {
                                debugFunction.accept(this, o);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                D2D2.engine().displayManager().focusWindow();
                            },
                            () -> println("No such display object with id: " + id, Color.DARK_RED)
                    );
        });

        setMulticolorEnabled(true);

    }

    public String treeString(Group group, String typeFilters) {
        TreeNode<Node> root = processFillNode(group, typeFilters);

        return root.toTreeString(treeNode -> {
            Node o = treeNode.getValue();

            String classSimpleName = o.getClass().getSimpleName();

            StringBuilder sb = new StringBuilder();

            if (o instanceof Group) {
                sb.append("<FF8000>");
            } else {
                sb.append("<FFFFFF>");
            }

            sb.append(classSimpleName);
            sb.append(" <8080FF>");
            sb.append(o.getNodeId());
            sb.append(" <228022>");
            sb.append(o.getName());

            if (o instanceof Group c) {
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

    private TreeNode<Node> processFillNode(Node o, String typeFilters) {
        TreeNode<Node> node = TreeNode.of(o);

        if (o instanceof Group c && c != this && c.getClass() != DevConsoleFrame.class) {
            int num = c.getNumChildren();
            for (int i = 0; i < num; i++) {
                Node child = c.getChild(i);
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


    public static DevConsole init(BiConsumer<DevConsole, Node> debugFunction) {
        DevConsole devConsole = new DevConsole();
        devConsole.setDebugFunction(debugFunction);
        D2D2.root().addChild(devConsole, 10, 10);
        return devConsole;
    }

    public static DevConsole init() {
        return init((devConsole, o) -> {
        });
    }
}
