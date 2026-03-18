package com.ldtteam.buildserveractions.widget;

import com.ldtteam.buildserveractions.WidgetSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Widget registry instance.
 */
public class Widget
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
     * Default internal constructor.
     */
    private Widget(
        final ResourceLocation groupId,
        final ResourceLocation widgetId,
        final Function<Widget, Component> name,
        final Function<Widget, Component> description,
        final ItemStack icon,
        final Map<String, Object> metadata,
        final Consumer<WidgetSource> handler)
    {
        this.groupId = groupId;
        this.widgetId = widgetId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.metadata = metadata;
        this.handler = handler;
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
     * @param key  the string key used to store the value.
     * @param type the expected type of the value.
     * @param <T>  the type of the value to retrieve.
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
         * Default constructor.
         *
         * @param groupId  the id of the group.
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
        public Builder setName(final Component name)
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
        public Builder setName(final Function<Widget, Component> name)
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
        public Builder setDescription(final Component description)
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
        public Builder setDescription(final Function<Widget, Component> description)
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
        public Builder setIcon(final ItemStack icon)
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
        public Builder addMetadata(final String key, final Object data)
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
        public Builder setHandler(final Consumer<WidgetSource> handler)
        {
            this.handler = handler;
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
            return new Widget(groupId, widgetId, name, description, icon, metadata, handler);
        }
    }
}
