package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.widget.Widget;
import com.ldtteam.buildserveractions.widget.WidgetGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * The forge registry containing all the widget groups.
     */
    @Nullable
    private Registry<WidgetGroup> widgetGroups;

    /**
     * The forge registry containing all the widgets.
     */
    @Nullable
    private Registry<Widget> widgets;

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
     * Assign the widget group registry.
     *
     * @param registry the registry.
     */
    void setWidgetGroupRegistry(final Registry<WidgetGroup> registry)
    {
        this.widgetGroups = registry;
    }

    /**
     * Assign the widget registry.
     *
     * @param registry the registry.
     */
    void setWidgetRegistry(final Registry<Widget> registry)
    {
        this.widgets = registry;
    }

    /**
     * Obtain the amount of widgets.
     *
     * @return the amount of widget groups.
     */
    public int getWidgetGroupCount()
    {
        if (this.widgetGroups == null)
        {
            return 0;
        }
        return this.widgetGroups.keySet().size();
    }

    /**
     * Obtain the max amount of widgets in any group.
     *
     * @return the max amount of widgets in any group.
     */
    public int getMaxWidgetCountInGroup()
    {
        if (this.widgets == null)
        {
            return 0;
        }
        final Map<ResourceLocation, List<Widget>> grouped = this.widgets.stream().collect(Collectors.groupingBy(Widget::getGroupId));
        return grouped.values().stream().mapToInt(List::size).max().orElse(0);
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
        if (this.widgetGroups == null || this.widgets == null)
        {
            return null;
        }

        try
        {
            final Map.Entry<ResourceKey<WidgetGroup>, WidgetGroup> group = this.widgetGroups.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList().get(groupIndex);
            return this.widgets.entrySet()
                .stream()
                .filter(f -> f.getValue().getGroupId().equals(group.getValue().getId()))
                .sorted((s1, s2) -> group.getValue().getWidgetSorter().compare(s1.getValue(), s2.getValue()))
                .toList()
                .get(index)
                .getValue();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
