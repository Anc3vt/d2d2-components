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

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;

public class Component extends DisplayObjectContainer {

    private float width;
    private float height;
    private boolean enabled;
    private boolean disposed;

    public Component() {
        addEventListener(Component.class, Event.ADD_TO_STAGE, this::this_addToStage);
    }

    private void this_addToStage(Event event) {
        ComponentManager.getInstance().registerComponent(this);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    public void setWidth(float width) {
        this.width = width;
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    public void setHeight(float height) {
        this.height = height;
        dispatchEvent(Event.builder().type(Event.RESIZE).build());
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void dispose() {
        this.disposed = true;
        ComponentManager.getInstance().unregisterComponent(this);
        removeFromParent();
        removeEventListener(Component.class, Event.ADD_TO_STAGE);
    }

    public boolean isDisposed() {
        return disposed;
    }
}
