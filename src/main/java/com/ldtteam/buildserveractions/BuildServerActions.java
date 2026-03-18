package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.event.ClientModEventHandler;
import com.ldtteam.buildserveractions.event.ForgeEventHandler;
import com.ldtteam.buildserveractions.event.ModEventHandler;
import com.ldtteam.buildserveractions.registry.ModArgumentTypes;
import com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes;
import com.ldtteam.buildserveractions.registry.ModItems;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Main mod class for BuildServerActions.
 */
@Mod(Constants.MOD_ID)
public class BuildServerActions
{
    /**
     * Mod entrypoint.
     *
     * @param modBus the mod event bus.
     */
    public BuildServerActions(final IEventBus modBus)
    {
        NeoForge.EVENT_BUS.register(ForgeEventHandler.class);
        modBus.register(ModEventHandler.class);
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientModEventHandler.register(modBus);
        }

        ModArgumentTypes.DEFERRED_REGISTER.register(modBus);
        ModItems.DEFERRED_REGISTER.register(modBus);
        ModDataAttachmentTypes.DEFERRED_REGISTER.register(modBus);
        ModWidgetGroups.DEFERRED_REGISTER.register(modBus);
        ModWidgets.DEFERRED_REGISTER.register(modBus);
    }
}
