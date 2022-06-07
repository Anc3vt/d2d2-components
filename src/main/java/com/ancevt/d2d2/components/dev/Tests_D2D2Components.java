/**
 * Copyright (C) 2022 the original author or authors.
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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.Button;
import com.ancevt.d2d2.components.ButtonEx;
import com.ancevt.d2d2.components.Checkbox;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.components.DropDownList;
import com.ancevt.d2d2.components.Panel;
import com.ancevt.d2d2.components.ScrollPane;
import com.ancevt.d2d2.components.TextInput;
import com.ancevt.d2d2.components.Tooltip;
import com.ancevt.d2d2.debug.StarletSpace;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.MouseButton;
import com.ancevt.d2d2.interactive.DragUtil;

import static com.ancevt.d2d2.D2D2.getTextureManager;
import static com.ancevt.d2d2.D2D2.init;
import static com.ancevt.d2d2.D2D2.loop;
import static com.ancevt.d2d2.components.menu.Menu.createMenu;

public class Tests_D2D2Components {

    public static void main(String[] args) {
        Stage stage = init(new LWJGLBackend(800, 600, "(floating)"));
        StarletSpace.haveFun();
        ComponentAssets.load();

        Panel panel = new Panel();
        panel.setSize(700, 550);
        stage.add(panel, 10, 10);



        DropDownList<Integer> dropDownList = new DropDownList<>();
        dropDownList.setPushEventsUp(false);
        for (int i = 0; i < 10; i++) {
            String text = Math.random() + "";
            dropDownList.addItem(text, i);
        }
        panel.add(dropDownList, 10, 50);

        DropDownList<Integer> dropDownList1 = new DropDownList<>();
        dropDownList1.setPushEventsUp(false);
        for (int i = 0; i < 10; i++) {
            String text = Math.random() + "";
            dropDownList1.addItem(text, i);
        }
        panel.add(dropDownList1, 10, 80);





        for (int i = 0; i < 3; i++) {
            Checkbox checkbox = new Checkbox("Test checkbox #%d".formatted(i));
            panel.add(checkbox, 180, 50 + i * 30);
        }




        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setWidth(150);
        scrollPane.getPadding().setRight(10);
        panel.add(scrollPane, 10, 150);
        for (int i = 0; i < 100; i++) {
            Button button = new Button("Test " + i);
            button.setWidth(200);
            scrollPane.addScrollableItem(button);
        }


        TextInput textInput = new TextInput();
        textInput.setText("Text input");
        panel.add(textInput, 370, 50);




        ButtonEx b2 = new ButtonEx();
        b2.setToggleMode(true);
        b2.setIcon(D2D2.getTextureManager().getTexture("satellite"));
        b2.setSize(60, 60);
        b2.setTabbingEnabled(true);
        panel.add(b2, 370, 100);

        ButtonEx b3 = new ButtonEx();
        b3.setIcon(D2D2.getTextureManager().getTexture("satellite"));
        b3.setSize(60, 60);
        b3.setTabbingEnabled(true);
        panel.add(b3, 370 + 70, 100);





        Tooltip tooltip = Tooltip.createTooltip();
        tooltip.setTexture(getTextureManager().getTexture("satellite"));
        tooltip.setText("""
                #This is a tooltip ϕϕϕϕϕϕϕ
                                
                <FF8000>Second line
                                
                <BBBBBB>Third line
                One more line
                And again""");
        tooltip.setImageScale(2f);
        b3.setTooltip(tooltip);


        stage.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            var e = (InputEvent) event;

            if (e.getMouseButton() == MouseButton.RIGHT) {
                createMenu()
                        .addItem("first", () -> System.out.println("1"))
                        .addItem("second", () -> System.out.println("2"))
                        .addSeparator()
                        .addItem("third", createMenu()
                                .addItem("second level first", () -> System.out.println("2"))
                                .addItem("second level second", createMenu()
                                        .addItem("third level first", () -> System.out.println("2"))
                                        .addItem("third level second", () -> System.out.println("2"))
                                        .addItem("third level third", () -> System.out.println("2"))
                                )
                                .addItem("second level third", () -> System.out.println("4"))
                        )
                        .addItem("fourth", createMenu()
                                .addItem("second level first", () -> System.out.println("5"))
                                .addItem("second level second", () -> System.out.println("6"))
                                .addItem("second level third", () -> System.out.println("7"))
                        ).activate();
            }
        });


        DragUtil.enableDrag(panel);

        loop();
    }
}
