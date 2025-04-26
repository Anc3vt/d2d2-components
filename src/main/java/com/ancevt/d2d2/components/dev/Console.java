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


import com.ancevt.commons.exception.StackTraceUtil;
import com.ancevt.commons.string.ConvertableString;
import com.ancevt.commons.string.TextTable;
import com.ancevt.commons.util.ApplicationMainClassNameExtractor;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.Disposable;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Scene;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.event.LifecycleEvent;
import com.ancevt.util.args.Args;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Getter
@Slf4j
public class Console extends Chat implements Disposable {

    private static final float PADDING = 20;

    private static final Object NO_INITIAL_VALUE = new Object();

    private final List<Command> commands = new ArrayList<>();

    private final Map<String, String> initialValues = new HashMap<>();

    @Getter
    private final Map<String, String> context = new TreeMap<>();

    @Getter
    private final String consoleName;

    private boolean maximized;

    private boolean disposed;

    @Getter
    @Setter
    private Supplier<String> prompt = () -> "$ ";

    public Console(String consoleName) {
        super(consoleName);

        this.consoleName = consoleName;
        setInputEnabled(true);
        addEventListener(ChatEvent.CHAT_TEXT_ENTER, this::this_chatTextEnter);
        addEventListener(ChatEvent.CHAT_INPUT_CLOSE, this::this_chatInputClose);
        D2D2.stage().addEventListener(this, LifecycleEvent.START_MAIN_LOOP, this::stage_startMainLoop);
        D2D2.stage().addEventListener(this, LifecycleEvent.EXIT_MAIN_LOOP, this::stage_exitMainLoop);
        openInput();
        loadOutputHistory();

        commands.add(new Command("/help", "/h", "Show help", a -> showHelp(), true));
        commands.add(new Command("/cls", null, "Clear console output", a -> clear(), true));
        commands.add(new Command("/exit", "/q", "Exit D2D2 loop", a -> D2D2.exit(), true));
        commands.add(new Command("/var", "/v", "Define and print variable value", this::commandVar, true));
        commands.add(new Command("/delete", "/d", "Delete variable", this::deleteVar, true));

    }

    private void stage_startMainLoop(Event event) {
        loadVariables();
    }

    private void stage_exitMainLoop(Event event) {
        saveInputHistory();
        saveOutputHistory();
        saveVariables();
    }

    public Console() throws ApplicationMainClassNameExtractor.MainClassNameExtractorException {
        this(".d2d2/console/" + ApplicationMainClassNameExtractor.getMainClassName());
    }

    public ConvertableString getVar(String varName) {
        return ConvertableString.convert(context.get(varName));
    }

    public Console setVar(String variable, String value) {
        context.put(variable, value);
        dispatchEvent(ConsoleEvent.builder()
            .type(ConsoleEvent.VAR_VALUE_CHANGE)
            .varName(variable)
            .oldValue(ConvertableString.convert(null))
            .value(ConvertableString.convert(value))
            .build()
        );
        return this;
    }

    public Console addVariableListener(String variable, BiConsumer<String, ConvertableString> listener) {
        return addVariableListener(variable, NO_INITIAL_VALUE, listener);
    }

    public Console addVariableListener(String variable, Object initialValue, BiConsumer<String, ConvertableString> listener) {
        addEventListener("console_" + variable, ConsoleEvent.VAR_VALUE_CHANGE, event -> {
            ConsoleEvent e = event.casted();
            if (Objects.equals(e.getVarName(), variable)) {
                try {
                    listener.accept(variable, e.getValue());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    println(StackTraceUtil.stringify(ex), Color.RED);
                }
            }
        });

        if (initialValue != NO_INITIAL_VALUE) {
            initialValues.put(variable, initialValue == null ? null : String.valueOf(initialValue));
        }

        return this;
    }

    public void removeVariableListener(String varName) {
        removeEventListener("console_" + varName, ConsoleEvent.VAR_VALUE_CHANGE);
    }

    private void deleteVar(Args args) {
        String varName = args.next();
        String oldValue = context.get(varName);
        if (oldValue != null) {
            context.remove(varName);
            println("delete '" + varName + "', old value: '" + oldValue + "'");
            dispatchEvent(ConsoleEvent.builder()
                .type(ConsoleEvent.VAR_VALUE_CHANGE)
                .varName(varName)
                .oldValue(ConvertableString.convert(oldValue))
                .value(ConvertableString.empty())
                .build()
            );
            dispatchEvent(ConsoleEvent.builder()
                .type(ConsoleEvent.VAR_VALUE_DELETE)
                .varName(varName)
                .oldValue(ConvertableString.convert(oldValue))
                .value(ConvertableString.empty())
                .build()
            );
        } else {
            println("no such variable '" + varName + "'");
        }
    }

    private void commandVar(Args args) {
        if (!args.hasNext()) {
            context.forEach((k, v) -> println(k + "=" + v));
        } else {
            String varName = args.next();
            if (args.hasNext()) {
                String oldValue = context.get(varName);
                String newValue = args.next();
                setVar(varName, newValue);
                println(varName + "=" + oldValue + "->" + newValue);
            } else {
                println(varName + "=" + context.get(varName));
            }
        }
    }

    private void showHelp() {
        TextTable textTable = new TextTable(false, "command", "alias", "description");
        commands.stream()
            .filter(c -> !c.builtIn)
            .forEach(c -> textTable.addRow(c.command, c.alias, c.description));
        println(textTable.render());
    }

    public Console addCommand(String command, String alias, String description, Consumer<Args> func) {
        if (alias == null) alias = "";
        if (description == null) description = "";
        commands.add(new Command(command, alias, description, func, false));
        return this;
    }

    public Console addCommand(String command, String alias, Consumer<Args> func) {
        addCommand(command, alias, null, func);
        return this;
    }

    public Console addCommand(String command, Consumer<Args> func) {
        addCommand(command, null, null, func);
        return this;
    }

    public Console removeCommand(String commandOrAlias) {
        Command commandToRemove = null;
        for (Command c : commands) {
            if (Objects.equals(c.command, commandOrAlias) ||
                Objects.equals(c.alias, commandOrAlias)) {
                commandToRemove = c;
                break;
            }
        }
        if (commandToRemove == null) {
            throw new IllegalStateException("Command not found: %s".formatted(commandOrAlias));
        }
        commands.remove(commandToRemove);
        return this;
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized) return;
        this.maximized = maximized;

        Scene scene = D2D2.stage();
        scene.removeEventListener(this, Event.RESIZE);

        if (maximized) {
            scene.addEventListener(this, Event.RESIZE, this::stage_resize);
            stage_resize(null);
            scene.setXY(PADDING, PADDING);
        }
    }

    private void stage_resize(Event event) {
        setWidth(D2D2.stage().getWidth() - PADDING * 2);
        setHeight(D2D2.stage().getHeight() - PADDING * 2);
    }

    private void this_chatInputClose(Event event) {
        openInput();
    }

    private void this_chatTextEnter(Event event) {
        ChatEvent e = event.casted();
        String text = e.getText();

        Args args = Args.of(text);
        addMessage(prompt.get() + text, Color.GRAY);

        if (checkSetVariablePattern(text)) {
            String[] s = text.split("=", 2);
            setVar(s[0], s[1]);
            return;
        }

        String cmdWord = args.next();

        for (Command c : commands) {
            if (Objects.equals(c.command, cmdWord) ||
                Objects.equals(c.alias, cmdWord)) {
                try {
                    c.func.accept(args);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    println(StackTraceUtil.stringify(ex), Color.RED);
                }
                return;
            }
        }
        println("Unknown command: " + args.get(String.class, 0), Color.RED);
    }

    public static boolean checkSetVariablePattern(String input) {
        return Pattern.matches("[a-zA-Z_$][a-zA-Z\\d_$\\.]*=.*", input);
    }

    private void loadOutputHistory() {
        getIsolatedDirectory().checkExists("outputhistory").ifPresent(relativePath -> {
            String content = getIsolatedDirectory().readString(relativePath);
            content.lines().forEach(s -> {
                ChatMessage chatMessage = ChatMessageJsonConverter.jsonToChatMessage(s);
                addMessage(chatMessage);
            });
        });
    }

    private void saveOutputHistory() {
        StringBuilder sb = new StringBuilder();

        getMessages().forEach(m -> {
            sb.append(ChatMessageJsonConverter.chatMessageToJson(m));
            sb.append("\n");
        });

        getIsolatedDirectory().writeString(sb.toString(), "outputhistory");
    }

    public void loadVariables() {
        getIsolatedDirectory().checkExists("variables").ifPresent(relativePath -> {
            String contextData = getIsolatedDirectory().readString(relativePath);
            Map<String, String> map = JsonEngine.gson().fromJson(
                contextData,
                new TypeToken<Map<String, String>>() {}.getType()
            );

            context.putAll(map);
            map.forEach(this::setVar);
        });

        initialValues.forEach((k, v) -> {
            if (!context.containsKey(k)) {
                setVar(k, v);
            }
        });
    }

    private void saveVariables() {
        String contextData = JsonEngine.gson().toJson(context);
        getIsolatedDirectory().writeString(contextData, "variables");
    }

    @Override
    public void dispose() {
        D2D2.stage().removeEventListener(this, InteractiveEvent.KEY_DOWN);
        D2D2.stage().removeEventListener(this, LifecycleEvent.START_MAIN_LOOP);
        D2D2.stage().removeEventListener(this, LifecycleEvent.EXIT_MAIN_LOOP);
        disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @RequiredArgsConstructor
    private static class Command {
        private final String command;
        private final String alias;
        private final String description;
        private final Consumer<Args> func;
        private final boolean builtIn;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class JsonEngine {
        private static Gson gson;

        private static Gson gson() {
            if (gson == null) {
                gson = new GsonBuilder().setPrettyPrinting().create();
            }
            return gson;
        }
    }
}
