package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Message for triggering a widget.
 */
public class WidgetTriggerMessage
{
    private final Widget clickedWidget;

    public WidgetTriggerMessage(final Widget clickedWidget)
    {
        this.clickedWidget = clickedWidget;
    }

    public WidgetTriggerMessage(final FriendlyByteBuf buf)
    {
        this.clickedWidget = WidgetManager.getInstance().readWidgetFromBuffer(buf);
    }

    public void toBytes(final FriendlyByteBuf buf)
    {
        WidgetManager.getInstance().writeWidgetToBuffer(buf, this.clickedWidget);
    }

    public void onExecute(final Supplier<Context> contextSupplier)
    {
        final Context context = contextSupplier.get();
        final WidgetSource source = new WidgetSource(this.clickedWidget, context.getSender());
        this.clickedWidget.getHandler().accept(source);
    }
}
