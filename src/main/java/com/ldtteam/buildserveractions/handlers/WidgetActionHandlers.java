package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Action callbacks for all widgets defined by the mod.
 */
public class WidgetActionHandlers
{
    /**
     * Action callback for switching game-modes.
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
                broadcastMessage(player, Component.translatable("commands.gamemode.success.self", gameType.getLongDisplayName()));
            }
        };
    }

    /**
     * Action callback for setting the game time.
     *
     * @param timeOfDay the time to set the server to.
     * @return the action callback.
     */
    public static Consumer<Player> setTime(final long timeOfDay)
    {
        return player -> {
            if (player.getServer() != null)
            {
                for (ServerLevel serverlevel : player.getServer().getAllLevels())
                {
                    serverlevel.setDayTime(timeOfDay);
                }
                broadcastMessage(player, Component.translatable("commands.time.set", timeOfDay));
            }
        };
    }

    /**
     * Broadcast a command message to the player, and to the admins.
     *
     * @param player  the player who called the command.
     * @param message the message that is being sent.
     */
    private static void broadcastMessage(final Player player, final Component message)
    {
        if (player.getServer() == null)
        {
            return;
        }

        player.sendSystemMessage(message);

        Component component = Component.translatable("chat.type.admin", player.getDisplayName(), message).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (player.getServer().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK))
        {
            for (ServerPlayer serverplayer : player.getServer().getPlayerList().getPlayers())
            {
                if (serverplayer != player && player.getServer().getPlayerList().isOp(serverplayer.getGameProfile()))
                {
                    serverplayer.sendSystemMessage(component);
                }
            }
        }

        if (player.getServer().getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS))
        {
            player.getServer().sendSystemMessage(component);
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
