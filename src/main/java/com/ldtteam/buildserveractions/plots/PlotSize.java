package com.ldtteam.buildserveractions.plots;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the size category of a plot.
 * <p>
 * Each size defines the dimensions of individual building plots within the plot area,
 * the spacing between them, and how many building plots fit in each column.
 */
public enum PlotSize implements StringRepresentable
{
    /**
     * Small plots with 32x32 building areas, 16 block spacing, and 5 plots per column.
     */
    SMALL(32, 16, 5),

    /**
     * Large plots with 48x48 building areas, 16 block spacing, and 5 plots per column.
     */
    MEDIUM(48, 16, 5),

    /**
     * Large plots with 64x64 building areas, 16 block spacing, and 5 plots per column.
     */
    LARGE(64, 16, 5);

    /**
     * The size of each individual building plot in blocks.
     */
    private final int plotSize;

    /**
     * The spacing between individual building plots in blocks.
     */
    private final int plotSpacing;

    /**
     * The number of building plots in each column.
     */
    private final int plotCount;

    /**
     * Creates a new plot size with the specified parameters.
     *
     * @param plotSize    the size of each individual building plot in blocks.
     * @param spacing     the spacing between individual building plots in blocks.
     * @param plotCount   the number of building plots in each column.
     */
    PlotSize(final int plotSize, final int spacing, final int plotCount)
    {
        this.plotSize = plotSize;
        this.plotSpacing = spacing;
        this.plotCount = plotCount;
    }

    /**
     * Gets the size of each individual building plot in blocks.
     *
     * @return the plot size in blocks.
     */
    public int getPlotSize()
    {
        return plotSize;
    }

    /**
     * Gets the spacing between individual building plots in blocks.
     *
     * @return the spacing in blocks.
     */
    public int getPlotSpacing()
    {
        return plotSpacing;
    }

    /**
     * Gets the number of building plots in each column.
     *
     * @return the plot count.
     */
    public int getPlotCount()
    {
        return plotCount;
    }

    /**
     * Calculates the total length of a plot column in blocks.
     * <p>
     * This includes all building plots, spacing between them, and edge blocks.
     *
     * @return the total length in blocks.
     */
    public int getTotalLength()
    {
        return (plotSize * plotCount) + (plotSpacing * (plotCount + 1)) + 2;
    }

    @Override
    @NotNull
    public String getSerializedName()
    {
        return name().toLowerCase();
    }
}
