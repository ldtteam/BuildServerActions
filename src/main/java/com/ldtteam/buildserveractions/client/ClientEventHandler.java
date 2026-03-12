package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.AtlasManager;
import com.ldtteam.buildserveractions.constants.Constants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-side event handler for the mod.
 */
public class ClientEventHandler
{
    /**
     * Register client-side event handlers.
     *
     * @param modBus the mod event bus.
     */
    public static void register(final IEventBus modBus)
    {
        NeoForge.EVENT_BUS.register(ActionsListGuiEventHandler.class);
        modBus.register(ClientEventHandler.class);
    }

    /**
     * Register client reload listeners for the atlas.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void registerClientReloadListeners(final RegisterClientReloadListenersEvent event)
    {
        AtlasManager.INSTANCE.addAtlas(event::registerReloadListener, Constants.MOD_ID);
    }
}
