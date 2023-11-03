package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.util.records.SizeI;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.buildserveractions.WidgetManager;
import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import com.ldtteam.buildserveractions.util.Constants;
import com.ldtteam.buildserveractions.widgets.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ActionsListWindow extends BOWindow
{
    private static final int ROOT_MARGIN       = 6;
    private static final int BANNER_TOP_MARGIN = 10;
    private static final int WIDGET_SIZE       = 20;
    private static final int WIDGET_SPACING    = 4;
    private static final int WIDGET_OFFSET     = WIDGET_SIZE + WIDGET_SPACING;
    private static final int SCROLLBAR_WIDTH   = 8;

    private final int widgetsInRow;
    private final int widgetsInColumn;

    public ActionsListWindow(final AbstractContainerScreen<?> attachedToScreen, final ActionRenderLayout<?> layout)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/actionslist.xml"));

        this.windowPausesGame = attachedToScreen.isPauseScreen();
        this.lightbox = false;

        this.widgetsInRow = Math.min(WidgetManager.getInstance().getWidgets().size(), layout.getGroupsPerRow());
        this.widgetsInColumn = WidgetManager.getInstance().getWidgets().values().stream().mapToInt(List::size).max().orElse(0);

        final ImageRepeatable background = findPaneOfTypeByID("background", ImageRepeatable.class);
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        final int maxContainerWidth = widgetsInRow * WIDGET_OFFSET - WIDGET_SPACING + SCROLLBAR_WIDTH;
        final int maxContainerHeight = Math.min(widgetsInColumn, layout.getMaxButtonsInGroupBeforeScroll()) * WIDGET_OFFSET - WIDGET_SPACING;

        final int pageCount = (int) Math.ceil(WidgetManager.getInstance().getWidgets().size() / (double) layout.getGroupsPerRow());

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
                        final Widget widget = WidgetManager.getInstance().getWidget(currentPageOffset + groupOffset, index);
                        if (widget == null)
                        {
                            continue;
                        }

                        ItemButton button = rowPane.findPaneOfTypeByID("button" + index, ItemButton.class);
                        if (button == null)
                        {
                            button = new ItemButton();
                            button.setID("button" + index);
                            button.setSize(WIDGET_SIZE, WIDGET_SIZE);
                            button.setSpacing(2);

                            ((View) rowPane).addChild(button);
                        }

                        button.setPosition(groupOffset * WIDGET_OFFSET, 0);
                        button.setItem(widget.getIcon());
                    }
                }
            });
        }

        setSize(maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);
        setPosition(getPositionX(attachedToScreen, layout), getPositionY(attachedToScreen, layout));
        background.setSize(maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);
        pages.setSize(maxContainerWidth, maxContainerHeight);

        screen.init(attachedToScreen.getMinecraft(), maxContainerWidth + (ROOT_MARGIN * 2), maxContainerHeight + (ROOT_MARGIN * 2) + BANNER_TOP_MARGIN);

        onPageUpdate(0);
    }

    private void onPageUpdate(final int pageId)
    {
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);
        pages.setView("page" + pageId);
    }

    private int getPositionX(final AbstractContainerScreen<?> attachedToScreen, final ActionRenderLayout<?> layout)
    {
        if (layout.getWindowAlignment().isHorizontalCentered())
        {
            return layout.getWidthOffset(attachedToScreen);
        }
        else if (layout.getWindowAlignment().isRightAligned())
        {
            return (attachedToScreen.getYSize() / 2) + (getWidth() / 2) + layout.getWidthOffset(attachedToScreen);
        }
        else
        {
            return -(attachedToScreen.getYSize() / 2) - (getWidth() / 2) + layout.getWidthOffset(attachedToScreen);
        }
    }

    private int getPositionY(final AbstractContainerScreen<?> attachedToScreen, final ActionRenderLayout<?> layout)
    {
        if (layout.getWindowAlignment().isVerticalCentered())
        {
            return layout.getHeightOffset(attachedToScreen);
        }
        else if (layout.getWindowAlignment().isBottomAligned())
        {
            return (attachedToScreen.getYSize() / 2) + (getHeight() / 2) + layout.getHeightOffset(attachedToScreen);
        }
        else
        {
            return -(attachedToScreen.getYSize() / 2) - (getHeight() / 2) + layout.getHeightOffset(attachedToScreen);
        }
    }
}
