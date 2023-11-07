package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.client.ActionsListGuiEventHandler;
import com.ldtteam.buildserveractions.registry.WidgetRegistriesInitializer;
import com.ldtteam.buildserveractions.util.Constants;
import com.ldtteam.buildserveractions.util.Network;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class BuildServerActions
{
    /**
     * Mod entrypoint.
     */
    public BuildServerActions()
    {
        WidgetRegistriesInitializer.registerLayoutEntries(FMLJavaModLoadingContext.get().getModEventBus());
        WidgetRegistriesInitializer.registerGroupEntries(FMLJavaModLoadingContext.get().getModEventBus());
        WidgetRegistriesInitializer.registerWidgetEntries(FMLJavaModLoadingContext.get().getModEventBus());

        Network.getNetwork().registerCommonMessages();

        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ActionsListGuiEventHandler.class);
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(EventManager.class);
    }
}
