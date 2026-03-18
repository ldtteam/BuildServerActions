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

    public static final String COMMAND_PLOT_NEW_CREATED     = "com.ldtteam.buildserveractions.commands.plot.new.created";
    public static final String COMMAND_PLOT_NEW_NO_OFFICIAL = "com.ldtteam.buildserveractions.commands.plot.new.no_official";

    public static final String COMMAND_PLOT_SETTINGS_OFFSET_GET              = "com.ldtteam.buildserveractions.commands.plot.settings.offset.get";
    public static final String COMMAND_PLOT_SETTINGS_OFFSET_SET              = "com.ldtteam.buildserveractions.commands.plot.settings.offset.set";
    public static final String COMMAND_PLOT_SETTINGS_CENTER_ROAD_SPACING_GET = "com.ldtteam.buildserveractions.commands.plot.settings.center.road.spacing.get";
    public static final String COMMAND_PLOT_SETTINGS_CENTER_ROAD_SPACING_SET = "com.ldtteam.buildserveractions.commands.plot.settings.center.road.spacing.set";
    public static final String COMMAND_PLOT_SETTINGS_PLOT_ROAD_SPACING_GET   = "com.ldtteam.buildserveractions.commands.plot.settings.plot.road.spacing.get";
    public static final String COMMAND_PLOT_SETTINGS_PLOT_ROAD_SPACING_SET   = "com.ldtteam.buildserveractions.commands.plot.settings.plot.road.spacing.set";
    public static final String COMMAND_PLOT_SETTINGS_Y_LEVEL_GET             = "com.ldtteam.buildserveractions.commands.plot.settings.ylevel.get";
    public static final String COMMAND_PLOT_SETTINGS_Y_LEVEL_SET             = "com.ldtteam.buildserveractions.commands.plot.settings.ylevel.set";
    public static final String COMMAND_PLOT_SETTINGS_ROAD_BLOCK_GET          = "com.ldtteam.buildserveractions.commands.plot.settings.roadblock.get";
    public static final String COMMAND_PLOT_SETTINGS_ROAD_BLOCK_SET          = "com.ldtteam.buildserveractions.commands.plot.settings.roadblock.set";

    public static final String COMMAND_PLOT_LIST_HEADER           = "com.ldtteam.buildserveractions.commands.plot.list.header";
    public static final String COMMAND_PLOT_LIST_ENTRY            = "com.ldtteam.buildserveractions.commands.plot.list.entry";
    public static final String COMMAND_PLOT_LIST_EMPTY            = "com.ldtteam.buildserveractions.commands.plot.list.empty";
    public static final String COMMAND_PLOT_LIST_INVALID_PAGE     = "com.ldtteam.buildserveractions.commands.plot.list.invalid_page";
    public static final String COMMAND_PLOT_LIST_CLICK_TO_TELEPORT = "com.ldtteam.buildserveractions.commands.plot.list.click_to_teleport";
    public static final String COMMAND_PLOT_LIST_PREV             = "com.ldtteam.buildserveractions.commands.plot.list.prev";
    public static final String COMMAND_PLOT_LIST_NEXT             = "com.ldtteam.buildserveractions.commands.plot.list.next";
    public static final String COMMAND_PLOT_LIST_PAGE             = "com.ldtteam.buildserveractions.commands.plot.list.page";

    public static final String COMMAND_PLOT_RENAME_SUCCESS   = "com.ldtteam.buildserveractions.commands.plot.rename.success";
    public static final String COMMAND_PLOT_RENAME_NOT_FOUND = "com.ldtteam.buildserveractions.commands.plot.rename.not_found";

    private TranslationConstants()
    {
    }
}
