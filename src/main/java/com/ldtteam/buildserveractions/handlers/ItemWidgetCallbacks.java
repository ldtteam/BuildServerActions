package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_ITEM_DESC;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_ITEM_NAME;
import static com.ldtteam.buildserveractions.util.WidgetLogger.broadcastMessage;

/**
 * Callbacks for the item widget defined by the mod.
 */
public class ItemWidgetCallbacks
{
    /**
     * Get the name for the item.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component name(final Widget widget)
    {
        return Component.translatable(WIDGET_ITEM_NAME, getItemName(widget));
    }

    /**
     * Get the description for the item.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component description(final Widget widget)
    {
        return Component.translatable(WIDGET_ITEM_DESC, getItemName(widget));
    }

    /**
     * Action callback for switching game-modes.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        final ItemStack itemStack = resolveItemStack(source.widget());
        if (source.player().addItem(itemStack))
        {
            final MutableComponent message = Component.translatable("commands.give.success.single", 1, getItemName(source.widget()), source.player().getDisplayName());
            broadcastMessage(source, message);
        }
    }

    /**
     * Resolve the item stack and ensure the count received is one.
     *
     * @param widget the widget.
     * @return the new item stack.
     */
    private static ItemStack resolveItemStack(final Widget widget)
    {
        final ItemStack copy = widget.getIcon().copy();
        copy.setCount(1);
        return copy;
    }

    /**
     * Resolve the item name for the current item.
     *
     * @param widget the input widget.
     * @return the component name.
     */
    private static Component getItemName(final Widget widget)
    {
        return widget.getIcon().getItem().getDescription();
    }
}
