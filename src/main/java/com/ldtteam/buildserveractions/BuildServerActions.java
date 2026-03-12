package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.client.ActionsListGuiEventHandler;
import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.registry.ModItems;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class BuildServerActions
{
    /**
     * Mod entrypoint.
     */
    public BuildServerActions(final IEventBus modBus)
    {
        NeoForge.EVENT_BUS.register(ActionsListGuiEventHandler.class);
        modBus.register(EventHandler.class);

        ModItems.DEFERRED_REGISTER.register(modBus);
        ModWidgetGroups.DEFERRED_REGISTER.register(modBus);
        ModWidgets.DEFERRED_REGISTER.register(modBus);
    }
}
