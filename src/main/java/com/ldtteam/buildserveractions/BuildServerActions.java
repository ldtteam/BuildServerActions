package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.registry.ModItems;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
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
        final var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.DEFERRED_REGISTER.register(modEventBus);
        ModWidgetGroups.DEFERRED_REGISTER.register(modEventBus);
        ModWidgets.DEFERRED_REGISTER.register(modEventBus);
    }
}
