package com.ldtteam.buildserveractions.registry;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.util.records.SizeI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * All the registry information for managing widgets.
 */
public class WidgetRegistries
{
    public static RegistryObject<WidgetLayout> layoutSurvivalInventory;
    public static RegistryObject<WidgetLayout> layoutCreativeInventory;

    public static RegistryObject<WidgetGroup> groupGamemodes;

    public static RegistryObject<Widget> gamemodeSurvivalWidget;
    public static RegistryObject<Widget> gamemodeCreativeWidget;
    public static RegistryObject<Widget> gamemodeSpectatorWidget;
    public static RegistryObject<Widget> gamemodeAdventureWidget;

    /**
     * Widget layout registry instance.
     */
    public static class WidgetLayout
    {
        /**
         * The class type of which screen the widget layout gets attached to.
         */
        private final Class<? extends Screen> screenClass;

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
        private final Function<Screen, SizeI> offsetFunc;

        /**
         * Default internal constructor.
         */
        public WidgetLayout(
          final Class<? extends Screen> screenClass, final int maxGroups, final int maxButtonsInGroup, final Alignment alignment, final Function<Screen, SizeI> offsetFunc)
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
        public Class<? extends Screen> getScreenClass()
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
        public Function<Screen, SizeI> getOffsetFunction()
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
            private final Class<? extends Screen> screenClass;

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
            private Function<Screen, SizeI> offsetFunc = screen -> new SizeI(0, 0);

            /**
             * Default constructor.
             *
             * @param screenClass the class type of the window which to attach to.
             */
            public Builder(final Class<? extends Screen> screenClass)
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
            public Builder setOffset(final Function<Screen, SizeI> offsetFunc)
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
        private final ResourceLocation groupId;

        /**
         * Default internal constructor.
         */
        public WidgetGroup(final ResourceLocation groupId)
        {
            this.groupId = groupId;
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
        private final Component name;

        /**
         * The component for the description.
         */
        @Nullable
        private final Component description;

        /**
         * The icon.
         */
        private final ItemStack icon;

        /**
         * The handler function for this widget.
         */
        private final Consumer<Player> handler;

        /**
         * The client handler function for this widget.
         */
        @Nullable
        private final Consumer<Player> clientHandler;

        /**
         * Default internal constructor.
         */
        private Widget(
          final ResourceLocation groupId,
          final ResourceLocation widgetId,
          final Component name,
          final @Nullable Component description,
          final ItemStack icon,
          final Consumer<Player> handler,
          final @Nullable Consumer<Player> clientHandler)
        {
            this.groupId = groupId;
            this.widgetId = widgetId;
            this.name = name;
            this.description = description;
            this.icon = icon;
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
        public Component getName()
        {
            return name;
        }

        /**
         * Get the description of this widget.
         *
         * @return the component.
         */
        public Component getDescription()
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
         * Get the handler function of this widget.
         *
         * @return the callback.
         */
        public Consumer<Player> getHandler()
        {
            return handler;
        }

        /**
         * Get the client handler function of this widget.
         *
         * @return the callback.
         */
        public Consumer<Player> getClientHandler()
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
             * The component for the name.
             */
            private Component name;

            /**
             * The component for the description.
             */
            @Nullable
            private Component description;

            /**
             * The icon.
             */
            private ItemStack icon = ItemStack.EMPTY;

            /**
             * The handler function for this widget.
             */
            private Consumer<Player> handler;

            /**
             * The client handler function for this widget.
             */
            @Nullable
            private Consumer<Player> clientHandler;

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
             * Set the handler of this widget.
             * Defaults to null.
             *
             * @param handler the callback.
             * @return builder for chaining.
             */
            public Widget.Builder setHandler(final Consumer<Player> handler)
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
            public Widget.Builder setClientHandler(final Consumer<Player> clientHandler)
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
                if (this.icon == null)
                {
                    throw new IllegalStateException("Icon is mandatory.");
                }
                if (this.handler == null)
                {
                    throw new IllegalStateException("Handler function is mandatory.");
                }
                return new Widget(groupId, widgetId, name, description, icon, handler, clientHandler);
            }
        }
    }
}