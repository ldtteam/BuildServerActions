package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.network.Network;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraft.resources.ResourceLocation;
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
public class EventManager
{

    /**
     * Event handler for forge pre init event.
     *
     * @param event the forge pre init event.
     */
    @SubscribeEvent
    public static void preInit(@NotNull final FMLCommonSetupEvent event)
    {
        Network.getInstance().registerMessages();
    }

    @SubscribeEvent
    public static void registerNewRegistries(final NewRegistryEvent event)
    {
        event.create(new RegistryBuilder<WidgetRegistries.WidgetGroup>()
                       .setName(new ResourceLocation(Constants.MOD_ID, "widget-groups"))
                       .disableSaving()
                       .allowModification()
                       .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetGroupRegistry);

        event.create(new RegistryBuilder<WidgetRegistries.Widget>()
                       .setName(new ResourceLocation(Constants.MOD_ID, "widgets"))
                       .disableSaving()
                       .allowModification()
                       .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetRegistry);
    }
}
