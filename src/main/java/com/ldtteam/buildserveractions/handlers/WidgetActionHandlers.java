package com.ldtteam.buildserveractions.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.util.function.Consumer;

/**
 * Action callbacks for all widgets defined by the mod.
 */
public class WidgetActionHandlers
{
    /**
     * Action callback for switching gamemodes.
     *
     * @param gameType the game mode to switch to.
     * @return the action callback.
     */
    public static Consumer<Player> switchGameMode(final GameType gameType)
    {
        return player -> {
            if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.setGameMode(gameType);
            }
        };
    }
}
