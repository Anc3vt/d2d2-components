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

import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import lombok.Getter;


public abstract class TextInputEvent extends Event {

    @EventPooled
    @Getter
    public static final class TextChange extends TextInputEvent {
        private String text;

        public static TextChange create(String text) {
            var e = EventPool.obtain(TextChange.class);
            e.text = text;
            return e;
        }
    }

    @EventPooled
    @Getter
    public static final class Enter extends TextInputEvent {

        private String text;
        private int keyCode;

        public static Enter create(String text, int keyCode) {
            var e = EventPool.obtain(Enter.class);
            e.text = text;
            e.keyCode = keyCode;
            return e;
        }
    }

    @EventPooled
    @Getter
    public static final class TextInputKeyDown extends TextInputEvent {
        private String text;
        private int keyCode;

        public static TextInputKeyDown create(String text, int keyCode) {
            var e = EventPool.obtain(TextInputKeyDown.class);
            e.text = text;
            e.keyCode = keyCode;
            return e;
        }
    }
}
