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
    private final ActionRenderLayout<?>      layout;
    private final View                       root;
    private       AbstractContainerScreen<?> screen;

    public ActionsListGui(final AbstractContainerScreen<?> screen, final ActionRenderLayout<?> layout)
    {
        this.screen = screen;
        this.layout = layout;

        this.root = new View();

        final ImageRepeatable background = new ImageRepeatable();
        background.setSize(50, 50);
        background.setImageLoc(new ResourceLocation(Constants.MOD_ID, "textures/gui/background.png"));
        background.setImageSize(0, 0, 18, 18, 6, 6, 6, 6);
        this.root.addChild(background);
    }

    public View getRoot()
    {
        return this.root;
    }

    public void draw(final PoseStack poseStack, final int mouseX, final int mouseY)
    {
        this.root.setPosition(screen.getGuiLeft() - 50, screen.getGuiTop());
        this.root.setSize(screen.getXSize(), screen.getYSize());

        final float guiScale = (float) screen.getMinecraft().getWindow().getGuiScale();

        poseStack.pushPose();
        poseStack.scale(guiScale, guiScale, 1);
        this.root.draw(poseStack, mouseX, mouseY);
        this.root.drawLast(poseStack, mouseX, mouseY);
        poseStack.popPose();

        this.root.onUpdate();
    }
}
