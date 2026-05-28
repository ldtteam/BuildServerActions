package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.widget.Widget;
import com.ldtteam.buildserveractions.widget.WidgetGroup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
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
    private IForgeRegistry<WidgetGroup> widgetGroups;

    /**
     * The forge registry containing all the widgets.
     */
    @Nullable
    private IForgeRegistry<Widget> widgets;

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
    void setWidgetGroupRegistry(final IForgeRegistry<WidgetGroup> registry)
    {
        this.widgetGroups = registry;
    }

    /**
     * Assign the widget registry.
     *
     * @param registry the registry.
     */
    void setWidgetRegistry(final IForgeRegistry<Widget> registry)
    {
        this.widgets = registry;
    }

    /**
     * Writes a widget
     *
     * @param buf network data byte buffer.
     * @return the widget, or null if a problem occurred on the sending side.
     */
    public Widget readWidgetFromBuffer(final FriendlyByteBuf buf)
    {
        if (buf.readBoolean())
        {
            return buf.readRegistryIdSafe(Widget.class);
        }
        return null;
    }

    /**
     * Writes a widget to a network data byte buffer.
     *
     * @param buf    network data byte buffer.
     * @param widget the widget to write.
     */
    public void writeWidgetToBuffer(final FriendlyByteBuf buf, final Widget widget)
    {
        if (this.widgets == null || widget == null)
        {
            buf.writeBoolean(false);
            return;
        }
        buf.writeBoolean(true);
        buf.writeRegistryId(this.widgets, widget);
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
        final Map<ResourceLocation, List<Widget>> grouped = this.widgets.getValues().stream().collect(Collectors.groupingBy(Widget::getGroupId));
        return grouped.values().stream().mapToInt(List::size).max().orElse(0);
    }

    /**
     * Obtain a widget by its resource location ID.
     *
     * @param widgetId the resource location.
     * @return the widget, or null if not found.
     */
    @Nullable
    public Widget getWidgetById(final ResourceLocation widgetId)
    {
        if (this.widgets == null)
        {
            return null;
        }
        return this.widgets.getValue(widgetId);
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
            final Map.Entry<ResourceKey<WidgetGroup>, WidgetGroup> group = this.widgetGroups.getEntries().stream().sorted(Map.Entry.comparingByKey()).toList().get(groupIndex);
            return this.widgets.getEntries()
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
