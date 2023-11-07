package com.ldtteam.buildserveractions.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Utility class for rendering clocks at a fixed time state.
 */
public class ClockItemStackUtilities
{
    /**
     * Key under which the fixed time value will be stored.
     */
    public static final String TIME_VALUE_KEY = "timeValue";

    private ClockItemStackUtilities()
    {
    }

    /**
     * Creates an {@link ItemStack} for a clock item, at a fixed time.
     *
     * @param timeValue the time value in a range of 0-1 scale.
     * @return the item stack.
     */
    public static ItemStack createItemStack(float timeValue)
    {
        final ItemStack clockStack = new ItemStack(Items.CLOCK);
        final CompoundTag clockStackTag = clockStack.getOrCreateTag();
        clockStackTag.putFloat(TIME_VALUE_KEY, timeValue);
        return clockStack;
    }
}
