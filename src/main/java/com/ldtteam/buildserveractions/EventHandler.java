package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.network.Network;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import com.ldtteam.buildserveractions.widget.Widget;
import com.ldtteam.buildserveractions.widget.WidgetGroup;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Event handler class for the mod code.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    /**
     * Event handler for forge pre init event.
     *
     * @param event the forge pre init event.
     */
    @SubscribeEvent
    public static void preInit(@NotNull final FMLCommonSetupEvent event)
    {
        Network.register();
    }

    @SubscribeEvent
    public static void registerNewRegistries(final NewRegistryEvent event)
    {
        event.create(new RegistryBuilder<WidgetGroup>().setName(ModWidgetGroups.REGISTRY_KEY.location())
            .disableSaving()
            .allowModification()
            .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetGroupRegistry);

        event.create(new RegistryBuilder<Widget>().setName(ModWidgets.REGISTRY_KEY.location())
            .disableSaving()
            .allowModification()
            .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetRegistry);
    }
}
