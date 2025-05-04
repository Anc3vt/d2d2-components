package com.ancevt.d2d2.components;

import com.ancevt.d2d2.event.core.Event;
import com.ancevt.d2d2.event.core.EventPool;
import com.ancevt.d2d2.event.core.EventPooled;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class ComponentEvent extends Event {

    @EventPooled
    public static final class FocusIn extends ComponentEvent {
        public static FocusIn create() {
            return EventPool.obtain(FocusIn.class);
        }
    }

    @EventPooled
    public static final class FocusOut extends ComponentEvent {
        public static FocusOut create() {
            return EventPool.obtain(FocusOut.class);
        }
    }
}
