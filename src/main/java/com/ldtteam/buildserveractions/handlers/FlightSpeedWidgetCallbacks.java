package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import net.minecraft.network.chat.Component;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_FLIGHT_SPEED_NAME;
import static com.ldtteam.buildserveractions.util.WidgetLogger.broadcastMessage;

/**
 * Callbacks for the flight speed widget defined by the mod.
 */
public class FlightSpeedWidgetCallbacks
{
    /**
     * Widget metadata keys.
     */
    public static final String FLIGHT_SPEED_MULTIPLIER_KEY = "multiplier";

    /**
     * Default flight speed in game, as indicated by {@link net.minecraft.world.entity.player.Abilities}.
     */
    private static final float DEFAULT_FLYING_SPEED = 0.05f;

    /**
     * Get the name for the player flight speed.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component name(final Widget widget)
    {
        Number multiplier = widget.getMetadataValue(FLIGHT_SPEED_MULTIPLIER_KEY, Number.class);
        return Component.translatable(WIDGET_FLIGHT_SPEED_NAME.apply(multiplier.longValue()));
    }

    /**
     * Action callback for setting the player flight speed.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        Number multiplier = source.widget().getMetadataValue(FLIGHT_SPEED_MULTIPLIER_KEY, Number.class);
        if (source.player().getServer() != null)
        {
            source.player().getAbilities().setFlyingSpeed(DEFAULT_FLYING_SPEED * multiplier.longValue());
            source.player().onUpdateAbilities();

            broadcastMessage(source, source.widget().getDescription().apply(source.widget()));
        }
    }
}
