package com.ldtteam.buildserveractions.client;

import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles all GUI events.
 */
public class ActionsListGuiEventHandler
{
    private static ActionsListWindow currentGui;

    private ActionsListGuiEventHandler()
    {
    }

    @SubscribeEvent
    public static void onScreenOpened(ScreenEvent.Init.Post event)
    {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen))
        {
            return;
        }

        final ActionRenderLayout<?> renderLayout = LayoutManager.getInstance().getLayout(event.getScreen().getClass());
        if (renderLayout == null)
        {
            return;
        }

        currentGui = new ActionsListWindow(containerScreen, renderLayout);
        currentGui.openAsLayer();
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event)
    {
        if (currentGui != null && event.getScreen().equals(currentGui.getScreen()))
        {
            currentGui.close();
            currentGui = null;
        }
    }
}
