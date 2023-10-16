package com.ldtteam.buildserveractions.client;

import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Handles all GUI events.
 */
public class ActionsListGuiEventHandler
{
    private static ActionsListGui currentGui;

    private ActionsListGuiEventHandler()
    {
    }

    @SubscribeEvent
    public static void onScreenOpened(ScreenEvent.Opening event)
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

        currentGui = new ActionsListGui(containerScreen, renderLayout);
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event)
    {
        currentGui = null;
    }

    @SubscribeEvent
    public static void onPostRender(ScreenEvent.Render.Post event)
    {
        if (currentGui == null)
        {
            return;
        }

        currentGui.draw(event.getPoseStack(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public static void onMouseClicked(ScreenEvent.MouseButtonReleased.Pre event)
    {
        if (currentGui == null)
        {
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && (currentGui.getRoot().click(event.getMouseX(), event.getMouseY())))
        {
            event.setCanceled(true);
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (currentGui.getRoot().rightClick(event.getMouseX(), event.getMouseY())))
        {
            event.setCanceled(true);
        }
    }
}
