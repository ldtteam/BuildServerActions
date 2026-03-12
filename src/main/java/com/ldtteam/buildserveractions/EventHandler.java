package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.network.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Event handler class for the mod code.
 */
public class EventHandler
{
    /**
     * Event handler for the payload registers.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void registerPayloadHandlers(@NotNull final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(WidgetTriggerMessage.TYPE, WidgetTriggerMessage.STREAM_CODEC, WidgetTriggerMessage::handle);
    }

    /**
     * Register the registries used by the mod.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void registerNewRegistries(final NewRegistryEvent event)
    {
        WidgetManager.getInstance().setWidgetRegistry(event.create(new RegistryBuilder<>(ModWidgets.REGISTRY_KEY).sync(true)));
        WidgetManager.getInstance().setWidgetGroupRegistry(event.create(new RegistryBuilder<>(ModWidgetGroups.REGISTRY_KEY).sync(true)));
    }

}
