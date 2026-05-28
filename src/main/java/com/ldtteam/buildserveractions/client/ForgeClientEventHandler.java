package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.network.Network;
import com.ldtteam.buildserveractions.network.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles all GUI events.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler
{
    private ForgeClientEventHandler()
    {
    }

    @SubscribeEvent
    public static void onWorldLoad(final LevelEvent.Load event)
    {
        if (event.getLevel().isClientSide())
        {
            FavoritesManager.getInstance().load(Minecraft.getInstance().gameDirectory);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(final LevelEvent.Unload event)
    {
        if (event.getLevel().isClientSide())
        {
            FavoritesManager.getInstance().unload();
        }
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

        for (int i = 0; i < ClientEventHandler.FAVORITE_SLOTS.length; i++)
        {
            if (ClientEventHandler.FAVORITE_SLOTS[i].consumeClick())
            {
                final ResourceLocation widgetId = FavoritesManager.getInstance().getSlot(i);
                if (widgetId == null)
                {
                    continue;
                }
                final Widget widget = WidgetManager.getInstance().getWidgetById(widgetId);
                if (widget != null)
                {
                    Network.CHANNEL.sendToServer(new WidgetTriggerMessage(widget));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(final ScreenEvent.MouseScrolled.Pre event)
    {
        // Scroll events only work directly within the screen itself and are not bubbled up to the children.
        // So we have to manually forward it to the open GUI.
        // If the mouse is over the BOScreen window, cancel the event to prevent the container screen from scrolling.
        final Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
        {
            for (GuiEventListener child : mc.screen.children())
            {
                if (child instanceof BOScreen attachedScreen)
                {
                    // Always forward the scroll event
                    attachedScreen.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta());

                    // Cancel if mouse is over the window, regardless of whether scrolling occurred
                    if (attachedScreen.getWindow().wasCursorInPane())
                    {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
}
