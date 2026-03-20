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

    public static final String COMMAND_PLOT_TELEPORT_SUCCESS   = "com.ldtteam.buildserveractions.commands.plot.teleport.success";
    public static final String COMMAND_PLOT_TELEPORT_NOT_FOUND = "com.ldtteam.buildserveractions.commands.plot.teleport.not_found";
    public static final String COMMAND_PLOT_TELEPORT_NO_ENTITY = "com.ldtteam.buildserveractions.commands.plot.teleport.no_entity";

    public static final String COMMAND_PLOT_REGENERATE_SUCCESS   = "com.ldtteam.buildserveractions.commands.plot.regenerate.success";
    public static final String COMMAND_PLOT_REGENERATE_NOT_FOUND = "com.ldtteam.buildserveractions.commands.plot.regenerate.not_found";

    // Setup command translations
    public static final String COMMAND_PLOT_SETUP_ALREADY_COMPLETE   = "com.ldtteam.buildserveractions.commands.plot.setup.already_complete";
    public static final String COMMAND_PLOT_SETUP_NO_PERMISSION      = "com.ldtteam.buildserveractions.commands.plot.setup.no_permission";
    public static final String COMMAND_PLOT_SETUP_STARTED            = "com.ldtteam.buildserveractions.commands.plot.setup.started";
    public static final String COMMAND_PLOT_SETUP_CANCELLED          = "com.ldtteam.buildserveractions.commands.plot.setup.cancelled";
    public static final String COMMAND_PLOT_SETUP_COMPLETE           = "com.ldtteam.buildserveractions.commands.plot.setup.complete";
    public static final String COMMAND_PLOT_SETUP_WRONG_STEP         = "com.ldtteam.buildserveractions.commands.plot.setup.wrong_step";
    public static final String COMMAND_PLOT_SETUP_NO_SESSION         = "com.ldtteam.buildserveractions.commands.plot.setup.no_session";
    public static final String COMMAND_PLOT_SETUP_STEP_HEADER        = "com.ldtteam.buildserveractions.commands.plot.setup.step_header";
    public static final String COMMAND_PLOT_SETUP_SET_VALUE          = "com.ldtteam.buildserveractions.commands.plot.setup.set_value";
    public static final String COMMAND_PLOT_SETUP_USE_DEFAULT        = "com.ldtteam.buildserveractions.commands.plot.setup.use_default";
    public static final String COMMAND_PLOT_SETUP_KEEP_CURRENT       = "com.ldtteam.buildserveractions.commands.plot.setup.keep_current";
    public static final String COMMAND_PLOT_SETUP_DEFAULT_VALUE      = "com.ldtteam.buildserveractions.commands.plot.setup.default_value";
    public static final String COMMAND_PLOT_SETUP_CURRENT_VALUE      = "com.ldtteam.buildserveractions.commands.plot.setup.current_value";
    public static final String COMMAND_PLOT_SETUP_VALUE_SET          = "com.ldtteam.buildserveractions.commands.plot.setup.value_set";
    public static final String COMMAND_PLOT_SETUP_CONFIRM_PROMPT     = "com.ldtteam.buildserveractions.commands.plot.setup.confirm_prompt";
    public static final String COMMAND_PLOT_SETUP_CONFIRM_BUTTON     = "com.ldtteam.buildserveractions.commands.plot.setup.confirm_button";
    public static final String COMMAND_PLOT_SETUP_CANCEL_BUTTON      = "com.ldtteam.buildserveractions.commands.plot.setup.cancel_button";
    public static final String COMMAND_PLOT_SETUP_RESTART_BUTTON     = "com.ldtteam.buildserveractions.commands.plot.setup.restart_button";
    public static final String COMMAND_PLOT_SETUP_RESTARTED          = "com.ldtteam.buildserveractions.commands.plot.setup.restarted";
    public static final String COMMAND_PLOT_SETUP_EXECUTING_PENDING  = "com.ldtteam.buildserveractions.commands.plot.setup.executing_pending";

    // Setup step descriptions
    public static final String COMMAND_PLOT_SETUP_DESC_CENTER_ROAD   = "com.ldtteam.buildserveractions.commands.plot.setup.desc.center_road";
    public static final String COMMAND_PLOT_SETUP_DESC_PLOT_ROAD     = "com.ldtteam.buildserveractions.commands.plot.setup.desc.plot_road";
    public static final String COMMAND_PLOT_SETUP_DESC_NORTH_OFFSET  = "com.ldtteam.buildserveractions.commands.plot.setup.desc.north_offset";
    public static final String COMMAND_PLOT_SETUP_DESC_SOUTH_OFFSET  = "com.ldtteam.buildserveractions.commands.plot.setup.desc.south_offset";
    public static final String COMMAND_PLOT_SETUP_DESC_ROAD_BLOCK    = "com.ldtteam.buildserveractions.commands.plot.setup.desc.road_block";
    public static final String COMMAND_PLOT_SETUP_DESC_Y_LEVEL       = "com.ldtteam.buildserveractions.commands.plot.setup.desc.y_level";

    // New plot command - setup required
    public static final String COMMAND_PLOT_NEW_SETUP_REQUIRED       = "com.ldtteam.buildserveractions.commands.plot.new.setup_required";
    public static final String COMMAND_PLOT_NEW_SETUP_NOT_COMPLETE   = "com.ldtteam.buildserveractions.commands.plot.new.setup_not_complete";

    private TranslationConstants()
    {
    }
}
