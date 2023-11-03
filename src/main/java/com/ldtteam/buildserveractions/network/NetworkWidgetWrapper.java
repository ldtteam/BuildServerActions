package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.util.Log;
import com.ldtteam.buildserveractions.util.Network;
import com.ldtteam.buildserveractions.widgets.NetworkWidget;
import com.ldtteam.buildserveractions.widgets.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Wrapper class for network widgets handlers.
 */
public class NetworkWidgetWrapper implements Widget
{
    private final NetworkWidget<?> wrappedWidget;

    public NetworkWidgetWrapper(NetworkWidget<?> wrappedWidget)
    {
        this.wrappedWidget = wrappedWidget;
    }

    @Override
    public void handle()
    {
        final IMessage message = wrappedWidget.createMessage();
        if (Objects.isNull(message.getExecutionSide()) || !message.getExecutionSide().isServer())
        {
            Network.getNetwork().sendToServer(message);
        }
        else
        {
            Log.getLogger().warn("Action {} attempted to send a CLIENT message, only SERVER messages are allowed.", getId());
        }
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return wrappedWidget.getId();
    }

    @Override
    public @NotNull ResourceLocation getGroupId()
    {
        return wrappedWidget.getGroupId();
    }

    @Override
    public @NotNull Component getName()
    {
        return wrappedWidget.getName();
    }

    @Override
    public @Nullable Component getDescription()
    {
        return wrappedWidget.getDescription();
    }

    @Override
    public @NotNull ItemStack getIcon()
    {
        return wrappedWidget.getIcon();
    }
}
