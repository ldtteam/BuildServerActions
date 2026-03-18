package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Contains configurable settings for plots.
 * <p>
 * These settings control the layout and appearance of plots, including
 * road spacing, Y level, and road block type.
 */
public final class PlotSettings
{
    private static final String NBT_CENTER_ROAD_SPACING = "centerRoadSpacing";
    private static final String NBT_PLOT_ROAD_SPACING   = "plotRoadSpacing";
    private static final String NBT_PLOT_Y_LEVEL        = "plotYLevel";
    private static final String NBT_ROAD_BLOCK          = "roadBlock";

    /**
     * The width of the center road in blocks.
     */
    private int centerRoadSpacing;

    /**
     * The width of roads between plot columns in blocks.
     */
    private int plotRoadSpacing;

    /**
     * The Y coordinate level where plots are generated.
     */
    private int plotYLevel;

    /**
     * The block state used for road surfaces.
     */
    @NotNull
    private BlockState roadBlock;

    /**
     * Creates new plot settings with the specified parameters.
     *
     * @param centerRoadSpacing the width of the center road in blocks.
     * @param plotRoadSpacing   the width of roads between plot columns in blocks.
     * @param plotYLevel        the Y coordinate level where plots are generated.
     * @param roadBlock         the block state used for road surfaces.
     */
    public PlotSettings(final int centerRoadSpacing, final int plotRoadSpacing, final int plotYLevel, final @NotNull BlockState roadBlock)
    {
        this.centerRoadSpacing = centerRoadSpacing;
        this.plotRoadSpacing = plotRoadSpacing;
        this.plotYLevel = plotYLevel;
        this.roadBlock = roadBlock;
    }

    /**
     * Gets the width of the center road in blocks.
     *
     * @return the center road spacing.
     */
    public int getCenterRoadSpacing()
    {
        return centerRoadSpacing;
    }

    /**
     * Sets the width of the center road in blocks.
     *
     * @param centerRoadSpacing the new center road spacing.
     */
    public void setCenterRoadSpacing(final int centerRoadSpacing)
    {
        this.centerRoadSpacing = centerRoadSpacing;
    }

    /**
     * Gets the width of roads between plot columns in blocks.
     *
     * @return the plot road spacing.
     */
    public int getPlotRoadSpacing()
    {
        return plotRoadSpacing;
    }

    /**
     * Sets the width of roads between plot columns in blocks.
     *
     * @param plotRoadSpacing the new plot road spacing.
     */
    public void setPlotRoadSpacing(final int plotRoadSpacing)
    {
        this.plotRoadSpacing = plotRoadSpacing;
    }

    /**
     * Gets the Y coordinate level where plots are generated.
     *
     * @return the plot Y level.
     */
    public int getPlotYLevel()
    {
        return plotYLevel;
    }

    /**
     * Sets the Y coordinate level where plots are generated.
     *
     * @param plotYLevel the new plot Y level.
     */
    public void setPlotYLevel(final int plotYLevel)
    {
        this.plotYLevel = plotYLevel;
    }

    /**
     * Gets the block state used for road surfaces.
     *
     * @return the road block state.
     */
    @NotNull
    public BlockState getRoadBlock()
    {
        return roadBlock;
    }

    /**
     * Sets the block state used for road surfaces.
     *
     * @param roadBlock the new road block state.
     */
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

    /**
     * Serializes these settings to NBT format for persistence.
     *
     * @return a CompoundTag containing all settings data.
     */
    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = new CompoundTag();
        compound.putInt(NBT_CENTER_ROAD_SPACING, centerRoadSpacing);
        compound.putInt(NBT_PLOT_ROAD_SPACING, plotRoadSpacing);
        compound.putInt(NBT_PLOT_Y_LEVEL, plotYLevel);
        compound.put(NBT_ROAD_BLOCK, NbtUtils.writeBlockState(roadBlock));
        return compound;
    }

    /**
     * Deserializes plot settings from NBT format.
     *
     * @param provider the holder lookup provider for block state deserialization.
     * @param compound the CompoundTag containing the settings data.
     * @return a new PlotSettings instance with the deserialized data.
     */
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
