package com.ldtteam.buildserveractions.layouts;

import com.ldtteam.blockui.Alignment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.NotNull;

public interface ActionRenderLayout<T extends AbstractContainerScreen<?>>
{
    Class<T> getScreenClass();

    default Alignment getWindowAlignment()
    {
        return Alignment.TOP_LEFT;
    }

    default int getWidthOffset(@NotNull AbstractContainerScreen<?> screen)
    {
        return 0;
    }

    default int getHeightOffset(@NotNull AbstractContainerScreen<?> screen)
    {
        return 0;
    }
}
