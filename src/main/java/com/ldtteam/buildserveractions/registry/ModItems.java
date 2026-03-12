package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.items.CustomDebugStick;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredHolder<Item, CustomDebugStick> DEBUG_STICK = DEFERRED_REGISTER.register("debug_stick", CustomDebugStick::new);
}
