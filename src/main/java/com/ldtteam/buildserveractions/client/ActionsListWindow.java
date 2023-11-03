package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.ImageRepeatable;
import com.ldtteam.blockui.controls.ItemIcon;
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
    private static final int WIDGET_SIZE    = 20;
    private static final int WIDGET_SPACING = 4;
    private static final int WIDGET_OFFSET  = WIDGET_SIZE + WIDGET_SPACING;

    private final int widgetsInRow;
    private final int widgetsInColumn;

    public ActionsListWindow(final AbstractContainerScreen<?> attachedToScreen, final ActionRenderLayout<?> layout)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/actionslist.xml"));

        this.windowPausesGame = attachedToScreen.isPauseScreen();
        this.lightbox = false;

        this.widgetsInRow = Math.min(WidgetManager.getInstance().getWidgets().size(), layout.getGroupsPerRow());
        this.widgetsInColumn = WidgetManager.getInstance().getWidgets().values().stream().mapToInt(List::size).max().orElse(0);

        setAlignment(layout.getWindowAlignment());

        final ImageRepeatable background = findPaneOfTypeByID("background", ImageRepeatable.class);
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);

        final int pageCount = (int) Math.ceil(WidgetManager.getInstance().getWidgets().size() / (double) layout.getGroupsPerRow());

        for (int pageId = 0; pageId < pageCount; pageId++)
        {
            final View pageRoot = new View();
            pageRoot.setID("page" + pageId);
            Loader.createFromXMLFile(new ResourceLocation(Constants.MOD_ID, "gui/actionspage.xml"), pageRoot);
            pages.addChild(pageRoot);

            final int currentPageOffset = pageId * this.widgetsInColumn;

            final ScrollingList list = pageRoot.findPaneOfTypeByID("buttons", ScrollingList.class);
            list.setDataProvider(new ScrollingList.DataProvider()
            {
                @Override
                public int getElementCount()
                {
                    return widgetsInColumn;
                }

                @Override
                public void updateElement(final int index, final Pane rowPane)
                {
                    for (int groupOffset = 0; groupOffset < widgetsInColumn; groupOffset++)
                    {
                        View buttonContainer = rowPane.findPaneOfTypeByID("button" + index, View.class);
                        if (buttonContainer == null)
                        {
                            buttonContainer = (View) Loader.createFromXMLFile2(new ResourceLocation(Constants.MOD_ID, "gui/actionsitem.xml"), (View) rowPane);
                            buttonContainer.setID("button" + index);
                        }

                        buttonContainer.setPosition(groupOffset * WIDGET_OFFSET, 0);

                        final Widget widget = WidgetManager.getInstance().getWidget(currentPageOffset + groupOffset, index);
                        final ItemIcon item = buttonContainer.findPaneByType(ItemIcon.class);

                        if (item != null && widget != null)
                        {
                            item.setItem(widget.getIcon());
                        }
                    }
                }
            });
        }

        onPageUpdate(0);

        final int maxContainerWidth = WIDGET_SPACING + (widgetsInRow * WIDGET_OFFSET);
        final int maxContainerHeight = WIDGET_SPACING + (Math.min(widgetsInColumn, layout.getMaxButtonsInGroupBeforeScroll()) * WIDGET_OFFSET);

        setSize(maxContainerWidth, maxContainerHeight);
        background.setSize(maxContainerWidth, maxContainerHeight);
        pages.setSize(maxContainerWidth, maxContainerHeight);


        //int rows = 0;
        //int columns = 0;
        //for (List<Widget> widgets : WidgetManager.getInstance().getWidgets().values())
        //{
        //    int rowColumnCount = 0;
        //    for (Widget widget : widgets)
        //    {
        //        final ItemIcon item = new ItemIcon();
        //        item.setSize(16, 16);
        //        item.setItem(widget.getIcon());
        //        item.setAlignment(Alignment.MIDDLE);
        //
        //        final View box = new View();
        //        box.setPosition(WIDGET_SPACING + (rows * WIDGET_OFFSET), WIDGET_SPACING + (rowColumnCount * WIDGET_OFFSET));
        //        box.setSize(WIDGET_SIZE, WIDGET_SIZE);
        //        box.addChild(item);
        //
        //        container.addChild(box);
        //
        //        rowColumnCount++;
        //        if (rowColumnCount > columns)
        //        {
        //            columns = rowColumnCount;
        //        }
        //    }
        //    rows++;
        //}
    }

    @Override
    public void onOpened()
    {
        super.onOpened();
    }

    private void onPageUpdate(final int pageId)
    {
        final SwitchView pages = findPaneOfTypeByID("pages", SwitchView.class);
        pages.setView("page" + pageId);
    }
}
