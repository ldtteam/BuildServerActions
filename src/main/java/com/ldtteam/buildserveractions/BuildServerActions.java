package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.client.ActionsListGuiEventHandler;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import com.ldtteam.buildserveractions.layouts.CreativeInventoryScreenLayout;
import com.ldtteam.buildserveractions.layouts.InventoryScreenLayout;
import com.ldtteam.buildserveractions.network.NetworkWidgetWrapper;
import com.ldtteam.buildserveractions.util.Constants;
import com.ldtteam.buildserveractions.util.Network;
import com.ldtteam.buildserveractions.widgets.NetworkWidget;
import com.ldtteam.buildserveractions.widgets.PlayerGameTypeSwitchWidget;
import com.ldtteam.buildserveractions.widgets.Widget;
import com.ldtteam.buildserveractions.widgets.WidgetBase;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class BuildServerActions
{
    /**
     * Mod entrypoint.
     */
    public BuildServerActions()
    {
        Network.getNetwork().registerCommonMessages();

        MinecraftForge.EVENT_BUS.register(ActionsListGuiEventHandler.class);

        final BuildServerActionsCallbacksImpl buildServerActionsCallbacks = new BuildServerActionsCallbacksImpl();
        BuildServerActionsAPI.setup(buildServerActionsCallbacks);

        registerDefaultLayouts(buildServerActionsCallbacks);
        registerDefaultWidgets(buildServerActionsCallbacks);
    }

    /**
     * Adds all the natively integrated widgets.
     */
    private void registerDefaultLayouts(final BuildServerActionsCallbacks buildServerActionsCallbacks)
    {
        buildServerActionsCallbacks.registerLayout(new InventoryScreenLayout());
        buildServerActionsCallbacks.registerLayout(new CreativeInventoryScreenLayout());
    }

    /**
     * Adds all the natively integrated widgets.
     */
    private void registerDefaultWidgets(final BuildServerActionsCallbacks buildServerActionsCallbacks)
    {
        buildServerActionsCallbacks.registerWidget(new PlayerGameTypeSwitchWidget(GameType.SURVIVAL, new ItemStack(Items.IRON_SWORD)));
        buildServerActionsCallbacks.registerWidget(new PlayerGameTypeSwitchWidget(GameType.CREATIVE, new ItemStack(Items.GRASS_BLOCK)));
        buildServerActionsCallbacks.registerWidget(new PlayerGameTypeSwitchWidget(GameType.SPECTATOR, new ItemStack(Items.ENDER_EYE)));
        buildServerActionsCallbacks.registerWidget(new PlayerGameTypeSwitchWidget(GameType.ADVENTURE, new ItemStack(Items.MAP)));
    }

    /**
     * Backing implementation for {@link BuildServerActions}.
     */
    public static class BuildServerActionsCallbacksImpl implements BuildServerActionsCallbacks
    {
        @Override
        public void registerLayout(final ActionRenderLayout<?> layout)
        {
            LayoutManager.getInstance().registerLayout(layout);
        }

        @Override
        public void registerWidget(final WidgetBase widgetBase)
        {
            if (widgetBase instanceof Widget widget)
            {
                WidgetManager.getInstance().addWidget(widget);
            }

            if (widgetBase instanceof NetworkWidget<?> networkWidget)
            {
                Network.getNetwork().registerMessage(networkWidget::constructEmptyMessage);
                WidgetManager.getInstance().addWidget(new NetworkWidgetWrapper(networkWidget));
            }
        }
    }
}
