package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.ldtteam.buildserveractions.constants.Constants.modId;

/**
 * Network channel for the mod.
 */
public class Network
{
    private static final String MOD_VERSION = ModList.get().getModContainerById(Constants.MOD_ID).get().getModInfo().getVersion().toString();

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        modId("default"),
        () -> MOD_VERSION,
        MOD_VERSION::equals,
        MOD_VERSION::equals);

    public static void register()
    {
        CHANNEL.messageBuilder(WidgetTriggerMessage.class, 0)
            .encoder(WidgetTriggerMessage::toBytes)
            .decoder(WidgetTriggerMessage::new)
            .consumerMainThread(WidgetTriggerMessage::onExecute)
            .add();
    }
}
