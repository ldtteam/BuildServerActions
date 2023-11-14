package com.ldtteam.buildserveractions.constants;

import org.jetbrains.annotations.NonNls;

import java.util.function.LongFunction;

/**
 * Constant values for translation keys.
 */
@NonNls
public class TranslationConstants
{
    public static final LongFunction<String> WIDGET_SET_TIME_NAME     = multiplier -> "com.ldtteam.buildserveractions.widgets.settime." + multiplier;
    public static final LongFunction<String> WIDGET_FLIGHT_SPEED_NAME = multiplier -> "com.ldtteam.buildserveractions.widgets.flightspeed." + multiplier;

    public static final String WIDGET_ITEM_NAME = "com.ldtteam.buildserveractions.widgets.item";
    public static final String WIDGET_ITEM_DESC = "com.ldtteam.buildserveractions.widgets.item.desc";

    private TranslationConstants()
    {
    }
}
