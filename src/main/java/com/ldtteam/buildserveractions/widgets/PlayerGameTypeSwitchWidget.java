package com.ldtteam.buildserveractions.widgets;

import com.ldtteam.buildserveractions.network.IMessage;
import com.ldtteam.buildserveractions.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Widget for switching player game type.
 */
public class PlayerGameTypeSwitchWidget implements NetworkWidget<PlayerGameTypeSwitchWidget.PlayerGameTypeSwitchMessage>
{
    /**
     * The game type to switch the player to.
     */
    private final GameType gameType;

    /**
     * Default constructor.
     *
     * @param gameType the game type to switch the player to.
     */
    public PlayerGameTypeSwitchWidget(final GameType gameType)
    {
        this.gameType = gameType;
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return new ResourceLocation(Constants.MOD_ID, String.format("game-types/%s", gameType.getName()));
    }

    @Override
    public @NotNull ResourceLocation getGroupId()
    {
        return new ResourceLocation(Constants.MOD_ID, "game-types");
    }

    @Override
    public @NotNull Component getName()
    {
        return Component.literal("Set game mode to ").append(gameType.getLongDisplayName());
    }

    @Override
    public @Nullable Component getDescription()
    {
        return null;
    }

    @NotNull
    @Override
    public PlayerGameTypeSwitchMessage createMessage()
    {
        return new PlayerGameTypeSwitchMessage(gameType);
    }

    @Override
    public @NotNull PlayerGameTypeSwitchMessage constructEmptyMessage()
    {
        return new PlayerGameTypeSwitchMessage();
    }

    public static class PlayerGameTypeSwitchMessage implements IMessage
    {
        /**
         * The game type to switch the player to.
         */
        private GameType gameType;

        /**
         * Default constructor.
         */
        public PlayerGameTypeSwitchMessage()
        {
        }

        /**
         * Default constructor.
         *
         * @param gameType the game type to switch the player to.
         */
        public PlayerGameTypeSwitchMessage(final GameType gameType)
        {
            this.gameType = gameType;
        }

        @Override
        public void toBytes(final FriendlyByteBuf buf)
        {
            buf.writeEnum(gameType);
        }

        @Override
        public void fromBytes(final FriendlyByteBuf buf)
        {
            gameType = buf.readEnum(GameType.class);
        }

        @Override
        public @Nullable LogicalSide getExecutionSide()
        {
            return LogicalSide.SERVER;
        }

        @Override
        public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
        {
            if (ctxIn.getSender() != null)
            {
                ctxIn.getSender().setGameMode(gameType);
            }
        }
    }
}
