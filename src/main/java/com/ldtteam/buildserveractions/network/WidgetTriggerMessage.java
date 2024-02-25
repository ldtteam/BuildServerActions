package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Command for triggering a widget.
 */
public class WidgetTriggerMessage
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

    /**
     * Message to bytes.
     *
     * @param message the message instance.
     * @param buf     the network buffer.
     */
    public static void toBytes(final WidgetTriggerMessage message, final FriendlyByteBuf buf)
    {
        WidgetManager.getInstance().writeWidgetToBuffer(buf, message.clickedWidget);
    }

    /**
     * Bytes to message.
     *
     * @param buf the network buffer.
     * @return the deserialized message instance.
     */
    public static WidgetTriggerMessage fromBytes(final FriendlyByteBuf buf)
    {
        final WidgetTriggerMessage message = new WidgetTriggerMessage();
        message.clickedWidget = WidgetManager.getInstance().readWidgetFromBuffer(buf);
        return message;
    }

    /**
     * Execute this message.
     *
     * @param context the network context.
     */
    public void onExecute(final Context context)
    {
        final WidgetSource source = new WidgetSource(this.clickedWidget, context.getSender());
        this.clickedWidget.getHandler().accept(source);
    }
}
