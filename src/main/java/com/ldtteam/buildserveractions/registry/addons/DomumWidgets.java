package com.ldtteam.buildserveractions.registry.addons;

import com.ldtteam.buildserveractions.handlers.OpenWindowWidgetCallback;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import com.ldtteam.buildserveractions.widget.Widget;
import com.ldtteam.domumornamentum.block.ModBlocks;
import com.ldtteam.domumornamentum.container.ArchitectsCutterContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.handlers.OpenWindowWidgetCallback.WIDGET_WINDOW_MENU_PROVIDER;
import static com.ldtteam.buildserveractions.registry.ModWidgetGroups.GROUP_WINDOWS_ID;

/**
 * Widget registrations for Domum Ornamentum integration.
 * This class is isolated to prevent class loading issues when the addon is not present.
 */
@SuppressWarnings("unused")
public class DomumWidgets
{
    public static final ResourceLocation DOMUM_OPEN_CUTTER_ID = modId("domum-open-cutter");

    public static final DeferredHolder<Widget, Widget> DOMUM_OPEN_CUTTER = ModWidgets.register(GROUP_WINDOWS_ID,
        DOMUM_OPEN_CUTTER_ID,
        builder -> builder.setName(OpenWindowWidgetCallback::name)
            .setDescription(OpenWindowWidgetCallback::description)
            .setIcon(ModBlocks.getInstance().getArchitectsCutter().asItem().getDefaultInstance())
            .setHandler(OpenWindowWidgetCallback::handler)
            .addMetadata(WIDGET_WINDOW_MENU_PROVIDER,
                new SimpleMenuProvider((id, inv, player) -> new ArchitectsCutterContainer(id, inv), Component.translatable("domum_ornamentum.architectscutter"))));

    /**
     * Initialize the Domum Ornamentum widgets.
     */
    public static void init()
    {
    }
}
