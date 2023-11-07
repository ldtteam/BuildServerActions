package com.ldtteam.buildserveractions.network.messages.client;

import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.network.IMessage;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Command for triggering a widget.
 */
public class WidgetTriggerMessage implements IMessage
{
    /**
     * The widget which was triggered.
     */
    private WidgetRegistries.Widget clickedWidget;

    /**
     * Default constructor.
     */
    public WidgetTriggerMessage()
    {
    }

    /**
     * Constructor for creating a new message.
     *
     * @param clickedWidget the widget which was triggered.
     */
    public WidgetTriggerMessage(final WidgetRegistries.Widget clickedWidget)
    {
        this.clickedWidget = clickedWidget;
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf)
    {
        WidgetManager.getInstance().writeWidgetToBuffer(buf, this.clickedWidget);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buf)
    {
        this.clickedWidget = WidgetManager.getInstance().readWidgetFromBuffer(buf);
    }

    @Override
    public @Nullable LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        if (!isLogicalServer)
        {
            return;
        }

        this.clickedWidget.getHandler().accept(ctxIn.getSender());
    }
}
