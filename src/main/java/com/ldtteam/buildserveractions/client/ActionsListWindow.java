package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.AbstractTextBuilder;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.ScrollingListContainer.RowSizeModifier;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.LayoutManager.WidgetLayout;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.client.button.ClockItemButton;
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

/**
 * Root GUI managing the actions list.
 */
public class ActionsListWindow extends BOWindow
{
    /**
     * All constant values for sizing requirements.
     */
    private static final int ROOT_MARGIN       = 6;
    private static final int BANNER_TOP_MARGIN = 10;
    private static final int WIDGET_SIZE       = 20;
    private static final int WIDGET_SPACING    = 4;
    private static final int WIDGET_OFFSET     = WIDGET_SIZE + WIDGET_SPACING;
    private static final int SCROLLBAR_WIDTH   = 8;

    /**
     * The screen this window will be attached to.
     */
    private final AbstractContainerScreen<?> attachedToScreen;

    /**
     * The layout information for this screen.
     */
    private final WidgetLayout layout;

    /**
     * The amount of widgets allowed in a single column.
     */
    private final int widgetsInColumn;

    /**
     * Default constructor.
     *
     * @param attachedToScreen the screen this window will be attached to.
     * @param layout           the layout information for this screen.
     */
    public ActionsListWindow(final AbstractContainerScreen<?> attachedToScreen, final WidgetLayout layout)
    {
        super(modId("gui/actionslist.xml"));
        this.attachedToScreen = attachedToScreen;
        this.layout = layout;

        this.windowPausesGame = attachedToScreen.isPauseScreen();
        this.lightbox = false;

        final int totalGroupCount = WidgetManager.getInstance().getWidgetGroupCount();
        this.widgetsInColumn = WidgetManager.getInstance().getMaxWidgetCountInGroup();

        final ImageRepeatable background = findPaneOfTypeByID("background", ImageRepeatable.class);
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        final int columnsToRender = Math.min(totalGroupCount, layout.getMaxGroups());
        final int maxContainerWidth = columnsToRender * WIDGET_OFFSET - WIDGET_SPACING + SCROLLBAR_WIDTH;
        final int maxContainerHeight = Math.min(widgetsInColumn, layout.getMaxButtonsInGroup()) * WIDGET_OFFSET - WIDGET_SPACING;

        final int pageCount = (int) Math.ceil(totalGroupCount / (double) layout.getMaxGroups());

        for (int pageId = 0; pageId < pageCount; pageId++)
        {
            final int currentPageOffset = pageId * layout.getMaxGroups();

            final View pageRoot = new View();
            final ScrollingList list = (ScrollingList) Loader.createFromXMLFile2(modId("gui/actionspage.xml"), pageRoot);
            pageRoot.setID("page" + pageId);
            pageRoot.setSize(maxContainerWidth, maxContainerHeight);
            pages.addChild(pageRoot);

            list.setSize(maxContainerWidth, maxContainerHeight);
            list.setMaxHeight(maxContainerHeight);
            list.setDataProvider(new ScrollingList.DataProvider()
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
                            button.setHandler(btn -> Network.CHANNEL.sendToServer(new WidgetTriggerMessage(widget)));

                            ((View) rowPane).addChild(button);
                        }

                        final AbstractTextBuilder.TooltipBuilder tooltipBuilder = new AbstractTextBuilder.AutomaticTooltipBuilder().append(widget.getName().apply(widget));

                        final Component description = widget.getDescription().apply(widget);
                        if (description != null && !description.equals(Component.empty()))
                        {
                            tooltipBuilder.newLine().appendNL(description.copy().withStyle(ChatFormatting.GRAY));
                        }
                        tooltipBuilder.hoverPane(button).build();
                    }
                }
            });
        }

        setSize(maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);
        background.setSize(maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);
        pages.setSize(maxContainerWidth, maxContainerHeight);

        screen.init(attachedToScreen.getMinecraft(), maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);

        if (pageCount > 1)
        {
            final int navHeight = BANNER_TOP_MARGIN - 2;
            final int navLabelWidth = maxContainerWidth - navHeight * 2;

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

        setPosition(getPositionX(attachedToScreen, layout), getPositionY(attachedToScreen, layout));
        onPageUpdate(0, pageCount);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        setPosition(getPositionX(attachedToScreen, layout), getPositionY(attachedToScreen, layout));
    }

    /**
     * Switch the page to a different page.
     *
     * @param pageId    the page number (0-based).
     * @param pageCount the total number of pages.
     */
    private void onPageUpdate(final int pageId, final int pageCount)
    {
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);
        pages.setView("page" + pageId);

        findPaneOfTypeByID("page_label", Text.class).setText(List.of(Component.literal((pageId + 1) + "/" + pageCount)));
        findPaneOfTypeByID("page_prev", ButtonImage.class).setHandler(btn -> onPageUpdate((pageId - 1 + pageCount) % pageCount, pageCount));
        findPaneOfTypeByID("page_next", ButtonImage.class).setHandler(btn -> onPageUpdate((pageId + 1) % pageCount, pageCount));
    }

    /**
     * Get the X position of where to put this screen at, relative to the attached screen.
     *
     * @param attachedToScreen the screen this window will be attached to.
     * @param layout           the layout information for this screen.
     * @return the X position.
     */
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

    /**
     * Get the Y position of where to put this screen at, relative to the attached screen.
     *
     * @param attachedToScreen the screen this window will be attached to.
     * @param layout           the layout information for this screen.
     * @return the Y position.
     */
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

    /**
     * Generate the correct {@link ItemButton} class for the provided item stack.
     *
     * @param itemStack the item stack.
     * @return the {@link ItemButton} instance.
     */
    private ItemButton getItemButtonInstance(final ItemStack itemStack)
    {
        if (itemStack.is(Items.CLOCK))
        {
            return new ClockItemButton();
        }
        else
        {
            return new ItemButton();
        }
    }
}
