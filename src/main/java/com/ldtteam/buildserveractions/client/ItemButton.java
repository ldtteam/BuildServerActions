package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.MatrixUtils;
import com.ldtteam.blockui.controls.ButtonVanilla;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public class ItemButton extends ButtonVanilla
{
    private ItemStack itemStack;

    private int spacing = 0;

    public void setItem(final ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public void setSpacing(final int spacing)
    {
        this.spacing = spacing;
    }

    @Override
    public void drawSelf(final PoseStack ms, final double mx, final double my)
    {
        super.drawSelf(ms, mx, my);
        if (itemStack != null)
        {
            MatrixUtils.pushShaderMVstack(ms);

            mc.getItemRenderer().renderAndDecorateItem(itemStack, spacing, spacing);

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            MatrixUtils.popShaderMVstack();
        }
    }
}
