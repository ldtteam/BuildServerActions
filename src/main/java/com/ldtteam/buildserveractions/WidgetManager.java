package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
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
    private IForgeRegistry<WidgetRegistries.WidgetGroup> widgetGroups;

    /**
     * The forge registry containing all the widgets.
     */
    @Nullable
    private IForgeRegistry<WidgetRegistries.Widget> widgets;

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
    void setWidgetGroupRegistry(final IForgeRegistry<WidgetRegistries.WidgetGroup> registry)
    {
        this.widgetGroups = registry;
    }

    /**
     * Assign the widget registry.
     *
     * @param registry the registry.
     */
    void setWidgetRegistry(final IForgeRegistry<WidgetRegistries.Widget> registry)
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
        return this.widgetGroups.getKeys().size();
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
        final Map<ResourceLocation, List<WidgetRegistries.Widget>> grouped = this.widgets.getValues().stream()
                                                                               .collect(Collectors.groupingBy(WidgetRegistries.Widget::getGroupId));
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
    public WidgetRegistries.Widget getWidget(final int groupIndex, final int index)
    {
        if (this.widgetGroups == null || this.widgets == null)
        {
            return null;
        }

        try
        {
            final ResourceLocation groupId = this.widgetGroups.getKeys().stream()
                                               .sorted(ResourceLocation::compareTo)
                                               .toList()
                                               .get(groupIndex);
            return this.widgets.getEntries().stream()
                     .filter(f -> f.getKey().location().equals(groupId))
                     .sorted(Map.Entry.comparingByKey())
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
