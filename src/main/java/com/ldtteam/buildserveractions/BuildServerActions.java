package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.registry.ModItems;
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
        ModItems.DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
