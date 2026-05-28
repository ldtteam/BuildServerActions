package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.AbstractTextBuilder;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.event.ModClientEventHandler;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.FAVORITES_KEYBIND;

/**
 * Utility class for building and attaching tooltips to widget panes.
 */
public class TooltipHelper
{
    private TooltipHelper() {}

    /**
     * Builds and attaches a tooltip for the given widget to the given pane.
     *
     * @param widget the widget whose name and description are used.
     * @param pane   the pane to attach the tooltip to.
     */
    public static void buildWidgetTooltip(final Widget widget, final Pane pane)
    {
        buildWidgetTooltip(widget, pane, null);
    }

    /**
     * Builds and attaches a tooltip for the given widget to the given pane, with an optional hint line.
     *
     * @param widget the widget whose name and description are used.
     * @param pane   the pane to attach the tooltip to.
     * @param hint   an optional additional hint shown at the bottom of the tooltip.
     */
    public static void buildWidgetTooltip(final Widget widget, final Pane pane, @Nullable final Component hint)
    {
        final AbstractTextBuilder.TooltipBuilder tip = new AbstractTextBuilder.AutomaticTooltipBuilder().append(widget.getName().apply(widget));
        final Component desc = widget.getDescription().apply(widget);
        if (desc != null && !desc.equals(Component.empty()))
        {
            tip.newLine().appendNL(desc.copy().withStyle(ChatFormatting.GRAY));
        }
        final int slot = FavoritesManager.getInstance().getSlotFor(widget.getWidgetId());
        if (slot >= 0)
        {
            tip.emptyLines(1)
                .append(Component.translatable(FAVORITES_KEYBIND).copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" "))
                .append(ModClientEventHandler.FAVORITE_SLOTS[slot].getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD));
        }
        if (hint != null)
        {
            tip.emptyLines(1).append(hint.copy().withStyle(ChatFormatting.YELLOW));
        }
        tip.hoverPane(pane).build();
    }
}
