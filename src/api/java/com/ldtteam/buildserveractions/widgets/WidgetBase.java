package com.ldtteam.buildserveractions.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Root class for widgets.
 */
public interface WidgetBase
{
    /**
     * A unique ID for the widget.
     *
     * @return the unique ID.
     */
    @NotNull
    ResourceLocation getId();

    /**
     * The list of widgets this specific widget will appear under.
     *
     * @return the group ID.
     */
    @NotNull
    ResourceLocation getGroupId();

    /**
     * The name of the widget, shown as a tooltip.
     *
     * @return a component containing the widget name.
     */
    @NotNull
    Component getName();

    /**
     * Additional description in the widget, shown on the tooltip below the name.
     *
     * @return a component containing the description, or null.
     */
    @Nullable
    Component getDescription();
}
