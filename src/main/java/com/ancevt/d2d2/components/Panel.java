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

package com.ancevt.d2d2.components;

import com.ancevt.d2d2.event.CommonEvent;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.shape.RectangleShape;

public class Panel extends Component {

    private final float DEFAULT_WIDTH = 200.0f;
    private final float DEFAULT_HEIGHT = 200.0f;

    private final RectangleShape bg;

    public Panel() {
        setTabbingEnabled(false);

        bg = new RectangleShape(1, 1, Color.BLACK);
        bg.setAlpha(Component.PANEL_BG_ALPHA);
        addChild(bg);

        addEventListener(Panel.class, CommonEvent.Resize.class, this::this_resize);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void this_resize(CommonEvent.Resize event) {
        bg.setSize(getWidth(), getHeight());
    }

}
