package com.ldtteam.buildserveractions.client.button;

import com.ldtteam.blockui.BOGuiGraphics;
import com.ldtteam.buildserveractions.util.ClockItemStackUtilities;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Button class which renders a button, with an item as overlay over the button.
 * Specific for Clocks, as they require an additional rendering requirement.
 */
public final class ClockItemButton extends ItemButton
{
    private ItemPropertyFunction initialFunction = null;

    @Override
    protected void preRender(final BOGuiGraphics ms, final double mx, final double my)
    {
        if (itemStack.is(Items.CLOCK) && mc.level != null)
        {
            final float timeValue = ClockItemStackUtilities.getTimeValue(itemStack);
            if (timeValue >= 0)
            {
                initialFunction = ItemProperties.getProperty(itemStack, ResourceLocation.parse("time"));
                ItemProperties.register(Items.CLOCK, ResourceLocation.parse("time"), (pStack, pLevel, pEntity, pSeed) -> timeValue);
            }
        }
    }

    @Override
    protected void postRender(final BOGuiGraphics ms, final double mx, final double my)
    {
        if (initialFunction != null)
        {
            ItemProperties.register(Items.CLOCK, ResourceLocation.parse("time"), initialFunction);
            initialFunction = null;
        }
    }
}
