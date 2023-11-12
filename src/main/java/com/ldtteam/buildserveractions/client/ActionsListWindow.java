package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.AbstractTextBuilder;
import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.util.records.SizeI;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.WidgetSource;
import com.ldtteam.buildserveractions.client.button.ClockItemButton;
import com.ldtteam.buildserveractions.client.button.ItemButton;
import com.ldtteam.buildserveractions.network.messages.client.WidgetTriggerMessage;
import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.util.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    private final WidgetRegistries.WidgetLayout layout;

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
    public ActionsListWindow(final AbstractContainerScreen<?> attachedToScreen, final WidgetRegistries.WidgetLayout layout)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/actionslist.xml"));
        this.attachedToScreen = attachedToScreen;
        this.layout = layout;

        this.windowPausesGame = attachedToScreen.isPauseScreen();
        this.lightbox = false;

        int widgetsInRow = Math.min(WidgetManager.getInstance().getWidgetGroupCount(), layout.getMaxGroups());
        this.widgetsInColumn = WidgetManager.getInstance().getMaxWidgetCountInGroup();

        final ImageRepeatable background = findPaneOfTypeByID("background", ImageRepeatable.class);
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        final int maxContainerWidth = widgetsInRow * WIDGET_OFFSET - WIDGET_SPACING + SCROLLBAR_WIDTH;
        final int maxContainerHeight = Math.min(widgetsInColumn, layout.getMaxButtonsInGroup()) * WIDGET_OFFSET - WIDGET_SPACING;

        final int pageCount = (int) Math.ceil(widgetsInRow / (double) layout.getMaxGroups());

        for (int pageId = 0; pageId < pageCount; pageId++)
        {
            final int currentPageOffset = pageId * this.widgetsInColumn;

            final View pageRoot = new View();
            final ScrollingList list = (ScrollingList) Loader.createFromXMLFile2(new ResourceLocation(Constants.MOD_ID, "gui/actionspage.xml"), pageRoot);
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
                public SizeI getElementSize(final int index, final Pane rowPane)
                {
                    return new SizeI(maxContainerWidth, index + 1 == widgetsInColumn ? WIDGET_SIZE : WIDGET_OFFSET);
                }

                @Override
                public void updateElement(final int index, final Pane rowPane)
                {
                    for (int groupOffset = 0; groupOffset < widgetsInColumn; groupOffset++)
                    {
                        final WidgetRegistries.Widget widget = WidgetManager.getInstance().getWidget(currentPageOffset + groupOffset, index);
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
                            button.setHandler(btn -> {
                                Network.getNetwork().sendToServer(new WidgetTriggerMessage(widget));
                                if (widget.getClientHandler() != null)
                                {
                                    final WidgetSource source = new WidgetSource(widget, Minecraft.getInstance().player);
                                    widget.getClientHandler().accept(source);
                                }
                            });

                            ((View) rowPane).addChild(button);
                        }

                        final AbstractTextBuilder.TooltipBuilder tooltipBuilder = new AbstractTextBuilder.AutomaticTooltipBuilder()
                                                                                    .append(widget.getName().apply(widget));

                        final Component description = widget.getDescription().apply(widget);
                        if (description != null)
                        {
                            tooltipBuilder.appendNL(description);
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

        onPageUpdate(0);
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
     * @param pageId the page number (0-based).
     */
    private void onPageUpdate(final int pageId)
    {
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);
        pages.setView("page" + pageId);
    }

    /**
     * Get the X position of where to put this screen at, relative to the attached screen.
     *
     * @param attachedToScreen the screen this window will be attached to.
     * @param layout           the layout information for this screen.
     * @return the X position.
     */
    private int getPositionX(final AbstractContainerScreen<?> attachedToScreen, final WidgetRegistries.WidgetLayout layout)
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
    private int getPositionY(final AbstractContainerScreen<?> attachedToScreen, final WidgetRegistries.WidgetLayout layout)
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
