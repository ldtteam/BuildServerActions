package com.ldtteam.buildserveractions.handlers;

import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_OPEN_WINDOW_DESC;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_OPEN_WINDOW_NAME;

/**
 * Callbacks for the open window widget defined by the mod.
 */
public final class OpenWindowWidgetCallback
{
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private OpenWindowWidgetCallback()
    {
    }

    /**
     * Widget metadata keys.
     */
    public static final String WIDGET_WINDOW_MENU_PROVIDER = "menuProvider";

    /**
     * Get the name for the window.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component name(final Widget widget)
    {
        return Component.translatable(WIDGET_OPEN_WINDOW_NAME, widget.getMetadataValue(WIDGET_WINDOW_MENU_PROVIDER, MenuProvider.class).getDisplayName());
    }

    /**
     * Get the description for the window.
     *
     * @param widget the widget class.
     * @return the component.
     */
    public static Component description(final Widget widget)
    {
        return Component.translatable(WIDGET_OPEN_WINDOW_DESC, widget.getMetadataValue(WIDGET_WINDOW_MENU_PROVIDER, MenuProvider.class).getDisplayName());
    }

    /**
     * Action callback for opening a window.
     *
     * @param source the widget source.
     */
    public static void handler(final WidgetSource source)
    {
        handlerInternal(source, source.widget().getMetadataValue(WIDGET_WINDOW_MENU_PROVIDER, MenuProvider.class));
    }

    /**
     * Internal action callback for opening a window.
     *
     * @param source       the widget source.
     * @param menuProvider the menu provider to show.
     */
    private static void handlerInternal(final WidgetSource source, final MenuProvider menuProvider)
    {
        source.player().openMenu(menuProvider);
    }
}
