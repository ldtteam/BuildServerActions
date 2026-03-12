package com.ldtteam.buildserveractions.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

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
        final CompoundTag tag = new CompoundTag();
        tag.putFloat(TIME_VALUE_KEY, timeValue);
        clockStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return clockStack;
    }

    /**
     * Gets the time value from a clock item stack.
     *
     * @param stack the item stack.
     * @return the time value, or -1 if not present.
     */
    public static float getTimeValue(final ItemStack stack)
    {
        final CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null)
        {
            final CompoundTag tag = customData.copyTag();
            if (tag.contains(TIME_VALUE_KEY))
            {
                return tag.getFloat(TIME_VALUE_KEY);
            }
        }
        return -1f;
    }
}
