package com.ldtteam.buildserveractions.constants;

import org.jetbrains.annotations.NonNls;

import java.util.function.LongFunction;

/**
 * Constant values for translation keys.
 */
public class TranslationConstants
{
    @NonNls
    public static final LongFunction<String> WIDGET_SET_TIME_NAME     = multiplier -> "com.ldtteam.buildserveractions.widgets.settime." + multiplier;
    @NonNls
    public static final LongFunction<String> WIDGET_SET_TIME_DESC     = multiplier -> String.format("com.ldtteam.buildserveractions.widgets.settime.%s.desc", multiplier);
    @NonNls
    public static final LongFunction<String> WIDGET_FLIGHT_SPEED_NAME = multiplier -> "com.ldtteam.buildserveractions.widgets.flightspeed." + multiplier;
    @NonNls
    public static final LongFunction<String> WIDGET_FLIGHT_SPEED_DESC = multiplier -> String.format("com.ldtteam.buildserveractions.widgets.flightspeed.%s.desc", multiplier);

    private TranslationConstants()
    {
    }
}
