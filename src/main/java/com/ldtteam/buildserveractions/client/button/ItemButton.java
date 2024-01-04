package com.ldtteam.buildserveractions.client.button;

import com.ldtteam.blockui.BOGuiGraphics;
import com.ldtteam.blockui.controls.ButtonVanilla;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

/**
 * Button class which renders a button, with an item as overlay over the button.
 */
public class ItemButton extends ButtonVanilla
{
    /**
     * The item stack to render.
     */
    protected ItemStack itemStack;

    /**
     * Spacing for top and left to apply to the icon.
     */
    protected int spacing = 0;

    /**
     * Set the item stack to render.
     *
     * @param itemStack the item stack.
     */
    public void setItem(final ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    /**
     * Set the spacing for the top and left to apply to the icon.
     *
     * @param spacing the spacing value.
     */
    public void setSpacing(final int spacing)
    {
        this.spacing = spacing;
    }

    /**
     * Called prior to rendering the icon.
     *
     * @param target the gui graphics.
     * @param mx     mouse x (relative to parent
     * @param my     mouse y (relative to parent)
     */
    protected void preRender(final BOGuiGraphics target, final double mx, final double my)
    {
        // Default empty
    }

    /**
     * Called after rendering the icon.
     *
     * @param target the gui graphics.
     * @param mx     mouse x (relative to parent
     * @param my     mouse y (relative to parent)
     */
    protected void postRender(final BOGuiGraphics target, final double mx, final double my)
    {
        // Default empty
    }

    @Override
    public final void drawSelf(final BOGuiGraphics target, final double mx, final double my)
    {
        super.drawSelf(target, mx, my);
        if (itemStack != null)
        {
            final PoseStack ms = target.pose();
            ms.pushPose();

            preRender(target, mx, my);

            target.renderItem(itemStack, x + spacing, y + spacing);

            postRender(target, mx, my);

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            ms.popPose();
        }
    }
}
