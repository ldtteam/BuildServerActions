package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.network.Network;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.WidgetGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
public class EventHandler
{
    public static final ResourceKey<Registry<WidgetGroup>> WIDGET_GROUP_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Constants.MOD_ID, "widget-groups"));
    public static final ResourceKey<Registry<Widget>>      WIDGET_REGISTRY_KEY       = ResourceKey.createRegistryKey(new ResourceLocation(Constants.MOD_ID, "widgets"));

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
        event.create(new RegistryBuilder<WidgetRegistries.WidgetGroup>().setName(WIDGET_GROUP_REGISTRY_KEY.location())
                       .disableSaving()
                       .allowModification()
                       .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetGroupRegistry);

        event.create(new RegistryBuilder<WidgetRegistries.Widget>().setName(WIDGET_REGISTRY_KEY.location())
                       .disableSaving()
                       .allowModification()
                       .setIDRange(0, Integer.MAX_VALUE - 1), WidgetManager.getInstance()::setWidgetRegistry);
    }
}
