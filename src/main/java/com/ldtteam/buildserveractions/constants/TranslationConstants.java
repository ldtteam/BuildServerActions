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

    public static final String WIDGET_OPEN_WINDOW_NAME = "com.ldtteam.buildserveractions.widgets.window";
    public static final String WIDGET_OPEN_WINDOW_DESC = "com.ldtteam.buildserveractions.widgets.window.desc";

    public static final String FAVORITES_SLOT_CURRENT   = "com.ldtteam.buildserveractions.favorites.slot.current";
    public static final String FAVORITES_TOGGLE_ENTER   = "com.ldtteam.buildserveractions.favorites.toggle.enter";
    public static final String FAVORITES_TOGGLE_EXIT    = "com.ldtteam.buildserveractions.favorites.toggle.exit";
    public static final String FAVORITES_HINT_ADD       = "com.ldtteam.buildserveractions.favorites.hint.add";
    public static final String FAVORITES_HINT_REMOVE    = "com.ldtteam.buildserveractions.favorites.hint.remove";
    public static final String FAVORITES_KEYBIND        = "com.ldtteam.buildserveractions.favorites.keybind";

    private TranslationConstants()
    {
    }
}
