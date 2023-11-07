package com.ldtteam.buildserveractions.client.button;

import com.ldtteam.blockui.MatrixUtils;
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
     * @param ms the pose stack.
     * @param mx mouse x (relative to parent
     * @param my mouse y (relative to parent)
     */
    protected void preRender(final PoseStack ms, final double mx, final double my)
    {
        // Default empty
    }

    /**
     * Called after rendering the icon.
     *
     * @param ms the pose stack.
     * @param mx mouse x (relative to parent
     * @param my mouse y (relative to parent)
     */
    protected void postRender(final PoseStack ms, final double mx, final double my)
    {
        // Default empty
    }

    @Override
    public final void drawSelf(final PoseStack ms, final double mx, final double my)
    {
        super.drawSelf(ms, mx, my);
        if (itemStack != null)
        {
            MatrixUtils.pushShaderMVstack(ms);

            preRender(ms, mx, my);

            mc.getItemRenderer().renderAndDecorateItem(itemStack, x + spacing, y + spacing);

            postRender(ms, mx, my);

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            MatrixUtils.popShaderMVstack();
        }
    }
}
