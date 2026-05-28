package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.*;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList.DataProvider;
import com.ldtteam.blockui.views.ScrollingListContainer.RowSizeModifier;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.FavoritesManager;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.client.button.ClockItemButton;
import com.ldtteam.buildserveractions.client.button.FavoritePlaceholderPane;
import com.ldtteam.buildserveractions.client.button.ItemButton;
import com.ldtteam.buildserveractions.network.Network;
import com.ldtteam.buildserveractions.network.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;

/**
 * Root GUI managing the action list.
 */
public class ActionsListWindow extends BOWindow
{
    private static final int ROOT_MARGIN       = 6;
    private static final int BANNER_TOP_MARGIN = 10;
    private static final int WIDGET_SIZE       = 20;
    private static final int WIDGET_SPACING    = 4;
    private static final int WIDGET_OFFSET     = WIDGET_SIZE + WIDGET_SPACING;
    private static final int SCROLLBAR_WIDTH   = 8;
    private static final int SEPARATOR_HEIGHT  = 9;

    private final AbstractContainerScreen<?> attachedToScreen;
    private final WidgetLayout               layout;
    private final int                        widgetsInColumn;
    private final int                        columnsToRender;

    /**
     * Whether the user is currently in favorite edit mode.
     */
    private boolean editMode = false;

    /**
     * The favorite scrolling list, rebuilt when favorites change.
     */
    private final SnappingScrollingList favoritesList;

    /**
     * Cached window width for rebuilding.
     */
    private final int windowWidth;

    public ActionsListWindow(final AbstractContainerScreen<?> attachedToScreen, final WidgetLayout layout)
    {
        super(modId("gui/actionslist.xml"));
        this.attachedToScreen = attachedToScreen;
        this.layout = layout;

        this.windowPausesGame = attachedToScreen.isPauseScreen();
        this.lightbox = false;

        final int totalGroupCount = WidgetManager.getInstance().getWidgetGroupCount();
        this.widgetsInColumn = WidgetManager.getInstance().getMaxWidgetCountInGroup();
        this.columnsToRender = Math.min(totalGroupCount, layout.getMaxGroups());

        final int mainContainerWidth = columnsToRender * WIDGET_OFFSET - WIDGET_SPACING + SCROLLBAR_WIDTH;
        windowWidth = mainContainerWidth + ROOT_MARGIN * 2;

        final int mainContainerHeight = Math.min(widgetsInColumn, layout.getMaxButtonsInGroup()) * WIDGET_OFFSET - WIDGET_SPACING;
        final int pageCount = (int) Math.ceil(totalGroupCount / (double) layout.getMaxGroups());

        final View favoritesContainer = findPaneOfTypeByID("favorites_container", View.class);
        favoritesContainer.setSize(mainContainerWidth, WIDGET_SIZE);

        favoritesList = findPaneOfTypeByID("favorites_list", SnappingScrollingList.class);
        favoritesList.setSize(mainContainerWidth, WIDGET_SIZE);
        favoritesList.setMaxHeight(WIDGET_SIZE);
        favoritesList.setRowHeight(WIDGET_OFFSET);
        attachFavoritesDataProvider(favoritesList);

        final ButtonVanilla toggleBtn = findPaneOfTypeByID("favorites_toggle", ButtonVanilla.class);
        toggleBtn.setPosition(ROOT_MARGIN + (columnsToRender - 1) * WIDGET_OFFSET, ROOT_MARGIN + BANNER_TOP_MARGIN);
        toggleBtn.setText(Component.literal("★"));
        toggleBtn.setHandler(btn -> {
            editMode = !editMode;
            toggleBtn.setText(Component.literal(editMode ? "✓" : "★"));
            new AbstractTextBuilder.AutomaticTooltipBuilder().append(Component.translatable(editMode ? FAVORITES_TOGGLE_EXIT : FAVORITES_TOGGLE_ENTER))
                .hoverPane(toggleBtn)
                .build();
            favoritesList.refreshElementPanes();
        });
        new AbstractTextBuilder.AutomaticTooltipBuilder().append(Component.translatable(FAVORITES_TOGGLE_ENTER)).hoverPane(toggleBtn).build();

        findPaneOfTypeByID("separator", Box.class).setSize(windowWidth - ROOT_MARGIN * 2, 1);

        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        for (int pageId = 0; pageId < pageCount; pageId++)
        {
            final int currentPageOffset = pageId * layout.getMaxGroups();
            final View pageRoot = new View();
            final SnappingScrollingList list = (SnappingScrollingList) Loader.createFromXMLFile2(modId("gui/widgetrow.xml"), pageRoot);
            pageRoot.setID("page" + pageId);
            pageRoot.setSize(mainContainerWidth, mainContainerHeight);
            pages.addChild(pageRoot);

            list.setSize(mainContainerWidth, mainContainerHeight);
            list.setMaxHeight(mainContainerHeight);
            list.setRowHeight(WIDGET_OFFSET);
            list.setDataProvider(new DataProvider()
            {
                @Override
                public int getElementCount()
                {
                    return widgetsInColumn;
                }

                @Override
                public void modifyRowSize(final int index, final RowSizeModifier modifier)
                {
                    modifier.setHeight(index + 1 == widgetsInColumn ? WIDGET_SIZE : WIDGET_OFFSET);
                }

                @Override
                public void updateElement(final int index, final Pane rowPane)
                {
                    for (int groupOffset = 0; groupOffset < layout.getMaxGroups(); groupOffset++)
                    {
                        final Widget widget = WidgetManager.getInstance().getWidget(currentPageOffset + groupOffset, index);
                        if (widget == null)
                        {
                            continue;
                        }

                        ItemButton button = rowPane.findPaneOfTypeByID("button" + groupOffset, ItemButton.class);
                        if (button == null)
                        {
                            button = getItemButtonInstance(widget.getIcon());
                            button.setID("button" + groupOffset);
                            button.setSize(WIDGET_SIZE, WIDGET_SIZE);
                            button.setPosition(groupOffset * WIDGET_OFFSET, 0);
                            button.setSpacing(2);
                            button.setItem(widget.getIcon());
                            ((View) rowPane).addChild(button);
                        }

                        final ItemButton finalButton = button;

                        if (editMode)
                        {
                            finalButton.setHandler(btn -> onEditModeWidgetClick(widget));
                        }
                        else
                        {
                            finalButton.setHandler(btn -> Network.CHANNEL.sendToServer(new WidgetTriggerMessage(widget)));
                        }

                        buildWidgetTooltip(widget, button);
                    }
                }
            });
        }

        applyWindowSize(mainContainerWidth, mainContainerHeight, pageCount);

        onPageUpdate(0, pageCount);
        setPosition(getPositionX(attachedToScreen, layout), getPositionY(attachedToScreen, layout));
    }

    private int getFavoritesRowCount()
    {
        final int filled = FavoritesManager.getInstance().getFilledSlotCount();
        return filled == 0 ? 1 : (int) Math.ceil(filled / (double) (columnsToRender - 1));
    }

    private void attachFavoritesDataProvider(final SnappingScrollingList list)
    {
        list.setDataProvider(new DataProvider()
        {
            @Override
            public int getElementCount()
            {
                return getFavoritesRowCount();
            }

            @Override
            public void modifyRowSize(final int index, final RowSizeModifier modifier)
            {
                modifier.setHeight(index + 1 == getFavoritesRowCount() ? WIDGET_SIZE : WIDGET_OFFSET);
            }

            @Override
            public void updateElement(final int index, final Pane rowPane)
            {
                final int favCols = columnsToRender - 1;

                final java.util.List<Integer> filledSlots = new java.util.ArrayList<>();
                for (int s = 0; s < FavoritesManager.SLOT_COUNT; s++)
                {
                    if (FavoritesManager.getInstance().getSlot(s) != null)
                    {
                        filledSlots.add(s);
                    }
                }

                for (int col = 0; col < favCols; col++)
                {
                    final int filledIndex = index * favCols + col;

                    final Integer slotIndex = filledIndex < filledSlots.size() ? filledSlots.get(filledIndex) : null;
                    final Widget widget = slotIndex != null ? WidgetManager.getInstance().getWidgetById(FavoritesManager.getInstance().getSlot(slotIndex)) : null;

                    if (widget != null)
                    {
                        final Pane placeholder = rowPane.findPaneOfTypeByID("favp" + col, FavoritePlaceholderPane.class);
                        if (placeholder != null)
                        {
                            placeholder.setVisible(false);
                        }

                        ItemButton btn = rowPane.findPaneOfTypeByID("fav" + col, ItemButton.class);
                        if (btn == null)
                        {
                            btn = getItemButtonInstance(widget.getIcon());
                            btn.setID("fav" + col);
                            btn.setSize(WIDGET_SIZE, WIDGET_SIZE);
                            btn.setPosition(col * WIDGET_OFFSET, 0);
                            btn.setSpacing(2);
                            ((View) rowPane).addChild(btn);
                        }
                        btn.setVisible(true);
                        btn.setItem(widget.getIcon());

                        final int finalSlot = slotIndex;
                        if (editMode)
                        {
                            btn.setHandler(b -> {
                                FavoritesManager.getInstance().clearSlot(finalSlot);
                                refreshFavorites();
                            });
                            buildWidgetTooltipWithHint(widget, btn, Component.translatable(FAVORITES_HINT_REMOVE));
                        }
                        else
                        {
                            btn.setHandler(b -> Network.CHANNEL.sendToServer(new WidgetTriggerMessage(widget)));
                            buildWidgetTooltip(widget, btn);
                        }
                    }
                    else
                    {
                        final ItemButton existing = rowPane.findPaneOfTypeByID("fav" + col, ItemButton.class);
                        if (existing != null)
                        {
                            existing.setVisible(false);
                        }

                        FavoritePlaceholderPane placeholder = rowPane.findPaneOfTypeByID("favp" + col, FavoritePlaceholderPane.class);
                        if (placeholder == null)
                        {
                            placeholder = new FavoritePlaceholderPane();
                            placeholder.setID("favp" + col);
                            placeholder.setSize(WIDGET_SIZE, WIDGET_SIZE);
                            placeholder.setPosition(col * WIDGET_OFFSET, 0);
                            ((View) rowPane).addChild(placeholder);
                        }
                        placeholder.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Rebuild the favorites list in place after a slot change.
     */
    private void refreshFavorites()
    {
        favoritesList.refreshElementPanes();
    }

    /**
     * Called in edit mode when the player clicks a widget in the main grid.
     * If already a favorite, removes it; otherwise opens the slot popup.
     */
    private void onEditModeWidgetClick(final Widget widget)
    {
        final FavoriteSlotPopup popup = new FavoriteSlotPopup(widget, slot -> {
            FavoritesManager.getInstance().setSlot(slot, widget.getWidgetId());
            refreshFavorites();
        });
        popup.setPosition(getX(), getY() + BANNER_TOP_MARGIN);
        popup.openAsLayer();
    }

    private void buildWidgetTooltip(final Widget widget, final Pane pane)
    {
        TooltipHelper.buildWidgetTooltip(widget, pane);
    }

    private void buildWidgetTooltipWithHint(final Widget widget, final Pane pane, final Component hint)
    {
        TooltipHelper.buildWidgetTooltip(widget, pane, hint);
    }

    private void applyWindowSize(final int mainContainerWidth, final int mainContainerHeight, final int pageCount)
    {
        final int totalHeight = ROOT_MARGIN * 2 + BANNER_TOP_MARGIN + WIDGET_SIZE + SEPARATOR_HEIGHT + mainContainerHeight;

        final ImageRepeatable background = findPaneOfTypeByID("background", ImageRepeatable.class);
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        setSize(windowWidth, totalHeight);
        background.setSize(windowWidth, totalHeight);
        pages.setSize(mainContainerWidth, mainContainerHeight);

        screen.init(attachedToScreen.getMinecraft(), windowWidth, totalHeight);

        if (pageCount > 1)
        {
            final int navHeight = BANNER_TOP_MARGIN - 2;
            final int navLabelWidth = mainContainerWidth - navHeight * 2;

            final ButtonImage prevBtn = findPaneOfTypeByID("page_prev", ButtonImage.class);
            prevBtn.setSize(navHeight, navHeight);
            prevBtn.setPosition(ROOT_MARGIN, 4);
            prevBtn.setVisible(true);

            final Text pageLabel = findPaneOfTypeByID("page_label", Text.class);
            pageLabel.setSize(navLabelWidth, navHeight);
            pageLabel.setPosition(ROOT_MARGIN + navHeight, 4);
            pageLabel.setVisible(true);

            final ButtonImage nextBtn = findPaneOfTypeByID("page_next", ButtonImage.class);
            nextBtn.setSize(navHeight, navHeight);
            nextBtn.setPosition(ROOT_MARGIN + navHeight + navLabelWidth, 4);
            nextBtn.setVisible(true);
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        setPosition(getPositionX(attachedToScreen, layout), getPositionY(attachedToScreen, layout));
    }

    private void onPageUpdate(final int pageId, final int pageCount)
    {
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);
        pages.setView("page" + pageId);

        findPaneOfTypeByID("page_label", Text.class).setText(List.of(Component.literal((pageId + 1) + "/" + pageCount)));
        findPaneOfTypeByID("page_prev", ButtonImage.class).setHandler(btn -> onPageUpdate((pageId - 1 + pageCount) % pageCount, pageCount));
        findPaneOfTypeByID("page_next", ButtonImage.class).setHandler(btn -> onPageUpdate((pageId + 1) % pageCount, pageCount));
    }

    private int getPositionX(final AbstractContainerScreen<?> attachedToScreen, final WidgetLayout layout)
    {
        if (layout.getAlignment().isHorizontalCentered())
        {
            return layout.getOffsetFunction().apply(attachedToScreen).width();
        }
        else if (layout.getAlignment().isRightAligned())
        {
            return (attachedToScreen.getXSize() / 2) + (getWidth() / 2) + layout.getOffsetFunction().apply(attachedToScreen).width();
        }
        else
        {
            return -(attachedToScreen.getXSize() / 2) - (getWidth() / 2) + layout.getOffsetFunction().apply(attachedToScreen).width();
        }
    }

    private int getPositionY(final AbstractContainerScreen<?> attachedToScreen, final WidgetLayout layout)
    {
        if (layout.getAlignment().isVerticalCentered())
        {
            return layout.getOffsetFunction().apply(attachedToScreen).height();
        }
        else if (layout.getAlignment().isBottomAligned())
        {
            return (attachedToScreen.getYSize() / 2) + (getHeight() / 2) + layout.getOffsetFunction().apply(attachedToScreen).height();
        }
        else
        {
            return -(attachedToScreen.getYSize() / 2) - (getHeight() / 2) + layout.getOffsetFunction().apply(attachedToScreen).height();
        }
    }

    private ItemButton getItemButtonInstance(final ItemStack itemStack)
    {
        if (itemStack.is(Items.CLOCK))
        {
            return new ClockItemButton();
        }
        return new ItemButton();
    }
}
