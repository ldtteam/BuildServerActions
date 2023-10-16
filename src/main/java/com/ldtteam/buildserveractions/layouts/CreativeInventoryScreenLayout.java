package com.ldtteam.buildserveractions.layouts;

import com.ldtteam.blockui.Alignment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.jetbrains.annotations.NotNull;

public class CreativeInventoryScreenLayout implements ActionRenderLayout<CreativeModeInventoryScreen>
{
    @Override
    public Class<CreativeModeInventoryScreen> getScreenClass()
    {
        return CreativeModeInventoryScreen.class;
    }

    @Override
    public Alignment getWindowAlignment()
    {
        return Alignment.MIDDLE_RIGHT;
    }

    @Override
    public int getWidthOffset(@NotNull final AbstractContainerScreen<?> screen)
    {
        return -16;
    }

    @Override
    public int getHeightOffset(final @NotNull AbstractContainerScreen<?> screen)
    {
        return screen.getYSize() / 2;
    }
}
