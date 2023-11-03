package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.widgets.Widget;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manager class for all widgets.
 */
public class WidgetManager
{
    /**
     * The singleton instance.
     */
    private static WidgetManager instance;

    /**
     * The map of widgets, separated by widget group.
     */
    private final Map<ResourceLocation, List<Widget>> widgetGroups = new HashMap<>();

    /**
     * List of keys, sorted.
     */
    private final List<ResourceLocation> sortedKeys = new ArrayList<>();

    /**
     * Obtain the {@link WidgetManager} instance.
     *
     * @return the singleton instance.
     */
    public static WidgetManager getInstance()
    {
        if (instance == null)
        {
            instance = new WidgetManager();
        }
        return instance;
    }

    /**
     * Obtain the map of widgets, grouped by their widget group.
     *
     * @return an unmodifiable map of the widgets.
     */
    public Map<ResourceLocation, List<Widget>> getWidgets()
    {
        return Collections.unmodifiableMap(this.widgetGroups);
    }

    /**
     * Obtain a widget group by its sorted index.
     * This way there's no shifting in groups (which may happen with maps).
     *
     * @param index the widget group index.
     * @return the list of widgets for this group, or null if bounds were exceeded.
     */
    @Nullable
    public List<Widget> getWidgetGroup(final int index)
    {
        try
        {
            return this.widgetGroups.get(this.sortedKeys.get(index));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Obtain a widget by its group and index.
     *
     * @param groupIndex the index of the group the widget is in.
     * @param index      the index of the widget itself.
     * @return the widget, or null.
     */
    @Nullable
    public Widget getWidget(final int groupIndex, final int index)
    {
        final List<Widget> widgets = getWidgetGroup(groupIndex);
        if (widgets == null)
        {
            return null;
        }
        return widgets.size() > index ? widgets.get(index) : null;
    }

    /**
     * Adds a widget to the manager, may only be called internally.
     *
     * @param widget the new widget.
     */
    void addWidget(final Widget widget)
    {
        this.widgetGroups.putIfAbsent(widget.getGroupId(), new ArrayList<>());
        this.widgetGroups.get(widget.getGroupId()).add(widget);
        if (!this.sortedKeys.contains(widget.getGroupId()))
        {
            this.sortedKeys.add(widget.getGroupId());
        }
        this.sortedKeys.sort(ResourceLocation::compareTo);
    }
}
