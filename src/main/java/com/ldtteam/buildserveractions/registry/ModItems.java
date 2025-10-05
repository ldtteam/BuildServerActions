package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.items.CustomDebugStick;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> DEBUG_STICK = DEFERRED_REGISTER.register("debug_stick", CustomDebugStick::new);
}
