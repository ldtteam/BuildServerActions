package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.client.button.ItemButton;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.IntConsumer;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.FAVORITES_SLOT_CURRENT;

/**
 * Small popup that asks the player which of the 10 favorite slots to assign a widget to.
 * Opened from edit mode when clicking a widget in the main grid.
 */
public class FavoriteSlotPopup extends BOWindow
{
    private static final int SLOT_SIZE    = 20;
    private static final int SLOT_SPACING = 4;
    private static final int SLOT_OFFSET  = SLOT_SIZE + SLOT_SPACING;
    private static final int LABEL_HEIGHT = 8;
    private static final int LABEL_GAP    = 2;
    private static final int MARGIN       = 6;

    /**
     * Create a popup for assigning the given widget to a slot.
     *
     * @param widget   the widget being assigned.
     * @param onAssign called with the chosen 0-based slot index; popup closes automatically.
     */
    public FavoriteSlotPopup(final Widget widget, final IntConsumer onAssign)
    {
        super(modId("gui/favoriteslotpopup.xml"));

        this.windowPausesGame = false;
        this.lightbox = false;

        final View slotsView = findPaneOfTypeByID("slots", View.class);

        for (int i = 0; i < FavoritesManager.SLOT_COUNT; i++)
        {
            final int slotIndex = i;
            final ResourceLocation occupant = FavoritesManager.getInstance().getSlot(i);
            final Widget occupantWidget = occupant != null ? WidgetManager.getInstance().getWidgetById(occupant) : null;

            final Text label = new Text();
            label.setSize(SLOT_SIZE, LABEL_HEIGHT);
            label.setPosition(i * SLOT_OFFSET, 0);
            label.setText(List.of(Component.literal(String.valueOf(i + 1))));
            label.setTextAlignment(Alignment.TOP_MIDDLE);
            slotsView.addChild(label);

            final ItemButton btn = new ItemButton();
            btn.setSize(SLOT_SIZE, SLOT_SIZE);
            btn.setPosition(i * SLOT_OFFSET, LABEL_HEIGHT + LABEL_GAP);
            btn.setSpacing(2);
            if (occupantWidget != null)
            {
                btn.setItem(occupantWidget.getIcon());
            }
            btn.setHandler(b -> {
                onAssign.accept(slotIndex);
                close();
            });
            slotsView.addChild(btn);

            if (occupantWidget != null)
            {
                final Component hint = widget.getWidgetId().equals(occupant) ? Component.translatable(FAVORITES_SLOT_CURRENT) : null;
                TooltipHelper.buildWidgetTooltip(occupantWidget, btn, hint);
            }
        }

        final int totalWidth = FavoritesManager.SLOT_COUNT * SLOT_OFFSET - SLOT_SPACING;
        final int totalHeight = LABEL_HEIGHT + LABEL_GAP + SLOT_SIZE;

        slotsView.setSize(totalWidth, totalHeight);

        final int windowWidth = totalWidth + MARGIN * 2;
        final int windowHeight = totalHeight + MARGIN * 2;
        setSize(windowWidth, windowHeight);
        findPaneOfTypeByID("background", Image.class).setSize(windowWidth, windowHeight);
    }
}
