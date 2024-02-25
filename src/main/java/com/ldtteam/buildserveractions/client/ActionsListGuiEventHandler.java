package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles all GUI events.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ActionsListGuiEventHandler
{
    private ActionsListGuiEventHandler()
    {
    }

    @SubscribeEvent
    public static void onScreenOpened(final ScreenEvent.Init.Post event)
    {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen))
        {
            return;
        }

        final WidgetLayout renderLayout = LayoutManager.getInstance().getLayout(event.getScreen().getClass());
        if (renderLayout == null)
        {
            return;
        }

        final ActionsListWindow currentGui = new ActionsListWindow(containerScreen, renderLayout);
        event.addListener(currentGui.getScreen());
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (!event.phase.equals(TickEvent.Phase.END) || !event.side.isClient())
        {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
        {
            for (GuiEventListener child : mc.screen.children())
            {
                if (child instanceof BOScreen attachedScreen)
                {
                    attachedScreen.tick();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(final ScreenEvent.MouseScrolled.Pre event)
    {
        // Scroll events only work directly within the screen itself and are not bubbled up to the children.
        // So we have to manually forward it to the open GUI.
        final Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
        {
            for (GuiEventListener child : mc.screen.children())
            {
                if (child instanceof BOScreen attachedScreen)
                {
                    attachedScreen.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta());
                }
            }
        }
    }
}
