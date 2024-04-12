package com.ancevt.d2d2.components.dev.chat;


import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LwjglBackend;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.LifecycleEvent;
import com.ancevt.util.args.Args;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class ConsoleChat extends Chat {

    private static final float PADDING = 20;

    private final Map<String, Consumer<Args>> commands = new HashMap<>();

    private boolean maximized;

    public ConsoleChat(String dirInUserHome) {
        super(dirInUserHome);
        setInputEnabled(true);
        addEventListener(ChatEvent.CHAT_TEXT_ENTER, this::this_chatTextEnter);
        addEventListener(ChatEvent.CHAT_INPUT_CLOSE, this::this_chatInputClose);
        D2D2.stage().addEventListener(this, LifecycleEvent.EXIT_MAIN_LOOP, this::stage_exit);
        openInput();
        loadContent();

        commands.put("/q", a -> D2D2.exit());
        commands.put("/cls", a -> clear());
    }

    private void stage_exit(Event event) {
        saveHistory();
        saveContent();
    }

    private void loadContent() {
        getDir().checkExists("content").ifPresent(relativePath -> {
            String content = getDir().readString(relativePath);
            content.lines().forEach(s -> {
                ChatMessage chatMessage = ChatMessageJsonConverter.jsonToChatMessage(s);
                addMessage(chatMessage);
            });
        });
    }

    private void saveContent() {
        StringBuilder sb = new StringBuilder();

        getMessages().forEach(m -> {
            sb.append(ChatMessageJsonConverter.chatMessageToJson(m));
            sb.append("\n");
        });

        getDir().writeString(sb.toString(), "content");
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized) return;
        this.maximized = maximized;

        Stage stage = D2D2.stage();
        stage.removeEventListener(this, Event.RESIZE);

        if (maximized) {
            stage.addEventListener(this, Event.RESIZE, this::stage_resize);
            stage_resize(null);
            stage.setXY(PADDING, PADDING);
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
        Consumer<Args> func = commands.get(args.next());
        addMessage("$> " + text, Color.GRAY);
        if (func != null) {
            func.accept(args);
        } else {
            addMessage("Unknown command: " + text, Color.RED);
        }
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LwjglBackend(1000, 800, "D2D2 Application"));
        StarletSpace.haveFun(false);

        ComponentAssets.init();

        ConsoleChat consoleChat = new ConsoleChat(".d2d2-console-chat");
        consoleChat.setMaximized(true);


        stage.add(consoleChat);
        stage.add(new FpsMeter());
        D2D2.loop();
    }

}
