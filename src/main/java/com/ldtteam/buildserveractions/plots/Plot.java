package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a plot in the build server world.
 * <p>
 * A plot is a designated area with a specific size, anchor point, and settings.
 * It contains areas for buildings and decorations that can be extended over time.
 */
public final class Plot
{
    private static final String NBT_PLOT_ID                = "id";
    private static final String NBT_PLOT_NAME              = "name";
    private static final String NBT_PLOT_ANCHOR_POINT      = "anchorPoint";
    private static final String NBT_PLOT_SIZE              = "size";
    private static final String NBT_PLOT_EDGE_BLOCK        = "edgeBlock";
    private static final String NBT_PLOT_BUILDING_COUNT    = "buildingCount";
    private static final String NBT_PLOT_DECORATIONS_COUNT = "decorationsCount";

    /**
     * The unique identifier for this plot.
     */
    private final int id;

    /**
     * The display name of this plot.
     */
    private String name;

    /**
     * The anchor point (origin) of this plot in world coordinates.
     */
    private final BlockPos anchorPoint;

    /**
     * The size category of this plot.
     */
    private final PlotSize size;

    /**
     * The block state used for the plot's edge/border.
     */
    private final BlockState edgeBlock;

    /**
     * The current number of building columns in this plot.
     */
    private int buildingCount;

    /**
     * The current number of decoration columns in this plot.
     */
    private int decorationsCount;

    /**
     * Creates a new plot with the specified parameters.
     *
     * @param id          the unique identifier for this plot.
     * @param name        the display name of this plot.
     * @param anchorPoint the anchor point (origin) of this plot in world coordinates.
     * @param size        the size category of this plot.
     * @param edgeBlock   the block state used for the plot's edge/border.
     */
    public Plot(final int id, final String name, final BlockPos anchorPoint, final PlotSize size, final BlockState edgeBlock)
    {
        this.id = id;
        this.name = name;
        this.anchorPoint = anchorPoint;
        this.size = size;
        this.edgeBlock = edgeBlock;
    }

    /**
     * Gets the unique identifier of this plot.
     *
     * @return the plot ID.
     */
    public int id()
    {
        return id;
    }

    /**
     * Gets the display name of this plot.
     *
     * @return the plot name.
     */
    public String name()
    {
        return name;
    }

    /**
     * Sets the display name of this plot.
     *
     * @param name the new name for this plot.
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Gets the anchor point (origin) of this plot.
     *
     * @return the anchor point as a BlockPos.
     */
    public BlockPos anchorPoint()
    {
        return anchorPoint;
    }

    /**
     * Gets the size category of this plot.
     *
     * @return the plot size.
     */
    public PlotSize size()
    {
        return size;
    }

    /**
     * Gets the block state used for the plot's edge/border.
     *
     * @return the edge block state.
     */
    public BlockState edgeBlock()
    {
        return edgeBlock;
    }

    /**
     * Adds a new building column to this plot and returns its index.
     *
     * @return the index of the newly added building column.
     */
    public int addBuilding()
    {
        return buildingCount++;
    }

    /**
     * Adds a new decoration column to this plot and returns its index.
     *
     * @return the index of the newly added decoration column.
     */
    public int addDecoration()
    {
        return decorationsCount++;
    }

    /**
     * Gets the current number of building columns in this plot.
     *
     * @return the building column count.
     */
    public int getBuildingCount()
    {
        return buildingCount;
    }

    /**
     * Gets the current number of decoration columns in this plot.
     *
     * @return the decoration column count.
     */
    public int getDecorationsCount()
    {
        return decorationsCount;
    }

    /**
     * Serializes this plot to NBT format for persistence.
     *
     * @return a CompoundTag containing all plot data.
     */
    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = new CompoundTag();
        compound.putInt(NBT_PLOT_ID, id);
        compound.putString(NBT_PLOT_NAME, name);
        compound.put(NBT_PLOT_ANCHOR_POINT, NbtUtils.writeBlockPos(anchorPoint));
        compound.putString(NBT_PLOT_SIZE, size.name());
        compound.put(NBT_PLOT_EDGE_BLOCK, NbtUtils.writeBlockState(edgeBlock));
        compound.putInt(NBT_PLOT_BUILDING_COUNT, buildingCount);
        compound.putInt(NBT_PLOT_DECORATIONS_COUNT, decorationsCount);
        return compound;
    }

    /**
     * Deserializes a plot from NBT format.
     *
     * @param provider the holder lookup provider for block state deserialization.
     * @param compound the CompoundTag containing the plot data.
     * @return a new Plot instance with the deserialized data.
     */
    public static Plot deserializeNBT(final @NotNull HolderLookup.Provider provider, final @NotNull CompoundTag compound)
    {
        final int id = compound.getInt(NBT_PLOT_ID);
        final String name = compound.getString(NBT_PLOT_NAME);
        final BlockPos anchorPoint = NbtUtils.readBlockPos(compound, NBT_PLOT_ANCHOR_POINT).orElseThrow();
        final PlotSize size = PlotSize.valueOf(compound.getString(NBT_PLOT_SIZE));
        final BlockState edgeBlock = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), compound.getCompound(NBT_PLOT_EDGE_BLOCK));
        final Plot plot = new Plot(id, name, anchorPoint, size, edgeBlock);
        plot.buildingCount = compound.getInt(NBT_PLOT_BUILDING_COUNT);
        plot.decorationsCount = compound.getInt(NBT_PLOT_DECORATIONS_COUNT);
        return plot;
    }
}
