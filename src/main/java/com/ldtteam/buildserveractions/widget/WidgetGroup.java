package com.ldtteam.buildserveractions.widget;

import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

/**
 * Widget group registry instance.
 */
public class WidgetGroup
{
    /**
     * The id of the group.
     */
    private final ResourceLocation groupId;

    /**
     * The sorter for widget instances used for this group.
     */
    private final Comparator<Widget> widgetSorter;

    /**
     * Creates a new widget group with the specified ID and sorter.
     *
     * @param groupId      the unique identifier for this group.
     * @param widgetSorter the comparator used to sort widgets within this group.
     */
    public WidgetGroup(final ResourceLocation groupId, final Comparator<Widget> widgetSorter)
    {
        this.groupId = groupId;
        this.widgetSorter = widgetSorter;
    }

    /**
     * Get the id of the group.
     *
     * @return the resource id.
     */
    public ResourceLocation getId()
    {
        return groupId;
    }

    /**
     * Get the sorter for widget instances used for this group.
     *
     * @return the comparator.
     */
    public Comparator<Widget> getWidgetSorter()
    {
        return widgetSorter;
    }

    @Override
    public int hashCode()
    {
        return groupId.hashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final WidgetGroup that = (WidgetGroup) o;

        return groupId.equals(that.groupId);
    }

    /**
     * Builder class for {@link WidgetGroup} instances.
     */
    public static class Builder
    {
        /**
         * Default instance of the widget sorter.
         */
        private static final Comparator<Widget> DEFAULT_WIDGET_SORTER = Comparator.comparing(Widget::getWidgetId);

        /**
         * The id of the group.
         */
        private final ResourceLocation groupId;

        /**
         * The sorter for widget instances used for this group.
         */
        private Comparator<Widget> widgetSorter = DEFAULT_WIDGET_SORTER;

        /**
         * Default constructor.
         *
         * @param groupId the id of the group.
         */
        public Builder(final ResourceLocation groupId)
        {
            this.groupId = groupId;
        }

        /**
         * Set the comparator used for sorting the list of widgets.
         * Defaults to a comparator that uses the widget id, alphabetically.
         *
         * @param widgetSorter the comparator.
         * @return builder for chaining.
         */
        public Builder setSorter(final Comparator<Widget> widgetSorter)
        {
            this.widgetSorter = widgetSorter;
            return this;
        }

        /**
         * Finalize construction of the {@link WidgetGroup} instance.
         *
         * @return the instance.
         */
        public WidgetGroup build()
        {
            return new WidgetGroup(groupId, widgetSorter != null ? widgetSorter : DEFAULT_WIDGET_SORTER);
        }
    }
}
