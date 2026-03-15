package com.ldtteam.buildserveractions.plots;

public enum PlotSize
{
    SMALL(32, 8, 5),
    LARGE(64, 8, 5);

    private final int plotSize;

    private final int plotSpacing;

    private final int plotCount;

    PlotSize(final int plotSize, final int spacing, final int plotCount)
    {
        this.plotSize = plotSize;
        this.plotSpacing = spacing;
        this.plotCount = plotCount;
    }

    public int getPlotSize()
    {
        return plotSize;
    }

    public int getPlotSpacing()
    {
        return plotSpacing;
    }

    public int getPlotCount()
    {
        return plotCount;
    }

    public int getTotalLength()
    {
        return (plotSize * plotCount) + (plotSpacing * (plotCount + 1)) + 2;
    }
}
