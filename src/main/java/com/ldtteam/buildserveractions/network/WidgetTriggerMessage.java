package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.ldtteam.buildserveractions.constants.Constants.modId;

/**
 * Message for triggering a widget.
 *
 * @param clickedWidget the widget that was clicked.
 */
public record WidgetTriggerMessage(Widget clickedWidget) implements CustomPacketPayload
{
    /**
     * The packet type identifier.
     */
    public static final Type<WidgetTriggerMessage> TYPE = new Type<>(modId("widget_trigger"));

    /**
     * The stream codec for serialization.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WidgetTriggerMessage> STREAM_CODEC =
        ByteBufCodecs.registry(ModWidgets.REGISTRY_KEY)
            .map(WidgetTriggerMessage::new, WidgetTriggerMessage::clickedWidget);

    /**
     * Handles the incoming widget trigger message on the server.
     *
     * @param msg     the message to handle.
     * @param context the payload context.
     */
    public static void handle(final WidgetTriggerMessage msg, final IPayloadContext context)
    {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer)
            {
                final WidgetSource source = new WidgetSource(msg.clickedWidget, serverPlayer);
                msg.clickedWidget.getHandler().accept(source);
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
