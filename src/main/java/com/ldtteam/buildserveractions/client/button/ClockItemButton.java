package com.ldtteam.buildserveractions.client.button;

import com.ldtteam.blockui.BOGuiGraphics;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import static com.ldtteam.buildserveractions.util.ClockItemStackUtilities.TIME_VALUE_KEY;

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
        if (itemStack.is(Items.CLOCK) && itemStack.getTag() != null && itemStack.getTag().contains(TIME_VALUE_KEY) && mc.level != null)
        {
            final float timeValue = itemStack.getTag().getFloat(TIME_VALUE_KEY);
            initialFunction = ItemProperties.getProperty(Items.CLOCK, new ResourceLocation("time"));
            ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), (pStack, pLevel, pEntity, pSeed) -> timeValue);
        }
    }

    @Override
    protected void postRender(final BOGuiGraphics ms, final double mx, final double my)
    {
        if (initialFunction != null)
        {
            ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), initialFunction);
            initialFunction = null;
        }
    }
}
