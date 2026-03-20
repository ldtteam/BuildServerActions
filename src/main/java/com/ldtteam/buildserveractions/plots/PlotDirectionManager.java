package com.ldtteam.buildserveractions.plots;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
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
        final int totalOffset = plots.values().stream().mapToInt(plot -> plot.size().getTotalLength() + settings.getPlotRoadSpacing()).sum();
        final BlockPos anchorPoint = new BlockPos(0, settings.getPlotYLevel(), 0).relative(direction.getDirection(), plotOffset + totalOffset);

        final int plotId = nextPlotId++;
        final Plot plot = new Plot(plotId, name, anchorPoint, size, edgeBlock);

        renderCenterRoad(level, plot, settings);
        renderBuildingsArea(level, plot, settings);
        renderDecorationsArea(level, plot, settings);

        createPlotBuildingColumns(level, plot, INITIAL_BUILDINGS_COUNT, settings);
        createPlotDecorationColumns(level, plot, INITIAL_DECORATIONS_COUNT, settings);

        // Render title board last so it overlays any edge blocks from roads
        renderTitleBoard(level, plot, settings);

        plots.put(plotId, plot);
        return plotId;
    }

    /**
     * Extends an existing plot by adding building or decoration columns.
     *
     * @param level      the server level containing the plot.
     * @param plotId     the ID of the plot to extend.
     * @param extendType the type of extension to apply.
     * @param settings   the plot settings to use for rendering.
     */
    public void extendPlot(final ServerLevel level, final int plotId, final PlotExtendType extendType, final PlotSettings settings)
    {
        final Plot plot = plots.get(plotId);
        if (extendType.equals(PlotExtendType.BUILDINGS))
        {
            createPlotBuildingColumns(level, plot, EXTEND_BUILDINGS_COUNT, settings);
        }
        else if (extendType.equals(PlotExtendType.DECORATIONS))
        {
            createPlotDecorationColumns(level, plot, EXTEND_DECORATIONS_COUNT, settings);
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
     * Regenerates an existing plot by redrawing all its sections.
     *
     * @param level    the server level containing the plot.
     * @param plotId   the ID of the plot to regenerate.
     * @param settings the plot settings to use for rendering.
     * @return true if the plot was found and regenerated, false if the plot was not found.
     */
    public boolean regeneratePlot(final ServerLevel level, final int plotId, final PlotSettings settings)
    {
        final Plot plot = plots.get(plotId);
        if (plot == null)
        {
            return false;
        }

        // Render the base plot areas
        renderCenterRoad(level, plot, settings);
        renderBuildingsArea(level, plot, settings);
        renderDecorationsArea(level, plot, settings);

        // Render all existing building columns
        for (int i = 0; i < plot.getBuildingCount(); ++i)
        {
            renderBuildingsAreaColumn(level, plot, i, settings);
        }

        // Render all existing decoration columns
        for (int i = 0; i < plot.getDecorationsCount(); ++i)
        {
            renderDecorationsAreaColumn(level, plot, i, settings);
        }

        // Render title board last so it overlays any edge blocks from roads
        renderTitleBoard(level, plot, settings);

        return true;
    }

    /**
     * Creates multiple building columns for a plot and renders them in the world.
     *
     * @param level         the server level to render in.
     * @param plot          the plot to add buildings to.
     * @param buildingCount the number of building columns to create.
     * @param settings      the plot settings to use for rendering.
     */
    private void createPlotBuildingColumns(final ServerLevel level, final Plot plot, final int buildingCount, final PlotSettings settings)
    {
        for (int i = 0; i < buildingCount; ++i)
        {
            final int columnIndex = plot.addBuilding();
            renderBuildingsAreaColumn(level, plot, columnIndex, settings);
        }
    }

    /**
     * Creates multiple decoration columns for a plot and renders them in the world.
     *
     * @param level           the server level to render in.
     * @param plot            the plot to add decorations to.
     * @param decorationCount the number of decoration columns to create.
     * @param settings        the plot settings to use for rendering.
     */
    private void createPlotDecorationColumns(final ServerLevel level, final Plot plot, final int decorationCount, final PlotSettings settings)
    {
        for (int i = 0; i < decorationCount; ++i)
        {
            final int columnIndex = plot.addDecoration();
            renderDecorationsAreaColumn(level, plot, columnIndex, settings);
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
     * @param level    the server level to render in.
     * @param plot     the plot to render the center road for.
     * @param settings the plot settings to use for rendering.
     */
    private void renderCenterRoad(final ServerLevel level, final Plot plot, final PlotSettings settings)
    {
        final BlockPos bottomLeftPos = centerRoadBottomLeft(plot, settings).relative(direction.getDirection().getOpposite(), settings.getPlotRoadSpacing());
        final BlockPos topRightPos = centerRoadTopRight(plot, settings).relative(direction.getDirection(), settings.getPlotRoadSpacing());

        // Draw the center road itself
        fillArea(level, bottomLeftPos, topRightPos, settings.getRoadBlock());
    }

    /**
     * Renders the initial buildings area edge for a plot.
     *
     * @param level    the server level to render in.
     * @param plot     the plot to render the buildings area for.
     * @param settings the plot settings to use for rendering.
     */
    private void renderBuildingsArea(final ServerLevel level, final Plot plot, final PlotSettings settings)
    {
        final BlockPos bottomLeftPos = buildingsAreaBottomLeft(plot, settings);
        final BlockPos topLeftPos = buildingsAreaTopLeft(plot, settings);

        // Draw the edge line east of the center road (from bottom to top)
        fillArea(level, bottomLeftPos, topLeftPos, plot.edgeBlock());

        renderRoads(level, plot, bottomLeftPos, bottomLeftPos, topLeftPos, topLeftPos, settings);
    }

    /**
     * Renders a single building column including roads and building platforms.
     *
     * @param level       the server level to render in.
     * @param plot        the plot to render for.
     * @param columnIndex the index of the column to render.
     * @param settings    the plot settings to use for rendering.
     */
    private void renderBuildingsAreaColumn(final ServerLevel level, final Plot plot, int columnIndex, final PlotSettings settings)
    {
        final BlockPos bottomLeftPos = buildingsAreaColumnBottomLeft(plot, columnIndex, settings);
        final BlockPos bottomRightPos = buildingsAreaColumnBottomRight(plot, columnIndex, settings);
        final BlockPos topLeftPos = buildingsAreaColumnTopLeft(plot, columnIndex, settings);
        final BlockPos topRightPos = buildingsAreaColumnTopRight(plot, columnIndex, settings);

        renderRoads(level, plot, bottomLeftPos, bottomRightPos, topLeftPos, topRightPos, settings);

        // Draw the buildings
        for (int i = 0; i < plot.size().getPlotCount(); i++)
        {
            final BlockPos buildingBottomLeft = buildingBottomLeft(plot, columnIndex, i, settings);
            final BlockPos buildingTopRight = buildingTopRight(plot, columnIndex, i, settings);
            fillArea(level, buildingBottomLeft, buildingTopRight, Blocks.WHITE_CONCRETE.defaultBlockState());
        }
    }

    /**
     * Renders the initial decorations area edge for a plot.
     *
     * @param level    the server level to render in.
     * @param plot     the plot to render the decorations area for.
     * @param settings the plot settings to use for rendering.
     */
    private void renderDecorationsArea(final ServerLevel level, final Plot plot, final PlotSettings settings)
    {
        final BlockPos bottomRightPos = decorationsAreaBottomRight(plot, settings);
        final BlockPos topRightPos = decorationsAreaTopRight(plot, settings);

        // Draw the edge line west of the center road (from bottom to top)
        fillArea(level, bottomRightPos, topRightPos, plot.edgeBlock());

        renderRoads(level, plot, bottomRightPos, bottomRightPos, topRightPos, topRightPos, settings);
    }

    /**
     * Renders a single decoration column including roads.
     *
     * @param level       the server level to render in.
     * @param plot        the plot to render for.
     * @param columnIndex the index of the column to render.
     * @param settings    the plot settings to use for rendering.
     */
    private void renderDecorationsAreaColumn(final ServerLevel level, final Plot plot, int columnIndex, final PlotSettings settings)
    {
        final BlockPos bottomLeftPos = decorationsAreaColumnBottomLeft(plot, columnIndex, settings);
        final BlockPos bottomRightPos = decorationsAreaColumnBottomRight(plot, columnIndex, settings);
        final BlockPos topLeftPos = decorationsAreaColumnTopLeft(plot, columnIndex, settings);
        final BlockPos topRightPos = decorationsAreaColumnTopRight(plot, columnIndex, settings);

        renderRoads(level, plot, bottomLeftPos, bottomRightPos, topLeftPos, topRightPos, settings);
    }

    /**
     * Renders the title board structure at the bottom-right corner of the decorations area.
     * <p>
     * The title board is a small structure with a sign for displaying the plot name.
     * It includes a floor, wall with sign, and roof made of mangrove slabs.
     *
     * @param level    the server level to render in.
     * @param plot     the plot to render the title board for.
     * @param settings the plot settings to use for rendering.
     */
    private void renderTitleBoard(final ServerLevel level, final Plot plot, final PlotSettings settings)
    {
        // Title board layout (4 wide E/W × 6 long N/S):
        // - Floor: 4×6 road blocks
        // - Wall: 5 blocks long on the western column of the floor, 3 blocks tall
        // - Sign: on east face of center wall block
        // - Roof: 5×3 slabs above the wall
        // - North edge: 1 row of edge blocks north of the floor
        //
        // Anchor point: SW corner of the 4×6 floor area

        final BlockPos anchor = decorationsAreaBottomRight(plot, settings)
            .east(1)                                    // 1 east from decorations edge
            .relative(direction.getDirection(), 1);     // 1 into plot from spacer road

        // Floor: 4×6 road blocks (overwrites old south edge)
        final BlockPos floorSW = anchor.relative(direction.getDirection().getOpposite(), 1).west(4);
        final BlockPos floorNE = anchor.relative(direction.getDirection(), 4).west(1);
        fillArea(level, floorSW, floorNE, settings.getRoadBlock());

        // North edge: 1×5 edge blocks (includes NW corner)
        final BlockPos northEdgeStart = anchor.relative(direction.getDirection(), 5).west(5);
        final BlockPos northEdgeEnd = anchor.relative(direction.getDirection(), 5).west(1);
        fillArea(level, northEdgeStart, northEdgeEnd, plot.edgeBlock());

        // West edge: 6×1 edge blocks (from south row to north row, 1 block west of floor)
        final BlockPos westEdgeStart = anchor.relative(direction.getDirection().getOpposite(), 1).west(5);
        final BlockPos westEdgeEnd = anchor.relative(direction.getDirection(), 4).west(5);
        fillArea(level, westEdgeStart, westEdgeEnd, plot.edgeBlock());

        // Wall positions (on west column of floor, 5 blocks N/S)
        final BlockPos wallSouth = anchor.west(4);
        final BlockPos wallNorth = anchor.relative(direction.getDirection(), 4).west(4);

        // Build wall (Y+1 to Y+3)
        for (int y = 1; y <= 3; y++)
        {
            // End pillars (Tuff Bricks)
            level.setBlock(wallNorth.above(y), Blocks.TUFF_BRICKS.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlock(wallSouth.above(y), Blocks.TUFF_BRICKS.defaultBlockState(), Block.UPDATE_CLIENTS);

            // Middle 3 blocks
            for (int i = 1; i <= 3; i++)
            {
                final BlockPos middlePos = wallNorth.relative(direction.getDirection().getOpposite(), i).above(y);
                final BlockState block = (y == 1 || y == 3)
                    ? Blocks.CHISELED_TUFF_BRICKS.defaultBlockState()
                    : Blocks.CHISELED_TUFF.defaultBlockState();
                level.setBlock(middlePos, block, Block.UPDATE_CLIENTS);
            }
        }

        // Wall sign on east face of center block at Y+2
        final BlockPos signPos = wallNorth.relative(direction.getDirection().getOpposite(), 2).above(2).east(1);
        final BlockState signState = Blocks.MANGROVE_WALL_SIGN.defaultBlockState()
            .setValue(WallSignBlock.FACING, Direction.EAST);
        level.setBlock(signPos, signState, Block.UPDATE_CLIENTS);

        // Configure the sign block entity
        if (level.getBlockEntity(signPos) instanceof SignBlockEntity signEntity)
        {
            // Set the front text to white and glowing
            SignText frontText = signEntity.getFrontText()
                .setColor(DyeColor.WHITE)
                .setHasGlowingText(true);
            signEntity.setText(frontText, true);
        }

        // Roof: 5×3 slabs
        final BlockState bottomSlab = Blocks.MANGROVE_SLAB.defaultBlockState()
            .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, SlabType.BOTTOM);
        final BlockState topSlab = Blocks.MANGROVE_SLAB.defaultBlockState()
            .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, SlabType.TOP);

        for (int i = 0; i < 5; i++)
        {
            final BlockPos rowBase = wallNorth.relative(direction.getDirection().getOpposite(), i);

            // West column (behind wall): top slab at Y+3
            level.setBlock(rowBase.west(1).above(3), topSlab, Block.UPDATE_CLIENTS);

            // Middle column (on wall): bottom slab at Y+4
            level.setBlock(rowBase.above(4), bottomSlab, Block.UPDATE_CLIENTS);

            // East column (in front of wall): bottom slab at Y+4
            level.setBlock(rowBase.east(1).above(4), bottomSlab, Block.UPDATE_CLIENTS);
        }
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
     * @param settings       the plot settings to use for rendering.
     */
    private void renderRoads(
        final ServerLevel level,
        final Plot plot,
        final BlockPos bottomLeftPos,
        final BlockPos bottomRightPos,
        final BlockPos topLeftPos,
        final BlockPos topRightPos,
        final PlotSettings settings)
    {
        // Draw the edge line bottom of the column
        fillArea(level, bottomLeftPos, bottomRightPos, plot.edgeBlock());
        //Draw the edge line top of the column
        fillArea(level, topLeftPos, topRightPos, plot.edgeBlock());

        // Draw the road at the bottom of the column
        fillArea(level,
            bottomLeftPos.relative(direction.getDirection().getOpposite()),
            bottomRightPos.relative(direction.getDirection().getOpposite(), settings.getPlotRoadSpacing()),
            settings.getRoadBlock());

        // Draw the road at the top of the column
        fillArea(level,
            topLeftPos.relative(direction.getDirection()),
            topRightPos.relative(direction.getDirection(), settings.getPlotRoadSpacing()),
            settings.getRoadBlock());
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
    private int centerRoadLeftOffset(final PlotSettings settings)
    {
        final int width = settings.getCenterRoadSpacing();
        return (int) Math.floor(width / 2.0);
    }

    /**
     * Calculates the right offset from center for the center road.
     */
    private int centerRoadRightOffset(final PlotSettings settings)
    {
        final int width = settings.getCenterRoadSpacing();
        return (int) Math.ceil(width / 2.0) - 1;
    }

    /**
     * Gets the bottom-left corner position of the center road.
     */
    private BlockPos centerRoadBottomLeft(final Plot plot, final PlotSettings settings)
    {
        return centerRoadBottomCenter(plot).west(centerRoadLeftOffset(settings));
    }

    /**
     * Gets the bottom-right corner position of the center road.
     */
    private BlockPos centerRoadBottomRight(final Plot plot, final PlotSettings settings)
    {
        return centerRoadBottomCenter(plot).east(centerRoadRightOffset(settings));
    }

    /**
     * Gets the top-left corner position of the center road.
     */
    private BlockPos centerRoadTopLeft(final Plot plot, final PlotSettings settings)
    {
        return centerRoadTopCenter(plot).west(centerRoadLeftOffset(settings));
    }

    /**
     * Gets the top-right corner position of the center road.
     */
    private BlockPos centerRoadTopRight(final Plot plot, final PlotSettings settings)
    {
        return centerRoadTopCenter(plot).east(centerRoadRightOffset(settings));
    }

    // ========================================
    // Position markers - Buildings area
    // ========================================

    /**
     * Gets the bottom-left corner of the buildings area.
     */
    private BlockPos buildingsAreaBottomLeft(final Plot plot, final PlotSettings settings)
    {
        return centerRoadBottomRight(plot, settings).east(1);
    }

    /**
     * Gets the top-left corner of the buildings area.
     */
    private BlockPos buildingsAreaTopLeft(final Plot plot, final PlotSettings settings)
    {
        return centerRoadTopRight(plot, settings).east(1);
    }

    /**
     * Gets the bottom-left corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnBottomLeft(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return buildingsAreaBottomLeft(plot, settings).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the bottom-right corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnBottomRight(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex, settings).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    /**
     * Gets the top-left corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnTopLeft(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return buildingsAreaTopLeft(plot, settings).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the top-right corner of a specific building column.
     */
    private BlockPos buildingsAreaColumnTopRight(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return buildingsAreaColumnTopLeft(plot, columnIndex, settings).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    /**
     * Gets the bottom-left corner of a specific building platform.
     */
    private BlockPos buildingBottomLeft(final Plot plot, int columnIndex, int buildingIndex, final PlotSettings settings)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex, settings).above()
            .east(plot.size().getPlotSpacing())
            .relative(direction.getDirection(), plot.size().getPlotSpacing() + 1)
            .relative(direction.getDirection(), buildingIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    /**
     * Gets the top-right corner of a specific building platform.
     */
    private BlockPos buildingTopRight(final Plot plot, int columnIndex, int buildingIndex, final PlotSettings settings)
    {
        return buildingBottomLeft(plot, columnIndex, buildingIndex, settings).relative(direction.getDirection(), plot.size().getPlotSize() - 1).east(plot.size().getPlotSize() - 1);
    }

    // ========================================
    // Position markers - Decorations area
    // ========================================

    /**
     * Gets the bottom-right corner of the decorations area.
     */
    private BlockPos decorationsAreaBottomRight(final Plot plot, final PlotSettings settings)
    {
        return centerRoadBottomLeft(plot, settings).west(1);
    }

    /**
     * Gets the top-right corner of the decorations area.
     */
    private BlockPos decorationsAreaTopRight(final Plot plot, final PlotSettings settings)
    {
        return centerRoadTopLeft(plot, settings).west(1);
    }

    /**
     * Gets the bottom-right corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnBottomRight(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return decorationsAreaBottomRight(plot, settings).west(1).west(columnIndex * DECORATIONS_COLUMN_WIDTH);
    }

    /**
     * Gets the bottom-left corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnBottomLeft(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return decorationsAreaColumnBottomRight(plot, columnIndex, settings).west(DECORATIONS_COLUMN_WIDTH - 1);
    }

    /**
     * Gets the top-right corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnTopRight(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return decorationsAreaTopRight(plot, settings).west(1).west(columnIndex * DECORATIONS_COLUMN_WIDTH);
    }

    /**
     * Gets the top-left corner of a specific decoration column.
     */
    private BlockPos decorationsAreaColumnTopLeft(final Plot plot, int columnIndex, final PlotSettings settings)
    {
        return decorationsAreaColumnTopRight(plot, columnIndex, settings).west(DECORATIONS_COLUMN_WIDTH - 1);
    }
}
