package com.ancevt.d2d2.components.dev.chat;

import com.ancevt.commons.string.ConvertableString;
import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ConsoleChatEvent extends Event {

    public static final String VAR_VALUE_CHANGE = "varValueChange";
    public static final String VAR_VALUE_DELETE = "varValueDelete";

    private final String varName;
    private final ConvertableString oldValue;
    private final ConvertableString value;
}
