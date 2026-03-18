package com.ldtteam.buildserveractions.event;

import com.ldtteam.buildserveractions.command.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Event handler class for the forge events.
 */
public class ForgeEventHandler
{
    /**
     * Private constructor to prevent instantiation.
     */
    private ForgeEventHandler()
    {
    }

    /**
     * Event handler for command registration.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void onCommandsRegister(final RegisterCommandsEvent event)
    {
        event.getDispatcher().register(new CommandPlotList().build(event.getBuildContext()));
        event.getDispatcher().register(new CommandNewPlot().build(event.getBuildContext()));
        event.getDispatcher().register(new CommandPlotExtend().build(event.getBuildContext()));
        event.getDispatcher().register(new CommandPlotRename().build(event.getBuildContext()));
        event.getDispatcher().register(new CommandPlotSettings().build(event.getBuildContext()));
    }
}
