package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import com.ldtteam.buildserveractions.util.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public class ActionsListGui
{
    private final AbstractContainerScreen<?> screen;

    private final ActionRenderLayout<?> layout;

    private final View root;

    public ActionsListGui(final AbstractContainerScreen<?> screen, final ActionRenderLayout<?> layout)
    {
        this.screen = screen;
        this.layout = layout;

        this.root = new View();

        final ImageRepeatable background = new ImageRepeatable();
        background.setAlignment(layout.getWindowAlignment());
        background.setSize(50, 50);
        background.setImageLoc(new ResourceLocation(Constants.MOD_ID, "textures/gui/background.png"));
        background.setImageSize(0, 0, 18, 18, 6, 6, 6, 6);
        this.root.addChild(background);
    }

    public AbstractContainerScreen<?> getScreen()
    {
        return this.screen;
    }

    public View getRoot()
    {
        return this.root;
    }

    public void draw(final PoseStack poseStack, final int mouseX, final int mouseY)
    {
        poseStack.pushPose();
        poseStack.translate(this.screen.getGuiLeft(), this.screen.getGuiTop(), 0);
        poseStack.translate(this.layout.getWidthOffset(this.screen), this.layout.getHeightOffset(this.screen), 0);
        this.root.draw(poseStack, mouseX, mouseY);
        this.root.drawLast(poseStack, mouseX, mouseY);
        poseStack.popPose();

        this.root.onUpdate();
    }
}
