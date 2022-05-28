package com.ancevt.d2d2.components;

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ComponentEvent extends Event {

    public static final String FOCUS_IN = "componentFocusIn";
    public static final String FOCUS_OUT = "componentFocusOut";
}
