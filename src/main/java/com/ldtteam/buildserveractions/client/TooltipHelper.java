package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.AbstractTextBuilder;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.FAVORITES_KEYBIND;

public class TooltipHelper
{
    private TooltipHelper() {}

    public static void buildWidgetTooltip(final Widget widget, final Pane pane)
    {
        buildWidgetTooltip(widget, pane, null);
    }

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
                .append(ClientEventHandler.FAVORITE_SLOTS[slot].getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD));
        }
        if (hint != null)
        {
            tip.emptyLines(1).append(hint.copy().withStyle(ChatFormatting.YELLOW));
        }
        tip.hoverPane(pane).build();
    }
}