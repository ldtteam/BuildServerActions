package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the direction/category of a plot.
 * <p>
 * Plots are organized into two directions:
 * <ul>
 *   <li>{@link #OFFICIAL} - Official plots extending north, requiring elevated permissions to create.</li>
 *   <li>{@link #UNOFFICIAL} - Unofficial plots extending south, available to all users.</li>
 * </ul>
 */
public enum PlotDirection implements StringRepresentable
{
    /**
     * Official plots that extend north. Requires gamemaster permissions to create.
     */
    OFFICIAL(Direction.NORTH),

    /**
     * Unofficial plots that extend south. Available to all users.
     */
    UNOFFICIAL(Direction.SOUTH);

    /**
     * The Minecraft direction this plot direction maps to.
     */
    private final Direction direction;

    /**
     * Creates a new plot direction with the specified Minecraft direction.
     *
     * @param direction the Minecraft direction this plot direction maps to.
     */
    PlotDirection(final Direction direction)
    {
        this.direction = direction;
    }

    /**
     * Gets the Minecraft direction this plot direction maps to.
     *
     * @return the Minecraft direction.
     */
    public Direction getDirection()
    {
        return direction;
    }

    @Override
    @NotNull
    public String getSerializedName()
    {
        return name().toLowerCase();
    }
}
