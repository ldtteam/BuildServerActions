package com.ldtteam.buildserveractions.layouts;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public interface ActionRenderLayout<T extends AbstractContainerScreen<?>>
{
    Class<T> getScreenClass();
}
