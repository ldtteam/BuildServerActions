package com.ldtteam.buildserveractions.util;

import com.ldtteam.buildserveractions.WidgetSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

/**
 * Log handlers for widget callbacks.
 */
public class WidgetLogger
{
    /**
     * Broadcast a command message to the player, and to the admins.
     *
     * @param source  the widget source.
     * @param message the message that is being sent.
     */
    public static void broadcastMessage(final WidgetSource source, final Component message)
    {
        final Player player = source.player();
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
}
