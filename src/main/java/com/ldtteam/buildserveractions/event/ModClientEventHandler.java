package com.ldtteam.buildserveractions.event;

import com.ldtteam.blockui.AtlasManager;
import com.ldtteam.blockui.Loader;
import com.ldtteam.buildserveractions.client.SnappingScrollingList;
import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Client-side event handler for the mod.
 */
public class ModClientEventHandler
{
    /**
     * The key mapping category name for all Build Server Actions bindings.
     */
    public static final String CATEGORY = "key.category." + Constants.MOD_ID;

    /**
     * Indexed 0–9, corresponding to favorite slots 1–10.
     */
    public static final KeyMapping[] FAVORITE_SLOTS = new KeyMapping[10];
    static
    {
        for (int i = 0; i < 10; i++)
        {
            FAVORITE_SLOTS[i] = new KeyMapping("key." + Constants.MOD_ID + ".favorite_slot_" + (i + 1), -1, CATEGORY);
        }
    }
    /**
     * Private constructor to prevent instantiation.
     */
    private ModClientEventHandler()
    {
    }

    /**
     * Registers the {@link SnappingScrollingList} pane type with the BlockUI loader.
     *
     * @param event the client setup event.
     */
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        Loader.INSTANCE.register("snappinglist", SnappingScrollingList::new);
    }

    /**
     * Register client reload listeners for the atlas.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void registerClientReloadListeners(final RegisterClientReloadListenersEvent event)
    {
        AtlasManager.INSTANCE.addAtlas(event::registerReloadListener, Constants.MOD_ID);
    }

    /**
     * Registers all favorite-slot key mappings.
     *
     * @param event the key mappings registration event.
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        for (final KeyMapping mapping : FAVORITE_SLOTS)
        {
            event.register(mapping);
        }
    }
}
