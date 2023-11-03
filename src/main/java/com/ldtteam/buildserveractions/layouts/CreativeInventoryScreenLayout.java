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
        return Alignment.MIDDLE_LEFT;
    }

    @Override
    public int getWidthOffset(@NotNull final AbstractContainerScreen<?> screen)
    {
        return -10;
    }
}
