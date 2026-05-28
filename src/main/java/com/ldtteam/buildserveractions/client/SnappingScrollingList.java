package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.views.ScrollingList;

/**
 * A {@link ScrollingList} that snaps scroll position to row boundaries.
 */
public class SnappingScrollingList extends ScrollingList
{
    private int rowHeight = 1;

    /**
     * Constructs a new snapping scrolling list from XML pane parameters.
     *
     * @param params the pane parameters from the XML layout.
     */
    public SnappingScrollingList(final PaneParams params)
    {
        super(params);
    }

    /**
     * Sets the row height used for snap calculations.
     *
     * @param rowHeight height of one row in pixels, including spacing.
     */
    public void setRowHeight(final int rowHeight)
    {
        this.rowHeight = rowHeight;
    }

    @Override
    public boolean scrollInput(final double horizontalWheel, final double verticalWheel, final double mx, final double my)
    {
        final double current = getScrollY();
        final double target = current - Math.signum(verticalWheel) * rowHeight;
        return setScrollY(Math.round(target / rowHeight) * rowHeight);
    }
}
