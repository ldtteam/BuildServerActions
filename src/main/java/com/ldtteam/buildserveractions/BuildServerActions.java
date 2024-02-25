package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.registry.WidgetRegistriesInitializer;
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
        WidgetRegistriesInitializer.registerGroupEntries(FMLJavaModLoadingContext.get().getModEventBus());
        WidgetRegistriesInitializer.registerWidgetEntries(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
