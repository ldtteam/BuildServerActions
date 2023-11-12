package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.Comparator;

import static com.ldtteam.buildserveractions.util.WidgetLogger.broadcastMessage;

/**
 * Callbacks for the gamemode widget defined by the mod.
 */
public class GameModeWidgetCallbacks
{
    /**
     * Widget metadata keys.
     */
    public static final String WIDGET_GAME_MODE_KEY = "gameMode";

    /**
     * Get the name for the switch gamemode.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component name(final Widget widget)
    {
        return widget.getMetadataValue(WIDGET_GAME_MODE_KEY, GameType.class).getLongDisplayName();
    }

    /**
     * Get the description for the switch gamemode.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component description(final Widget widget)
    {
        return Component.translatable("commands.gamemode.success.self", widget.getMetadataValue(WIDGET_GAME_MODE_KEY, GameType.class).getLongDisplayName());
    }

    /**
     * Action callback for switching game-modes.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        GameType gameType = source.widget().getMetadataValue(WIDGET_GAME_MODE_KEY, GameType.class);
        if (source.player() instanceof ServerPlayer serverPlayer && serverPlayer.setGameMode(gameType))
        {
            broadcastMessage(source, source.widget().getDescription().apply(source.widget()));
        }
    }

    /**
     * Comparator for the game mode widget list.
     */
    public static class GameModeSorter implements Comparator<WidgetRegistries.Widget>
    {
        @Override
        public int compare(final WidgetRegistries.Widget widget1, final WidgetRegistries.Widget widget2)
        {
            final GameType gameType1 = GameType.byName(widget1.getWidgetId().getPath().replace("gamemode-", ""));
            final GameType gameType2 = GameType.byName(widget2.getWidgetId().getPath().replace("gamemode-", ""));
            return gameType1.getId() - gameType2.getId();
        }
    }
}
