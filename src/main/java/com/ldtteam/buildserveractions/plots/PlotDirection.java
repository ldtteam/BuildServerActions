package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.Direction;

public enum PlotDirection
{
    OFFICIAL(Direction.NORTH),
    UNOFFICIAL(Direction.SOUTH);

    private final Direction direction;

    PlotDirection(final Direction direction)
    {
        this.direction = direction;
    }

    public Direction getDirection()
    {
        return direction;
    }
}
