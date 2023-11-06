package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import com.ldtteam.buildserveractions.util.Constants;
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
    public static void onScreenOpened(ScreenEvent.Init.Post event)
    {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen))
        {
            return;
        }

        final WidgetRegistries.WidgetLayout renderLayout = LayoutManager.getInstance().getLayout(event.getScreen().getClass());
        if (renderLayout == null)
        {
            return;
        }

        final ActionsListWindow currentGui = new ActionsListWindow(containerScreen, renderLayout);
        event.addListener(currentGui.getScreen());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
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
}
