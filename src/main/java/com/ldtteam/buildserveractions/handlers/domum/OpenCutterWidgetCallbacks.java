package com.ldtteam.buildserveractions.handlers.domum;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.domumornamentum.container.ArchitectsCutterContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;

/**
 * Callbacks for opening the architects cutter window of Domum Ornamentum.
 */
public class OpenCutterWidgetCallbacks
{
    /**
     * Action callback for opening the architects cutter window.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        source.player()
          .openMenu(new SimpleMenuProvider((id, inventory, player) -> new ArchitectsCutterContainer(id, inventory), Component.translatable("domum_ornamentum.architectscutter")));
    }
}
