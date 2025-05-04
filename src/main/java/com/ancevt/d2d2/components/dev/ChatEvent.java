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

import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class ChatEvent extends Event {

    @EventPooled
    @Getter
    public static final class TextEnter extends ChatEvent {
        private String text;

        public static TextEnter create(String text) {
            TextEnter e = EventPool.obtain(TextEnter.class);
            e.text = text;
            return e;
        }
    }

    @EventPooled
    public static final class InputOpen extends ChatEvent {
        public static InputOpen create() {
            return EventPool.obtain(InputOpen.class);
        }
    }

    @EventPooled
    public static final class InputClose extends ChatEvent {
        public static InputOpen create() {
            return EventPool.obtain(InputOpen.class);
        }
    }
}
