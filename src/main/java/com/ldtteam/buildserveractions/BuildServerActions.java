package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.client.ClientEventHandler;
import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.registry.ModItems;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class BuildServerActions
{
    /**
     * Mod entrypoint.
     */
    public BuildServerActions(final IEventBus modBus)
    {
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.register(modBus);
        }
        modBus.register(EventHandler.class);

        ModItems.DEFERRED_REGISTER.register(modBus);
        ModWidgetGroups.DEFERRED_REGISTER.register(modBus);
        ModWidgets.DEFERRED_REGISTER.register(modBus);
    }
}
