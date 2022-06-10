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

import java.util.ArrayList;
import java.util.List;

public class FrameManager {

    private static FrameManager instance;

    public static FrameManager getInstance() {
        return instance == null ? instance = new FrameManager() : instance;
    }

    private final List<Component> frameList;

    private FrameManager() {
        frameList = new ArrayList<>();
    }

    public void register(Component component) {
        frameList.add(component);
    }

    public void unregister(Component component) {
        frameList.remove(component);
    }

    public boolean hitTest(float x, float y) {
        return hitTest((int) x, (int) y);
    }

    public boolean hitTest(int x, int y) {
        for (Component component : frameList) {
            if (component.isOnScreen() && component.isVisible() &&
                    x >= component.getX() && x < component.getX() + component.getWidth() &&
                    y >= component.getY() && y < component.getY() + component.getHeight()) {

                return true;
            }
        }

        return false;
    }
}
