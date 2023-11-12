package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_SET_TIME_DESC;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_SET_TIME_NAME;
import static com.ldtteam.buildserveractions.util.WidgetLogger.broadcastMessage;

/**
 * Callbacks for the set time widget defined by the mod.
 */
public class SetTimeWidgetCallbacks
{
    public static final String TIME_KEY = "timeOfDay";

    /**
     * Get the name for the game time.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component name(final Widget widget)
    {
        Number multiplier = widget.getMetadataValue(TIME_KEY, Number.class);
        return Component.translatable(WIDGET_SET_TIME_NAME.apply(multiplier.longValue()));
    }

    /**
     * Get the description for the game time.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component description(final Widget widget)
    {
        Number multiplier = widget.getMetadataValue(TIME_KEY, Number.class);
        return Component.translatable(WIDGET_SET_TIME_DESC.apply(multiplier.longValue()));
    }

    /**
     * Action callback for setting the game time.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        Number timeOfDay = source.widget().getMetadataValue(TIME_KEY, Number.class);
        if (source.player().getServer() != null)
        {
            for (ServerLevel serverlevel : source.player().getServer().getAllLevels())
            {
                serverlevel.setDayTime(timeOfDay.longValue());
            }
            broadcastMessage(source, source.widget().getDescription().apply(source.widget()));
        }
    }
}
