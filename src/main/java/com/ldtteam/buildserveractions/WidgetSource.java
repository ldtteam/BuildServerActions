package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import net.minecraft.world.entity.player.Player;

/**
 * Class for widget invocation manager.
 *
 * @param widget The widget which was called.
 * @param player The player who called the widget.
 */
public record WidgetSource(Widget widget, Player player)
{
}
