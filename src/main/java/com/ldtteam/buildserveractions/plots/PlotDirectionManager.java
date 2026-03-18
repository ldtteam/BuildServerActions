package com.ldtteam.buildserveractions.plots;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Manages all plots for a specific direction (OFFICIAL or UNOFFICIAL).
 * <p>
 * Handles plot creation, extension, renaming, and world rendering for plots
 * that extend in the configured direction. Also responsible for serialization
 * and deserialization of plot data.
 */
public class PlotDirectionManager implements INBTSerializable<CompoundTag>
{
    /**
     * The number of building columns created when a new plot is made.
     */
    private static final int INITIAL_BUILDINGS_COUNT = 20;

    /**
     * The number of decoration columns created when a new plot is made.
     */
    private static final int INITIAL_DECORATIONS_COUNT = 5;

    /**
     * The number of building columns added when extending a plot.
     */
    private static final int EXTEND_BUILDINGS_COUNT = 1;

    /**
     * The number of decoration columns added when extending a plot.
     */
    private static final int EXTEND_DECORATIONS_COUNT = 1;

    /**
     * The width of each decoration column in blocks.
     */
    private static final int DECORATIONS_COLUMN_WIDTH = 100;

    private static final String NBT_PLOTS        = "plots";
    private static final String NBT_NEXT_PLOT_ID = "nextPlotId";
    private static final String NBT_PLOT_OFFSET  = "plotOffset";

    /**
     * Map of plot IDs to plot instances managed by this direction manager.
     */
    private final Int2ObjectArrayMap<Plot> plots = new Int2ObjectArrayMap<>();

    /**
     * The direction this manager handles.
     */
    private final PlotDirection direction;

    /**
     * The next available plot ID for new plots.
     */
    private int nextPlotId = 1;

    /**
     * The offset from origin where plots start in this direction.
     */
    private int plotOffset = 0;

    /**
     * Creates a new plot direction manager for the specified direction.
     *
     * @param direction the direction this manager will handle.
     */
    public PlotDirectionManager(final PlotDirection direction)
    {
        this.direction = direction;
    }

    /**
     * Gets the direction this manager handles.
     *
     * @return the plot direction.
     */
    public PlotDirection getDirection()
    {
        return direction;
    }

    /**
     * Gets the offset from origin where plots start.
     *
     * @return the offset in blocks.
     */
    public int getOffset()
    {
        return plotOffset;
    }

    /**
     * Sets the offset from origin where plots start.
     *
     * @param plotOffset the new offset in blocks.
     */
    public void setOffset(final int plotOffset)
    {
        this.plotOffset = plotOffset;
    }

    /**
     * Gets an unmodifiable view of all plots managed by this direction manager.
     *
     * @return an unmodifiable map of plot IDs to plots.
     */
    public Map<Integer, Plot> getPlots()
    {
        return Collections.unmodifiableMap(plots);
    }

    /**
     * Creates a new plot with the specified parameters and renders it in the world.
     *
     * @param level     the server level to create the plot in.
     * @param name      the name for the new plot.
     * @param size      the size category for the new plot.
     * @param edgeBlock the block state to use for the plot's edge.
     * @param settings  the settings to apply to the new plot.
     * @return the ID of the newly created plot.
     */
    public int createPlot(final ServerLevel level, final String name, final PlotSize size, final BlockState edgeBlock, final PlotSettings settings)
    {
        final int totalOffset = plots.values().stream().mapToInt(plot -> plot.size().getTotalLength() + plot.settings().getPlotRoadSpacing()).sum();
        final BlockPos anchorPoint = new BlockPos(0, settings.getPlotYLevel(), 0).relative(direction.getDirection(), plotOffset + totalOffset);

        final int plotId = nextPlotId++;
        final Plot plot = new Plot(plotId, name, anchorPoint, size, edgeBlock, settings);

        renderCenterRoad(level, plot);
        renderBuildingsArea(level, plot);
        renderDecorationsArea(level, plot);

        createPlotBuildingColumns(level, plot, INITIAL_BUILDINGS_COUNT);
        createPlotDecorationColumns(level, plot, INITIAL_DECORATIONS_COUNT);

        plots.put(plotId, plot);
        return plotId;
    }

    /**
     * Extends an existing plot by adding building or decoration columns.
     *
     * @param level      the server level containing the plot.
     * @param plotId     the ID of the plot to extend.
     * @param extendType the type of extension to apply.
     */
    public void extendPlot(final ServerLevel level, final int plotId, final PlotExtendType extendType)
    {
        final Plot plot = plots.get(plotId);
        if (extendType.equals(PlotExtendType.BUILDINGS))
        {
            createPlotBuildingColumns(level, plot, EXTEND_BUILDINGS_COUNT);
        }
        else if (extendType.equals(PlotExtendType.DECORATIONS))
        {
            createPlotDecorationColumns(level, plot, EXTEND_DECORATIONS_COUNT);
        }
    }

    /**
     * Renames an existing plot.
     *
     * @param plotId  the ID of the plot to rename.
     * @param newName the new name for the plot.
     * @return true if the plot was found and renamed, false if the plot was not found.
     */
    public boolean renamePlot(final int plotId, final String newName)
    {
        final Plot plot = plots.get(plotId);
        if (plot == null)
        {
            return false;
        }
        plot.setName(newName);
        return true;
    }

    /**
     * Creates multiple building columns for a plot and renders them in the world.
     *
     * @param level         the server level to render in.
     * @param plot          the plot to add buildings to.
     * @param buildingCount the number of building columns to create.
     */
    private void createPlotBuildingColumns(final ServerLevel level, final Plot plot, final int buildingCount)
    {
        for (int i = 0; i < buildingCount; ++i)
        {
            final int columnIndex = plot.addBuilding();
            renderBuildingsAreaColumn(level, plot, columnIndex);
        }
    }

    /**
     * Creates multiple decoration columns for a plot and renders them in the world.
     *
     * @param level           the server level to render in.
     * @param plot            the plot to add decorations to.
     * @param decorationCount the number of decoration columns to create.
     */
    private void createPlotDecorationColumns(final ServerLevel level, final Plot plot, final int decorationCount)
    {
        for (int i = 0; i < decorationCount; ++i)
        {
            final int columnIndex = plot.addDecoration();
            renderDecorationsAreaColumn(level, plot, columnIndex);
        }
    }

    @Override
    public CompoundTag serializeNBT(final @NotNull HolderLookup.Provider provider)
    {
        final CompoundTag compound = new CompoundTag();
        final ListTag plotsCompound = new ListTag();
        for (final Plot plot : plots.values())
        {
            plotsCompound.add(plot.serializeNBT());
        }
        compound.put(NBT_PLOTS, plotsCompound);
        compound.putInt(NBT_NEXT_PLOT_ID, nextPlotId);
        compound.putInt(NBT_PLOT_OFFSET, plotOffset);
        return compound;
    }

    @Override
    public void deserializeNBT(final @NotNull HolderLookup.Provider provider, final @NotNull CompoundTag compound)
    {
        final ListTag plotsCompound = compound.getList(NBT_PLOTS, Tag.TAG_COMPOUND);
        final Int2ObjectArrayMap<Plot> plots = new Int2ObjectArrayMap<>();
        for (final Tag plotTag : plotsCompound)
        {
            if (plotTag instanceof CompoundTag plotCompound)
            {
                final Plot plot = Plot.deserializeNBT(provider, plotCompound);
                plots.put(plot.id(), plot);
            }
        }
        this.plots.clear();
        this.plots.putAll(plots);

        this.nextPlotId = compound.getInt(NBT_NEXT_PLOT_ID);
        this.plotOffset = compound.getInt(NBT_PLOT_OFFSET);
    }

    // ========================================
    // World rendering methods
    // ========================================

    /**
     * Fills a rectangular area with the specified block state.
     *
     * @param level     the server level to modify.
     * @param firstPos  one corner of the area.
     * @param secondPos the opposite corner of the area.
     * @param state     the block state to fill with.
     */
    private void fillArea(final ServerLevel level, final BlockPos firstPos, final BlockPos secondPos, final BlockState state)
    {
        BlockPos.betweenClosed(firstPos, secondPos).forEach(pos -> level.setBlock(pos, state, Block.UPDATE_CLIENTS));
    }

    /**
     * Renders the center road for a plot.
     *
     * @param level the server level to render in.
     * @param plot  the plot to render the center road for.
     */
    private void renderCenterRoad(final ServerLevel level, final Plot plot)
    {
        final BlockPos bottomLeftPos = centerRoadBottomLeft(plot).relative(direction.getDirection().getOpposite(), plot.settings().getPlotRoadSpacing());
        final BlockPos topRightPos = centerRoadTopRight(plot).relative(direction.getDirection(), plot.settings().getPlotRoadSpacing());

        // Draw the center road itself
        fillArea(level, bottomLeftPos, topRightPos, plot.settings().getRoadBlock());
    }

    /**
     * Renders the initial buildings area edge for a plot.
     *
     * @param level the server level to render in.
     * @param plot  the plot to render the buildings area for.
     */
    private void renderBuildingsArea(final ServerLevel level, final Plot plot)
    {
        final BlockPos bottomLeftPos = buildingsAreaBottomLeft(plot);
        final BlockPos topLeftPos = buildingsAreaTopLeft(plot);

        // Draw the edge line east of the center road (from bottom to top)
        fillArea(level, bottomLeftPos, topLeftPos, plot.edgeBlock());

        renderRoads(level, plot, bottomLeftPos, bottomLeftPos, topLeftPos, topLeftPos);
    }

    /**
     * Renders a single building column including roads and building platforms.
     *
     * @param level       the server level to render in.
     * @param plot        the plot to render for.
     * @param columnIndex the index of the column to render.
     */
    private void renderBuildingsAreaColumn(final ServerLevel level, final Plot plot, int columnIndex)
    {
        final BlockPos bottomLeftPos = buildingsAreaColumnBottomLeft(plot, columnIndex);
        final BlockPos bottomRightPos = buildingsAreaColumnBottomRight(plot, columnIndex);
        final BlockPos topLeftPos = buildingsAreaColumnTopLeft(plot, columnIndex);
        final BlockPos topRightPos = buildingsAreaColumnTopRight(plot, columnIndex);

        renderRoads(level, plot, bottomLeftPos, bottomRightPos, topLeftPos, topRightPos);

        // Draw the buildings
        for (int i = 0; i < plot.size().getPlotCount(); i++)
        {
            final BlockPos buildingBottomLeft = buildingBottomLeft(plot, columnIndex, i);
            final BlockPos buildingTopRight = buildingTopRight(plot, columnIndex, i);
            fillArea(level, buildingBottomLeft, buildingTopRight, Blocks.WHITE_CONCRETE.defaultBlockState());
        }
    }

    /**
     * Renders the initial decorations area edge for a plot.
     *
     * @param level the server level to render in.
     * @param plot  the plot to render the decorations area for.
     */
    private void renderDecorationsArea(final ServerLevel level, final Plot plot)
    {
        final BlockPos bottomRightPos = decorationsAreaBottomRight(plot);
        final BlockPos topRightPos = decorationsAreaTopRight(plot);

        // Draw the edge line west of the center road (from bottom to top)
        fillArea(level, bottomRightPos, topRightPos, plot.edgeBlock());

        renderRoads(level, plot, bottomRightPos, bottomRightPos, topRightPos, topRightPos);
    }

    /**
     * Renders a single decoration column including roads.
     *
     * @param level       the server level to render in.
     * @param plot        the plot to render for.
     * @param columnIndex the index of the column to render.
     */
    private void renderDecorationsAreaColumn(final ServerLevel level, final Plot plot, int columnIndex)
    {
        final BlockPos bottomLeftPos = decorationsAreaColumnBottomLeft(plot, columnIndex);
        final BlockPos bottomRightPos = decorationsAreaColumnBottomRight(plot, columnIndex);
        final BlockPos topLeftPos = decorationsAreaColumnTopLeft(plot, columnIndex);
        final BlockPos topRightPos = decorationsAreaColumnTopRight(plot, columnIndex);

        renderRoads(level, plot, bottomLeftPos, bottomRightPos, topLeftPos, topRightPos);
    }

    /**
     * Renders roads and edge blocks around a column area.
     *
     * @param level          the server level to render in.
     * @param plot           the plot to render for.
     * @param bottomLeftPos  the bottom-left corner of the column.
     * @param bottomRightPos the bottom-right corner of the column.
     * @param topLeftPos     the top-left corner of the column.
     * @param topRightPos    the top-right corner of the column.
     */
    private void renderRoads(
        final ServerLevel level,
        final Plot plot,
        final BlockPos bottomLeftPos,
        final BlockPos bottomRightPos,
        final BlockPos topLeftPos,
        final BlockPos topRightPos)
    {
        // Draw the edge line bottom of the column
        fillArea(level, bottomLeftPos, bottomRightPos, plot.edgeBlock());
        //Draw the edge line top of the column
        fillArea(level, topLeftPos, topRightPos, plot.edgeBlock());

        // Draw the road at the bottom of the column
        fillArea(level,
            bottomLeftPos.relative(direction.getDirection().getOpposite()),
            bottomRightPos.relative(direction.getDirection().getOpposite(), plot.settings().getPlotRoadSpacing()),
            plot.settings().getRoadBlock());

        // Draw the road at the top of the column
        fillArea(level,
            topLeftPos.relative(direction.getDirection()),
            topRightPos.relative(direction.getDirection(), plot.settings().getPlotRoadSpacing()),
            plot.settings().getRoadBlock());
    }

    // ========================================
    // Position markers - Center Road
    // ========================================

    /**
     * Gets the bottom center position of the center road.
     */
    private BlockPos centerRoadBottomCenter(final Plot plot)
    {
        return plot.anchorPoint();
    }

    /**
     * Gets the top center position of the center road.
     */
    private BlockPos centerRoadTopCenter(final Plot plot)
    {
        return plot.anchorPoint().relative(direction.getDirection(), plot.size().getTotalLength() - 1);
    }

    /**
     * Calculates the left offset from center for the center road.
     */
    private int centerRoadLeftOffset(final Plot plot)
    {
        final int width = plot.settings().getCenterRoadSpacing();
        return (int) Math.floor(width / 2.0);
    }

    /**
     * Calculates the right offset from center for the center road.
     */
    private int centerRoadRightOffset(final Plot plot)
    {
        final int width = plot.settings().getCenterRoadSpacing();
        return (int) Math.ceil(width / 2.0) - 1;
    }

    /**
     * Gets the bottom-left corner position of the center road.
     */
    private BlockPos centerRoadBottomLeft(final Plot plot)
    {
        return centerRoadBottomCenter(plot).west(centerRoadLeftOffset(plot));
    }

    /**
     * Gets the bottom-right corner position of the center road.
     */
    private BlockPos centerRoadBottomRight(final Plot plot)
    {
        return centerRoadBottomCenter(plot).east(centerRoadRightOffset(plot));
    }

    /**
     * Gets the top-left corner position of the center road.
     */
    private BlockPos centerRoadTopLeft(final Plot plot)
    {
        return centerRoadTopCenter(plot).west(centerRoadLeftOffset(plot));
    }

    /**
     * Gets the top-right corner position of the center road.
     */
    private BlockPos centerRoadTopRight(final Plot plot)
    {
        return centerRoadTopCenter(plot).east(centerRoadRightOffset(plot));
    }

    // ========================================
    // Position markers - Buildings area
    // ========================================

    /**
     * Gets the bottom-left corner of the buildings area.
     */
    private BlockPos buildingsAreaBottomLeft(final Plot plot)
    {
        return centerRoadBottomRight(plot).east(1);
    }

    /**
     * Gets the top-left corner of the buildings area.
     */
    private BlockPos buildingsAreaTopLeft(final Plot plot)
    {
        return centerRoadTopRight(plot).east(1);
    }

    /**
     * Gets the bottom-left corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnBottomLeft(final Plot plot, int columnIndex)
    {
        return buildingsAreaBottomLeft(plot).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the bottom-right corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnBottomRight(final Plot plot, int columnIndex)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    /**
     * Gets the top-left corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnTopLeft(final Plot plot, int columnIndex)
    {
        return buildingsAreaTopLeft(plot).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the top-right corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnTopRight(final Plot plot, int columnIndex)
    {
        return buildingsAreaColumnTopLeft(plot, columnIndex).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    /**
     * Gets the bottom-left corner of a specific building platform.
     */
    private BlockPos buildingBottomLeft(final Plot plot, int columnIndex, int buildingIndex)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex).above()
            .east(plot.size().getPlotSpacing())
            .relative(direction.getDirection(), plot.size().getPlotSpacing() + 1)
            .relative(direction.getDirection(), buildingIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the top-right corner of a specific building platform.
     */
    private BlockPos buildingTopRight(final Plot plot, int columnIndex, int buildingIndex)
    {
        return buildingBottomLeft(plot, columnIndex, buildingIndex).relative(direction.getDirection(), plot.size().getPlotSize() - 1).east(plot.size().getPlotSize() - 1);
    }

    // ========================================
    // Position markers - Decorations area
    // ========================================

    /**
     * Gets the bottom-right corner of the decorations area.
     */
    private BlockPos decorationsAreaBottomRight(final Plot plot)
    {
        return centerRoadBottomLeft(plot).west(1);
    }

    /**
     * Gets the top-right corner of the decorations area.
     */
    private BlockPos decorationsAreaTopRight(final Plot plot)
    {
        return centerRoadTopLeft(plot).west(1);
    }

    /**
     * Gets the bottom-right corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnBottomRight(final Plot plot, int columnIndex)
    {
        return decorationsAreaBottomRight(plot).west(1).west(columnIndex * DECORATIONS_COLUMN_WIDTH);
    }

    /**
     * Gets the bottom-left corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnBottomLeft(final Plot plot, int columnIndex)
    {
        return decorationsAreaColumnBottomRight(plot, columnIndex).west(DECORATIONS_COLUMN_WIDTH - 1);
    }

    /**
     * Gets the top-right corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnTopRight(final Plot plot, int columnIndex)
    {
        return decorationsAreaTopRight(plot).west(1).west(columnIndex * DECORATIONS_COLUMN_WIDTH);
    }

    /**
     * Gets the top-left corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnTopLeft(final Plot plot, int columnIndex)
    {
        return decorationsAreaColumnTopRight(plot, columnIndex).west(DECORATIONS_COLUMN_WIDTH - 1);
    }
}
