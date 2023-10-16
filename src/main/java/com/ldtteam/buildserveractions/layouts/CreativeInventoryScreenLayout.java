package com.ldtteam.buildserveractions.layouts;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

public class CreativeInventoryScreenLayout implements ActionRenderLayout<CreativeModeInventoryScreen>
{
    @Override
    public Class<CreativeModeInventoryScreen> getScreenClass()
    {
        return CreativeModeInventoryScreen.class;
    }
}
