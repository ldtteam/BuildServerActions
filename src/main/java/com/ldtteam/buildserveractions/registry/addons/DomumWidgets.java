package com.ldtteam.buildserveractions.registry.addons;

import com.ldtteam.buildserveractions.handlers.addons.domum.OpenCutterWidgetCallbacks;
import com.ldtteam.buildserveractions.registry.ModWidgetGroups;
import com.ldtteam.buildserveractions.registry.ModWidgets;
import com.ldtteam.buildserveractions.widget.Widget;
import com.ldtteam.buildserveractions.widget.WidgetGroup;
import com.ldtteam.domumornamentum.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_DOMUM_OPEN_CUTTER_DESC;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_DOMUM_OPEN_CUTTER_NAME;

/**
 * Widget registrations for Domum Ornamentum integration.
 * This class is isolated to prevent class loading issues when the addon is not present.
 */
@SuppressWarnings("unused")
public class DomumWidgets
{
    public static final ResourceLocation GROUP_DOMUM_ID       = modId("group-domum-ornamentum");
    public static final ResourceLocation DOMUM_OPEN_CUTTER_ID = modId("domum-open-cutter");

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_DOMUM = ModWidgetGroups.register(GROUP_DOMUM_ID);

    public static final DeferredHolder<Widget, Widget> DOMUM_OPEN_CUTTER = ModWidgets.register(GROUP_DOMUM_ID, DOMUM_OPEN_CUTTER_ID,
        builder -> builder
            .setName(Component.translatable(WIDGET_DOMUM_OPEN_CUTTER_NAME))
            .setDescription(Component.translatable(WIDGET_DOMUM_OPEN_CUTTER_DESC))
            .setIcon(ModBlocks.getInstance().getArchitectsCutter().asItem().getDefaultInstance())
            .setHandler(OpenCutterWidgetCallbacks::handler));

    /**
     * Initialize the Domum Ornamentum widgets.
     */
    public static void init()
    {
    }
}
