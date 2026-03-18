package com.ldtteam.buildserveractions.plots;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the type of extension that can be applied to a plot.
 * <p>
 * Plots can be extended by adding more columns for either buildings or decorations.
 */
public enum PlotExtendType implements StringRepresentable
{
    /**
     * Extends the plot by adding a new building column.
     */
    BUILDINGS,

    /**
     * Extends the plot by adding a new decoration column.
     */
    DECORATIONS;

    @Override
    @NotNull
    public String getSerializedName()
    {
        return name().toLowerCase();
    }
}
