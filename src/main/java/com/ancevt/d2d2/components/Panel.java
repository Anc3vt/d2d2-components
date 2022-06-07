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

import com.ancevt.d2d2.common.PlainRect;

public class Panel extends Component {

    private static final float DEFAULT_WIDTH = 400.0f;
    private static final float DEFAULT_HEIGHT = 200.0f;

    private final PlainRect bg;

    public Panel() {
        bg = new PlainRect();
        add(bg);
        bg.setAlpha(Component.PANEL_BG_ALPHA);
        bg.setColor(Component.BACKGROUND_COLOR);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setFocusRectEnabled(false);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bg.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bg.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bg.setSize(width, height);
    }

    @Override
    public void update() {

    }
}
