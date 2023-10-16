package com.ldtteam.buildserveractions.widgets;

import net.minecraft.world.entity.player.Player;

/**
 * Implementation for any widget present on the actions GUI.
 */
public interface Widget extends WidgetBase
{
    /**
     * Callback for the widget, this method is only invoked on the client side, networking to the server has to be implemented manually for each widget.
     *
     * @param player the player who triggered the widget.
     */
    void handle(Player player);
}
