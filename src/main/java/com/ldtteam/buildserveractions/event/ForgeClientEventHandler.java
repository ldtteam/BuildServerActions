package com.ldtteam.buildserveractions.event;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.LayoutManager;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.client.ActionsListWindow;
import com.ldtteam.buildserveractions.network.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.widget.Widget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public static void onScreenRender(final ScreenEvent.Render.Post event)
    {
        // BOScreen.render() is only called when it's the main screen, not when added as a child listener.
        // We need to manually render it here. Render.Pre is used because Render.Post causes BOScreen's
        // shared BufferSource to corrupt already-queued vanilla tooltip geometry when BOScreen replaces
        // the projection matrix.
        for (GuiEventListener child : event.getScreen().children())
        {
            if (child instanceof BOScreen attachedScreen)
            {
                // Flush any queued vanilla draw calls before BOScreen replaces the projection matrix,
                // so they are committed to the framebuffer with the correct transform first.
                // Then clear the depth buffer so our UI is not clipped by depth values written by vanilla items.
                event.getGuiGraphics().flush();
                RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
                final PoseStack pose = event.getGuiGraphics().pose();
                pose.pushPose();
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
