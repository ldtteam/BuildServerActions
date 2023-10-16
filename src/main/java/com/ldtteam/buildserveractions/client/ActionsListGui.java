package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import com.ldtteam.buildserveractions.util.Constants;
import com.ldtteam.buildserveractions.widgets.Widget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ActionsListGui
{
    private static final int WIDGET_SIZE    = 20;
    private static final int WIDGET_SPACING = 4;

    private final AbstractContainerScreen<?> screen;

    private final ActionRenderLayout<?> layout;

    private final View root;

    public ActionsListGui(final AbstractContainerScreen<?> screen, final ActionRenderLayout<?> layout)
    {
        this.screen = screen;
        this.layout = layout;

        final ImageRepeatable background = new ImageRepeatable();
        background.setImageLoc(new ResourceLocation(Constants.MOD_ID, "textures/gui/background.png"));
        background.setImageSize(0, 0, 18, 18, 6, 6, 6, 6);

        final View container = new View();
        container.setAlignment(layout.getWindowAlignment());

        int rows = 0;
        int columns = 0;
        for (List<Widget> widgets : WidgetManager.getInstance().getWidgets().values())
        {
            int rowColumnCount = 0;
            for (Widget widget : widgets)
            {
                final View box = new View();
                box.setPosition(WIDGET_SPACING + (rows * (WIDGET_SIZE * WIDGET_SPACING)), WIDGET_SPACING + (rowColumnCount * (WIDGET_SIZE * WIDGET_SPACING)));
                box.setSize(WIDGET_SIZE, WIDGET_SIZE);
                box.setAlignment(Alignment.MIDDLE);

                final ItemIcon item = new ItemIcon();
                item.setSize(16, 16);
                item.setItem(widget.getIcon());
                box.addChild(item);

                container.addChild(box);

                rowColumnCount++;
                if (rowColumnCount > columns)
                {
                    columns = rowColumnCount;
                }
            }
            rows++;
        }

        container.setSize(WIDGET_SPACING + (rows * (WIDGET_SIZE + WIDGET_SPACING)), WIDGET_SPACING + (columns * (WIDGET_SIZE + WIDGET_SPACING)));
        background.setSize(container.getWidth(), container.getHeight());

        container.addChild(background);

        this.root = new View();
        this.root.addChild(container);
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
    }
}
