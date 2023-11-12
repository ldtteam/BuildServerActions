package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Event handler class for the mod code.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventManager
{
    @SubscribeEvent
    public static void registerNewRegistries(final NewRegistryEvent event)
    {
        event.create(new RegistryBuilder<WidgetRegistries.WidgetLayout>()
                       .setName(new ResourceLocation(Constants.MOD_ID, "widget-layouts"))
                       .disableSaving()
                       .allowModification()
                       .setIDRange(0, Integer.MAX_VALUE - 1), LayoutManager.getInstance()::setWidgetLayoutRegistry);

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
