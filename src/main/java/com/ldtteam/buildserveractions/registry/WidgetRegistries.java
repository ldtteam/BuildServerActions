package com.ldtteam.buildserveractions.registry;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.util.records.SizeI;
import com.ldtteam.buildserveractions.WidgetSource;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * All the registry information for managing widgets.
 */
@SuppressWarnings({"java:S1104", "java:S1444"})
public class WidgetRegistries
{
    public static RegistryObject<WidgetLayout> layoutSurvivalInventory;
    public static RegistryObject<WidgetLayout> layoutCreativeInventory;

    public static RegistryObject<WidgetGroup> groupGamemodes;
    public static RegistryObject<WidgetGroup> groupTime;
    public static RegistryObject<WidgetGroup> groupSpeed;
    public static RegistryObject<WidgetGroup> groupItems;

    public static RegistryObject<Widget> widgetGamemodeSurvival;
    public static RegistryObject<Widget> widgetGamemodeCreative;
    public static RegistryObject<Widget> widgetGamemodeAdventure;
    public static RegistryObject<Widget> widgetGamemodeSpectator;

    public static RegistryObject<Widget> widgetTimeNoon;
    public static RegistryObject<Widget> widgetTimeMidnight;

    public static RegistryObject<Widget> widgetSpeed01;
    public static RegistryObject<Widget> widgetSpeed02;
    public static RegistryObject<Widget> widgetSpeed05;
    public static RegistryObject<Widget> widgetSpeed10;

    public static RegistryObject<Widget> widgetItemBarrierBlock;
    public static RegistryObject<Widget> widgetItemDebugStick;
    public static RegistryObject<Widget> widgetItemInvisibleItemFrame;
    public static RegistryObject<Widget> widgetItemJigsawBlock;
    public static RegistryObject<Widget> widgetItemStructureBlock;
    public static RegistryObject<Widget> widgetItemStructureVoid;

    /**
     * Widget layout registry instance.
     */
    public static class WidgetLayout
    {
        /**
         * The class type of which screen the widget layout gets attached to.
         */
        private final Class<? extends AbstractContainerScreen<?>> screenClass;

        /**
         * The maximum amount of button columns visible at once.
         */
        private final int maxGroups;

        /**
         * The maximum amount of buttons visible per column.
         */
        private final int maxButtonsInGroup;

        /**
         * The alignment of the window compared to the attached screen.
         */
        private final Alignment alignment;

        /**
         * An additional offset compared to the position where the window is aligned.
         */
        private final Function<AbstractContainerScreen<?>, SizeI> offsetFunc;

        /**
         * Default internal constructor.
         */
        public WidgetLayout(
          final Class<? extends AbstractContainerScreen<?>> screenClass,
          final int maxGroups,
          final int maxButtonsInGroup,
          final Alignment alignment,
          final Function<AbstractContainerScreen<?>, SizeI> offsetFunc)
        {
            this.screenClass = screenClass;
            this.maxGroups = maxGroups;
            this.maxButtonsInGroup = maxButtonsInGroup;
            this.alignment = alignment;
            this.offsetFunc = offsetFunc;
        }

        /**
         * Get the class type of which screen the widget layout gets attached to.
         */
        public Class<? extends AbstractContainerScreen<?>> getScreenClass()
        {
            return screenClass;
        }

        /**
         * Get the maximum amount of button columns visible at once.
         *
         * @return the selected amount.
         */
        public int getMaxGroups()
        {
            return maxGroups;
        }

        /**
         * Get the maximum amount of buttons visible per column.
         *
         * @return the selected amount.
         */
        public int getMaxButtonsInGroup()
        {
            return maxButtonsInGroup;
        }

        /**
         * Get the alignment of the window compared to the attached screen.
         *
         * @return the selected alignment.
         */
        public Alignment getAlignment()
        {
            return alignment;
        }

        /**
         * Get an additional offset compared to the position where the window is aligned.
         *
         * @return the selected offset.
         */
        public Function<AbstractContainerScreen<?>, SizeI> getOffsetFunction()
        {
            return offsetFunc;
        }

        @Override
        public int hashCode()
        {
            return screenClass.hashCode();
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

            final WidgetLayout that = (WidgetLayout) o;

            return screenClass.equals(that.screenClass);
        }

        /**
         * Builder class for {@link WidgetLayout} instances.
         */
        public static class Builder
        {
            /**
             * The class type of which screen the widget layout gets attached to.
             */
            private final Class<? extends AbstractContainerScreen<?>> screenClass;

            /**
             * The maximum amount of button columns visible at once.
             */
            private int maxGroups = 5;

            /**
             * The maximum amount of buttons visible per column.
             */
            private int maxButtonsInGroup = 5;

            /**
             * The alignment of the window compared to the attached screen.
             */
            private Alignment alignment = Alignment.TOP_LEFT;

            /**
             * An additional offset compared to the position where the window is aligned.
             */
            private Function<AbstractContainerScreen<?>, SizeI> offsetFunc = screen -> new SizeI(0, 0);

            /**
             * Default constructor.
             *
             * @param screenClass the class type of the window which to attach to.
             */
            public Builder(final Class<? extends AbstractContainerScreen<?>> screenClass)
            {
                this.screenClass = screenClass;
            }

            /**
             * Set the maximum amount of button columns visible at once.
             * Defaults to 5.
             *
             * @param maxGroups the new amount.
             * @return builder for chaining.
             */
            public Builder setMaxGroups(final int maxGroups)
            {
                this.maxGroups = maxGroups;
                return this;
            }

            /**
             * Set the maximum amount of buttons visible per column.
             * Defaults to 5.
             *
             * @param maxButtonsInGroup the new amount.
             * @return builder for chaining.
             */
            public Builder setMaxButtonsInGroup(final int maxButtonsInGroup)
            {
                this.maxButtonsInGroup = maxButtonsInGroup;
                return this;
            }

            /**
             * Set the alignment of the window compared to the attached screen.
             * Defaults to {@link Alignment#TOP_LEFT}.
             *
             * @param alignment the new alignment.
             * @return builder for chaining.
             */
            public Builder setAlignment(final Alignment alignment)
            {
                this.alignment = alignment;
                return this;
            }

            /**
             * Set an additional offset compared to the position where the window is aligned.
             * Defaults to 0 on both axis.
             *
             * @param offset the new offset.
             * @return builder for chaining.
             */
            public Builder setOffset(final SizeI offset)
            {
                this.offsetFunc = screen -> offset;
                return this;
            }

            /**
             * Set an additional offset compared to the position where the window is aligned.
             * Defaults to 0 on both axis.
             *
             * @param offsetFunc a function to determine the new offset.
             * @return builder for chaining.
             */
            public Builder setOffset(final Function<AbstractContainerScreen<?>, SizeI> offsetFunc)
            {
                this.offsetFunc = offsetFunc;
                return this;
            }

            /**
             * Finalize construction of the {@link WidgetLayout} instance.
             *
             * @return the instance.
             */
            public WidgetLayout build()
            {
                return new WidgetLayout(screenClass, maxGroups, maxButtonsInGroup, alignment, offsetFunc);
            }
        }
    }

    /**
     * Widget group registry instance.
     */
    public static class WidgetGroup
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
         * Default internal constructor.
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
         * Builder class for {@link WidgetLayout} instances.
         */
        public static class Builder
        {
            /**
             * Default instance of the widget sorter.
             */
            private static final Comparator<Widget> DEFAULT_WIDGET_SORTER = Comparator.comparing(c -> c.widgetId);

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

    /**
     * Widget registry instance.
     */
    public static class Widget
    {
        /**
         * The group of the widget.
         */
        private final ResourceLocation groupId;

        /**
         * The id of the widget.
         */
        private final ResourceLocation widgetId;

        /**
         * The component for the name.
         */
        private final Function<Widget, Component> name;

        /**
         * The component for the description.
         */
        private final Function<Widget, Component> description;

        /**
         * The icon.
         */
        private final ItemStack icon;

        /**
         * Metadata on this widget object.
         */
        private final Map<String, Object> metadata;

        /**
         * The handler function for this widget.
         */
        private final Consumer<WidgetSource> handler;

        /**
         * The client handler function for this widget.
         */
        @Nullable
        private final Consumer<WidgetSource> clientHandler;

        /**
         * Default internal constructor.
         */
        private Widget(
          final ResourceLocation groupId,
          final ResourceLocation widgetId,
          final Function<Widget, Component> name,
          final Function<Widget, Component> description,
          final ItemStack icon,
          final Map<String, Object> metadata,
          final Consumer<WidgetSource> handler,
          final @Nullable Consumer<WidgetSource> clientHandler)
        {
            this.groupId = groupId;
            this.widgetId = widgetId;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.metadata = metadata;
            this.handler = handler;
            this.clientHandler = clientHandler;
        }

        /**
         * Get the group of this widget.
         *
         * @return the resource id.
         */
        public ResourceLocation getGroupId()
        {
            return groupId;
        }

        /**
         * Get the id of this widget.
         *
         * @return the resource id.
         */
        public ResourceLocation getWidgetId()
        {
            return widgetId;
        }

        /**
         * Get the name of this widget.
         *
         * @return the component.
         */
        public Function<Widget, Component> getName()
        {
            return name;
        }

        /**
         * Get the description of this widget.
         *
         * @return the component.
         */
        public Function<Widget, Component> getDescription()
        {
            return description;
        }

        /**
         * Get the icon of this widget.
         *
         * @return the item stack.
         */
        public ItemStack getIcon()
        {
            return icon;
        }

        /**
         * Get a value from the metadata of this widget.
         *
         * @param key the key of the metadata.
         * @return the object or null if not exists.
         */
        public Object getMetadataValue(final String key)
        {
            return metadata.get(key);
        }

        /**
         * Get a value from the metadata of this widget.
         * Will attempt to cast the value to the correct type argument.
         *
         * @param key  the key of the metadata.
         * @param type the expected type of the value.
         * @return the object or null if not exists.
         * @throws ClassCastException when the value of the key does not match the given type.
         */
        @SuppressWarnings("unchecked")
        public <T> T getMetadataValue(final String key, final Class<T> type)
        {
            final Object value = getMetadataValue(key);
            if (type.isInstance(value))
            {
                return (T) value;
            }
            return null;
        }

        /**
         * Get the handler function of this widget.
         *
         * @return the callback.
         */
        public Consumer<WidgetSource> getHandler()
        {
            return handler;
        }

        /**
         * Get the client handler function of this widget.
         *
         * @return the callback.
         */
        public Consumer<WidgetSource> getClientHandler()
        {
            return clientHandler;
        }

        @Override
        public int hashCode()
        {
            return widgetId.hashCode();
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

            final Widget that = (Widget) o;

            return widgetId.equals(that.widgetId);
        }

        /**
         * Builder class for {@link Widget} instances.
         */
        public static class Builder
        {
            /**
             * The group of the widget.
             */
            private final ResourceLocation groupId;

            /**
             * The id of the widget.
             */
            private final ResourceLocation widgetId;

            /**
             * Metadata on this widget object.
             */
            private final Map<String, Object> metadata = new HashMap<>();

            /**
             * The getter for the component for the name.
             */
            private Function<Widget, Component> name;

            /**
             * The getter for the component for the description.
             */
            private Function<Widget, Component> description = widget -> null;

            /**
             * The icon.
             */
            private ItemStack icon = ItemStack.EMPTY;

            /**
             * The handler function for this widget.
             */
            private Consumer<WidgetSource> handler;

            /**
             * The client handler function for this widget.
             */
            @Nullable
            private Consumer<WidgetSource> clientHandler;

            /**
             * Default constructor.
             *
             * @param widgetId the id of the widget.
             */
            public Builder(final ResourceLocation groupId, final ResourceLocation widgetId)
            {
                this.groupId = groupId;
                this.widgetId = widgetId;
            }

            /**
             * Set the name of this widget.
             * Defaults to null.
             *
             * @param name the component.
             * @return builder for chaining.
             */
            public Widget.Builder setName(final Component name)
            {
                this.name = widget -> name;
                return this;
            }

            /**
             * Set the name of this widget.
             * Defaults to null.
             *
             * @param name the component.
             * @return builder for chaining.
             */
            public Widget.Builder setName(final Function<Widget, Component> name)
            {
                this.name = name;
                return this;
            }

            /**
             * Set the description of this widget.
             * Defaults to null.
             *
             * @param description the component.
             * @return builder for chaining.
             */
            public Widget.Builder setDescription(final Component description)
            {
                this.description = widget -> description;
                return this;
            }

            /**
             * Set the description of this widget.
             * Defaults to null.
             *
             * @param description the component.
             * @return builder for chaining.
             */
            public Widget.Builder setDescription(final Function<Widget, Component> description)
            {
                this.description = description;
                return this;
            }

            /**
             * Set the icon of this widget.
             * Defaults to {@link ItemStack#EMPTY}.
             *
             * @param icon the item stack.
             * @return builder for chaining.
             */
            public Widget.Builder setIcon(final ItemStack icon)
            {
                this.icon = icon;
                return this;
            }

            /**
             * Add metadata to this widget, values may be anything and will be passed along to the handler.
             *
             * @param key  the key for the metadata.
             * @param data the value for the metadata, may be anything.
             * @return builder for chaining.
             */
            public Widget.Builder addMetadata(final String key, final Object data)
            {
                this.metadata.putIfAbsent(key, data);
                return this;
            }

            /**
             * Set the handler of this widget.
             * Defaults to null.
             *
             * @param handler the callback.
             * @return builder for chaining.
             */
            public Widget.Builder setHandler(final Consumer<WidgetSource> handler)
            {
                this.handler = handler;
                return this;
            }

            /**
             * Set the client handler of this widget.
             * Defaults to null.
             *
             * @param clientHandler the callback.
             * @return builder for chaining.
             */
            public Widget.Builder setClientHandler(final Consumer<WidgetSource> clientHandler)
            {
                this.clientHandler = clientHandler;
                return this;
            }

            /**
             * Finalize construction of the {@link Widget} instance.
             *
             * @return the instance.
             * @throws IllegalStateException if any of the mandatory fields have not been filled in.
             */
            public Widget build()
            {
                if (this.name == null)
                {
                    throw new IllegalStateException("Name is mandatory.");
                }
                if (this.description == null)
                {
                    throw new IllegalStateException("Description is mandatory.");
                }
                if (this.icon == null)
                {
                    throw new IllegalStateException("Icon is mandatory.");
                }
                if (this.handler == null)
                {
                    throw new IllegalStateException("Handler function is mandatory.");
                }
                return new Widget(groupId, widgetId, name, description, icon, metadata, handler, clientHandler);
            }
        }
    }
}