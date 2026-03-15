package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class PlotSettings
{
    private static final String NBT_CENTER_ROAD_SPACING = "centerRoadSpacing";
    private static final String NBT_PLOT_ROAD_SPACING   = "plotRoadSpacing";
    private static final String NBT_PLOT_Y_LEVEL        = "plotYLevel";
    private static final String NBT_ROAD_BLOCK          = "roadBlock";

    private int        centerRoadSpacing;
    private int        plotRoadSpacing;
    private int        plotYLevel;
    @NotNull
    private BlockState roadBlock;

    public PlotSettings(final int centerRoadSpacing, final int plotRoadSpacing, final int plotYLevel, final @NotNull BlockState roadBlock)
    {
        this.centerRoadSpacing = centerRoadSpacing;
        this.plotRoadSpacing = plotRoadSpacing;
        this.plotYLevel = plotYLevel;
        this.roadBlock = roadBlock;
    }

    public int getCenterRoadSpacing()
    {
        return centerRoadSpacing;
    }

    public void setCenterRoadSpacing(final int centerRoadSpacing)
    {
        this.centerRoadSpacing = centerRoadSpacing;
    }

    public int getPlotRoadSpacing()
    {
        return plotRoadSpacing;
    }

    public void setPlotRoadSpacing(final int plotRoadSpacing)
    {
        this.plotRoadSpacing = plotRoadSpacing;
    }

    public int getPlotYLevel()
    {
        return plotYLevel;
    }

    public void setPlotYLevel(final int plotYLevel)
    {
        this.plotYLevel = plotYLevel;
    }

    @NotNull
    public BlockState getRoadBlock()
    {
        return roadBlock;
    }

    public void setRoadBlock(final @NotNull BlockState roadBlock)
    {
        this.roadBlock = roadBlock;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (!(o instanceof final PlotSettings that))
        {
            return false;
        }

        return getCenterRoadSpacing() == that.getCenterRoadSpacing() && plotRoadSpacing == that.plotRoadSpacing && getPlotYLevel() == that.getPlotYLevel() && getRoadBlock().equals(
            that.getRoadBlock());
    }

    @Override
    public int hashCode()
    {
        int result = getCenterRoadSpacing();
        result = 31 * result + plotRoadSpacing;
        result = 31 * result + getPlotYLevel();
        result = 31 * result + getRoadBlock().hashCode();
        return result;
    }

    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = new CompoundTag();
        compound.putInt(NBT_CENTER_ROAD_SPACING, centerRoadSpacing);
        compound.putInt(NBT_PLOT_ROAD_SPACING, plotRoadSpacing);
        compound.putInt(NBT_PLOT_Y_LEVEL, plotYLevel);
        compound.put(NBT_ROAD_BLOCK, NbtUtils.writeBlockState(roadBlock));
        return compound;
    }

    public static PlotSettings deserializeNBT(final @NotNull HolderLookup.Provider provider, final @NotNull CompoundTag compound)
    {
        final int centerRoadSpacing = compound.getInt(NBT_CENTER_ROAD_SPACING);
        final int plotRoadSpacing = compound.getInt(NBT_PLOT_ROAD_SPACING);
        final int plotYLevel = compound.getInt(NBT_PLOT_Y_LEVEL);
        final BlockState roadBlock = compound.contains(NBT_ROAD_BLOCK)
            ? NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), compound.getCompound(NBT_ROAD_BLOCK))
            : Blocks.STONE_BRICKS.defaultBlockState();
        return new PlotSettings(centerRoadSpacing, plotRoadSpacing, plotYLevel, roadBlock);
    }
}
