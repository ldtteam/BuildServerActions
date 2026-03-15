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

public class PlotDirectionManager implements INBTSerializable<CompoundTag>
{
    private static final int INITIAL_BUILDINGS_COUNT = 20;
    private static final int EXTEND_BUILDINGS_COUNT  = 1;

    private static final String NBT_PLOTS        = "plots";
    private static final String NBT_NEXT_PLOT_ID = "nextPlotId";
    private static final String NBT_PLOT_OFFSET  = "plotOffset";

    private final Int2ObjectArrayMap<Plot> plots = new Int2ObjectArrayMap<>();

    private final PlotDirection direction;

    private int nextPlotId = 1;

    private int plotOffset = 0;

    public PlotDirectionManager(final PlotDirection direction)
    {
        this.direction = direction;
    }

    public PlotDirection getDirection()
    {
        return direction;
    }

    public int getOffset()
    {
        return plotOffset;
    }

    public void setOffset(final int plotOffset)
    {
        this.plotOffset = plotOffset;
    }

    public Map<Integer, Plot> getPlots()
    {
        return Collections.unmodifiableMap(plots);
    }

    public int createPlot(final ServerLevel level, final String name, final PlotSize size, final BlockState edgeBlock, final PlotSettings settings)
    {
        final int totalOffset = plots.values().stream().mapToInt(plot -> plot.size().getTotalLength() + plot.settings().getPlotRoadSpacing()).sum();
        final BlockPos anchorPoint = new BlockPos(0, settings.getPlotYLevel(), 0)
            .relative(direction.getDirection(), plotOffset + totalOffset);

        final int plotId = nextPlotId++;
        final Plot plot = new Plot(plotId, name, anchorPoint, size, edgeBlock, settings);

        renderCenterRoad(level, plot);
        renderBuildingsArea(level, plot);

        createPlotBuildingColumn(level, plot, INITIAL_BUILDINGS_COUNT);

        plots.put(plotId, plot);
        return plotId;
    }

    public void extendPlot(final ServerLevel level, final int plotId, final PlotExtendType extendType)
    {
        final Plot plot = plots.get(plotId);
        if (extendType.equals(PlotExtendType.BUILDINGS))
        {
            createPlotBuildingColumn(level, plot, EXTEND_BUILDINGS_COUNT);
        }
    }

    private void createPlotBuildingColumn(final ServerLevel level, final Plot plot, final int buildingCount)
    {
        for (int i = 0; i < buildingCount; ++i)
        {
            final int columnIndex = plot.addBuilding();
            renderBuildingsAreaColumn(level, plot, columnIndex);
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

    // Schematic options

    private void fillArea(final ServerLevel level, final BlockPos firstPos, final BlockPos secondPos, final BlockState state)
    {
        BlockPos.betweenClosed(firstPos, secondPos).forEach(pos -> level.setBlock(pos, state, Block.UPDATE_CLIENTS));
    }

    private void renderCenterRoad(final ServerLevel level, final Plot plot)
    {
        final BlockPos bottomLeftPos = centerRoadBottomLeft(plot).relative(direction.getDirection().getOpposite(), plot.settings().getPlotRoadSpacing());
        final BlockPos topRightPos = centerRoadTopRight(plot).relative(direction.getDirection(), plot.settings().getPlotRoadSpacing());

        // Draw the center road itself
        fillArea(level, bottomLeftPos, topRightPos, plot.settings().getRoadBlock());
    }

    private void renderBuildingsArea(final ServerLevel level, final Plot plot)
    {
        final BlockPos bottomLeftPos = buildingsAreaBottomLeft(plot);
        final BlockPos topLeftPos = buildingsAreaTopLeft(plot);

        // Draw the edge line east of the center road
        fillArea(level, bottomLeftPos, topLeftPos, plot.edgeBlock());

        // Draw the road at the top of the east line
        fillArea(level,
            bottomLeftPos.relative(direction.getDirection().getOpposite()),
            bottomLeftPos.relative(direction.getDirection().getOpposite(), plot.settings().getPlotRoadSpacing()),
            plot.settings().getRoadBlock());

        // Draw the road at the top of the east line
        fillArea(level,
            topLeftPos.relative(direction.getDirection()),
            topLeftPos.relative(direction.getDirection(), plot.settings().getPlotRoadSpacing()),
            plot.settings().getRoadBlock());
    }

    private void renderBuildingsAreaColumn(final ServerLevel level, final Plot plot, int columnIndex)
    {
        final BlockPos bottomLeftPos = buildingsAreaColumnBottomLeft(plot, columnIndex);
        final BlockPos bottomRightPos = buildingsAreaColumnBottomRight(plot, columnIndex);
        final BlockPos topLeftPos = buildingsAreaColumnTopLeft(plot, columnIndex);
        final BlockPos topRightPos = buildingsAreaColumnTopRight(plot, columnIndex);

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

        // Draw the buildings
        for (int i = 0; i < plot.size().getPlotCount(); i++)
        {
            final BlockPos buildingBottomLeft = buildingBottomLeft(plot, columnIndex, i);
            final BlockPos buildingTopRight = buildingTopRight(plot, columnIndex, i);
            fillArea(level, buildingBottomLeft, buildingTopRight, Blocks.WHITE_CONCRETE.defaultBlockState());
        }
    }

    //private void renderEndWestRoad(final ServerLevel level, final Plot plot, final int sectionCount)
    //{
    //    final int width = plot.settings().getPlotRoadSpacing();
    //    final int length = 100;
    //
    //    BlockPos startPos = plot.anchorPoint().west(length);
    //
    //    final int rightOffset = (int) Math.ceil(width / 2.0) - 1;
    //}

    // Position markers - Center Road

    private BlockPos centerRoadBottomCenter(final Plot plot)
    {
        return plot.anchorPoint();
    }

    private BlockPos centerRoadTopCenter(final Plot plot)
    {
        return plot.anchorPoint().relative(direction.getDirection(), plot.size().getTotalLength() - 1);
    }

    private int centerRoadLeftOffset(final Plot plot)
    {
        final int width = plot.settings().getCenterRoadSpacing();
        return (int) Math.floor(width / 2.0);
    }

    private int centerRoadRightOffset(final Plot plot)
    {
        final int width = plot.settings().getCenterRoadSpacing();
        return (int) Math.ceil(width / 2.0) - 1;
    }

    private BlockPos centerRoadBottomLeft(final Plot plot)
    {
        return centerRoadBottomCenter(plot).west(centerRoadLeftOffset(plot));
    }

    private BlockPos centerRoadBottomRight(final Plot plot)
    {
        return centerRoadBottomCenter(plot).east(centerRoadRightOffset(plot));
    }

    private BlockPos centerRoadTopLeft(final Plot plot)
    {
        return centerRoadTopCenter(plot).west(centerRoadLeftOffset(plot));
    }

    private BlockPos centerRoadTopRight(final Plot plot)
    {
        return centerRoadTopCenter(plot).east(centerRoadRightOffset(plot));
    }

    // Position markers - Buildings area

    private BlockPos buildingsAreaBottomLeft(final Plot plot)
    {
        return centerRoadBottomRight(plot).east(1);
    }

    private BlockPos buildingsAreaTopLeft(final Plot plot)
    {
        return centerRoadTopRight(plot).east(1);
    }

    private BlockPos buildingsAreaColumnBottomLeft(final Plot plot, int columnIndex)
    {
        return buildingsAreaBottomLeft(plot).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    private BlockPos buildingsAreaColumnBottomRight(final Plot plot, int columnIndex)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    private BlockPos buildingsAreaColumnTopLeft(final Plot plot, int columnIndex)
    {
        return buildingsAreaTopLeft(plot).east(1).east(columnIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    private BlockPos buildingsAreaColumnTopRight(final Plot plot, int columnIndex)
    {
        return buildingsAreaColumnTopLeft(plot, columnIndex).east(plot.size().getPlotSize() + plot.size().getPlotSpacing() - 1);
    }

    private BlockPos buildingBottomLeft(final Plot plot, int columnIndex, int buildingIndex)
    {
        return buildingsAreaColumnBottomLeft(plot, columnIndex).above()
            .east(plot.size().getPlotSpacing())
            .relative(direction.getDirection(), plot.size().getPlotSpacing() + 1)
            .relative(direction.getDirection(), buildingIndex * (plot.size().getPlotSize() + plot.size().getPlotSpacing()));
    }

    private BlockPos buildingTopRight(final Plot plot, int columnIndex, int buildingIndex)
    {
        return buildingBottomLeft(plot, columnIndex, buildingIndex).relative(direction.getDirection(), plot.size().getPlotSize() - 1).east(plot.size().getPlotSize() - 1);
    }
}
