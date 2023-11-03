package com.ldtteam.buildserveractions.layouts;

import com.ldtteam.blockui.Alignment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.NotNull;

public class InventoryScreenLayout implements ActionRenderLayout<InventoryScreen>
{
    @Override
    public Class<InventoryScreen> getScreenClass()
    {
        return InventoryScreen.class;
    }

    @Override
    public Alignment getWindowAlignment()
    {
        return Alignment.MIDDLE_LEFT;
    }

    @Override
    public int getWidthOffset(@NotNull final AbstractContainerScreen<?> screen)
    {
        return -10;
    }
}
