package com.ldtteam.buildserveractions.network;

import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Wrapper for Forge network layer
 */
public class Network
{
    /**
     * Singleton instance.
     */
    private static Network instance;

    /**
     * Forge network channel
     */
    private final SimpleChannel rawChannel;

    /**
     * Creates a new instance of network channel.
     *
     * @throws IllegalArgumentException if channelName already exists
     */
    public Network()
    {
        final String modVersion = ModList.get().getModContainerById(Constants.MOD_ID).get().getModInfo().getVersion().toString();
        rawChannel =
          NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "default"), () -> modVersion, str -> str.equals(modVersion), str -> str.equals(modVersion));
    }

    /**
     * Get the network instance.
     *
     * @return the network channel instance.
     */
    public static Network getInstance()
    {
        if (instance == null)
        {
            instance = new Network();
        }
        return instance;
    }

    /**
     * Get the underlying network channel.
     *
     * @return the network channel.
     */
    public SimpleChannel getChannel()
    {
        return rawChannel;
    }

    /**
     * Register all network messages.
     */
    public void registerMessages()
    {
        rawChannel.registerMessage(1,
          WidgetTriggerMessage.class,
          WidgetTriggerMessage::toBytes,
          WidgetTriggerMessage::fromBytes,
          (message, context) -> message.onExecute(context.get()));
    }
}
