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

import com.ancevt.commons.string.ConvertableString;
import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public abstract class ConsoleEvent extends Event {

    protected String varName;
    protected ConvertableString oldValue;
    protected ConvertableString value;

    @EventPooled
    public static class ValueChange extends ConsoleEvent {
        public static ValueChange create(String varName, ConvertableString oldValue, ConvertableString value) {
            ValueChange event = EventPool.obtain(ValueChange.class);
            event.varName = varName;
            event.oldValue = oldValue;
            event.value = value;
            return event;
        }
    }

    @EventPooled
    public static class ValueDelete extends ConsoleEvent {
        public static ValueDelete create(String varName, ConvertableString oldValue, ConvertableString value) {
            ValueDelete event = EventPool.obtain(ValueDelete.class);
            event.varName = varName;
            event.oldValue = oldValue;
            event.value = value;
            return event;
        }
    }
}
