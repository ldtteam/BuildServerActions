package com.ldtteam.buildserveractions.event;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.client.ActionsListWindow;
import com.ldtteam.buildserveractions.network.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Handles all GUI events for attaching and rendering the action list window.
 */
public class ForgeClientEventHandler
{
    /**
     * Z-level offset for rendering the attached BlockUI screen above container screen items.
     * Container screens render items at z-levels around 100-250, so we use 400 to ensure
     * our tooltips and UI elements render above them.
     */
    private static final int ATTACHED_SCREEN_Z_OFFSET = 400;

    private ForgeClientEventHandler()
    {
    }

    /**
     * Called when a level loads; initializes the favorites manager on the client.
     *
     * @param event the level load event.
     */
    @SubscribeEvent
    public static void onWorldLoad(final LevelEvent.Load event)
    {
        if (event.getLevel().isClientSide())
        {
            FavoritesManager.getInstance().load(Minecraft.getInstance().gameDirectory);
        }
    }

    /**
     * Called when a level unloads; clears the favorites manager state on the client.
     *
     * @param event the level unload event.
     */
    @SubscribeEvent
    public static void onWorldUnload(final LevelEvent.Unload event)
    {
        if (event.getLevel().isClientSide())
        {
            FavoritesManager.getInstance().unload();
        }
    }

    /**
     * Called when a screen is opened; attaches the action list window if applicable.
     *
     * @param event the screen init event.
     */
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

    /**
     * Called during screen rendering; manually renders the attached BlockUI screen.
     *
     * @param event the screen render event.
     */
    @SubscribeEvent
    public static void onScreenRender(final ScreenEvent.Render.Pre event)
    {
        // BOScreen.render() is only called when it's the main screen, not when added as a child listener.
        // We need to manually render it. Using Render.Pre ensures we render before vanilla tooltips,
        // so our z-offset doesn't obscure tooltip text.
        // Push the z-level higher so our UI renders above the container screen's items.
        for (GuiEventListener child : event.getScreen().children())
        {
            if (child instanceof BOScreen attachedScreen)
            {
                final var pose = event.getGuiGraphics().pose();
                pose.pushPose();
                pose.translate(0, 0, ATTACHED_SCREEN_Z_OFFSET);
                attachedScreen.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
                pose.popPose();
            }
        }
    }

    /**
     * Called on client tick; ticks the attached BlockUI screen.
     *
     * @param event the client tick event.
     */
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event)
    {
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

        for (int i = 0; i < ModClientEventHandler.FAVORITE_SLOTS.length; i++)
        {
            if (ModClientEventHandler.FAVORITE_SLOTS[i].consumeClick())
            {
                final ResourceLocation widgetId = FavoritesManager.getInstance().getSlot(i);
                if (widgetId == null)
                {
                    continue;
                }
                final Widget widget = WidgetManager.getInstance().getWidgetById(widgetId);
                if (widget != null)
                {
                    PacketDistributor.sendToServer(new WidgetTriggerMessage(widget));
                }
            }
        }
    }

    /**
     * Called on mouse scroll; forwards scroll events to the attached BlockUI screen.
     *
     * @param event the mouse scroll event.
     */
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
                    attachedScreen.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());

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
