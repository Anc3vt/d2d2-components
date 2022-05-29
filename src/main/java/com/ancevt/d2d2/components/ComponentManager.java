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
package com.ancevt.d2d2.components;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventDispatcher;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2.D2D2.stage;

public class ComponentManager extends EventDispatcher {

    private static ComponentManager instance;

    public static ComponentManager getInstance() {
        return instance == null ? instance = new ComponentManager() : instance;
    }

    private final List<Component> componentList;

    public ComponentManager() {
        componentList = new CopyOnWriteArrayList<>();
    }

    public void init() {
        stage().addEventListener(Event.RESIZE, event -> resize(stage().getWidth(), stage().getHeight()));
    }

    private void resize(float width, float height) {
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    public int getComponentCount() {
        return componentList.size();
    }

    public void registerComponent(Component component) {
        componentList.add(component);
    }

    public void unregisterComponent(Component component) {
        componentList.remove(component);
    }
}
