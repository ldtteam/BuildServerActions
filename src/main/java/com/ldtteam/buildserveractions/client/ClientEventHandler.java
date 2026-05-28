package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler
{
    public static final String CATEGORY = "key.category." + Constants.MOD_ID;

    /** Indexed 0–9, corresponding to favorite slots 1–10. */
    public static final KeyMapping[] FAVORITE_SLOTS = new KeyMapping[10];

    static
    {
        for (int i = 0; i < 10; i++)
        {
            FAVORITE_SLOTS[i] = new KeyMapping("key." + Constants.MOD_ID + ".favorite_slot_" + (i + 1), -1, CATEGORY);
        }
    }

    private ClientEventHandler() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        Loader.INSTANCE.register("snappinglist", SnappingScrollingList::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        for (final KeyMapping mapping : FAVORITE_SLOTS)
        {
            event.register(mapping);
        }
    }
}