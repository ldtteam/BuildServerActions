package com.ldtteam.buildserveractions.layouts;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class InventoryScreenLayout implements ActionRenderLayout<InventoryScreen>
{
    @Override
    public Class<InventoryScreen> getScreenClass()
    {
        return InventoryScreen.class;
    }
}
