package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central manager for all plots in the world.
 * <p>
 * Manages plots across all directions and provides access to global plot settings.
 * This class is stored as a data attachment on the server level.
 */
public final class PlotManager implements INBTSerializable<CompoundTag>
{
    /**
     * Creates a new plot manager instance with default settings.
     */
    public PlotManager()
    {
    }

    private static final String NBT_PLOT_DIRECTION_MANAGERS          = "plotDirectionManagers";
    private static final String NBT_PLOT_DIRECTION_MANAGER_DIRECTION = "direction";
    private static final String NBT_PLOT_DIRECTION_MANAGER_DATA      = "data";
    private static final String NBT_BASE_SETTINGS                    = "baseSettings";

    /**
     * Map of direction managers, one for each plot direction.
     */
    private final EnumMap<PlotDirection, PlotDirectionManager> plotDirectionManagers = new EnumMap<>(PlotDirection.class);

    /**
     * The base settings used for creating new plots.
     */
    @NotNull
    private PlotSettings baseSettings = new PlotSettings(5, 3, -61, Blocks.STONE_BRICKS.defaultBlockState());

    /**
     * Gets the plot offset for the specified direction.
     *
     * @param direction the plot direction.
     * @return the offset in blocks.
     */
    public int getPlotOffset(final PlotDirection direction)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).getOffset();
    }

    /**
     * Sets the plot offset for the specified direction.
     *
     * @param direction  the plot direction.
     * @param plotOffset the new offset in blocks.
     */
    public void setPlotOffset(final PlotDirection direction, final int plotOffset)
    {
        plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).setOffset(plotOffset);
    }

    /**
     * Gets the center road spacing from base settings.
     *
     * @return the center road spacing in blocks.
     */
    public int getCenterRoadSpacing()
    {
        return baseSettings.getCenterRoadSpacing();
    }

    /**
     * Sets the center road spacing in base settings.
     *
     * @param centerRoadSpacing the new center road spacing in blocks.
     */
    public void setCenterRoadSpacing(final int centerRoadSpacing)
    {
        baseSettings.setCenterRoadSpacing(centerRoadSpacing);
    }

    /**
     * Gets the plot road spacing from base settings.
     *
     * @return the plot road spacing in blocks.
     */
    public int getPlotRoadSpacing()
    {
        return baseSettings.getPlotRoadSpacing();
    }

    /**
     * Sets the plot road spacing in base settings.
     *
     * @param plotRoadSpacing the new plot road spacing in blocks.
     */
    public void setPlotRoadSpacing(final int plotRoadSpacing)
    {
        baseSettings.setPlotRoadSpacing(plotRoadSpacing);
    }

    /**
     * Gets the Y level for plots from base settings.
     *
     * @return the plot Y level.
     */
    public int getPlotYLevel()
    {
        return baseSettings.getPlotYLevel();
    }

    /**
     * Sets the Y level for plots in base settings.
     *
     * @param plotYLevel the new plot Y level.
     */
    public void setPlotYLevel(final int plotYLevel)
    {
        baseSettings.setPlotYLevel(plotYLevel);
    }

    /**
     * Gets the road block from base settings.
     *
     * @return the road block state.
     */
    public BlockState getRoadBlock()
    {
        return baseSettings.getRoadBlock();
    }

    /**
     * Sets the road block in base settings.
     *
     * @param roadBlock the new road block state.
     */
    public void setRoadBlock(final BlockState roadBlock)
    {
        baseSettings.setRoadBlock(roadBlock);
    }

    /**
     * Gets all plots for the specified direction.
     *
     * @param direction the plot direction.
     * @return an unmodifiable map of plot IDs to plots.
     */
    public Map<Integer, Plot> getPlots(final PlotDirection direction)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).getPlots();
    }

    /**
     * Creates a new plot in the specified direction.
     *
     * @param level     the server level to create the plot in.
     * @param name      the name for the new plot.
     * @param size      the size category for the new plot.
     * @param direction the direction to create the plot in.
     * @param edgeBlock the block state to use for the plot's edge.
     * @return the ID of the newly created plot.
     */
    public int createPlot(final ServerLevel level, final String name, final PlotSize size, final PlotDirection direction, final BlockState edgeBlock)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).createPlot(level, name, size, edgeBlock, baseSettings);
    }

    /**
     * Extends an existing plot by adding building or decoration columns.
     *
     * @param level      the server level containing the plot.
     * @param direction  the direction of the plot.
     * @param plotId     the ID of the plot to extend.
     * @param extendType the type of extension to apply.
     */
    public void extendPlot(final ServerLevel level, final PlotDirection direction, final int plotId, final PlotExtendType extendType)
    {
        plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).extendPlot(level, plotId, extendType);
    }

    /**
     * Renames an existing plot.
     *
     * @param direction the direction of the plot.
     * @param plotId    the ID of the plot to rename.
     * @param newName   the new name for the plot.
     * @return true if the plot was found and renamed, false otherwise.
     */
    public boolean renamePlot(final PlotDirection direction, final int plotId, final String newName)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).renamePlot(plotId, newName);
    }

    @Override
    public CompoundTag serializeNBT(final @NotNull HolderLookup.Provider provider)
    {
        final CompoundTag compound = new CompoundTag();
        final ListTag plotDirectionManagersCompound = new ListTag();
        for (final PlotDirectionManager directionManager : plotDirectionManagers.values())
        {
            final CompoundTag plotDirectionManagerCompound = new CompoundTag();
            plotDirectionManagerCompound.putString(NBT_PLOT_DIRECTION_MANAGER_DIRECTION, directionManager.getDirection().name());
            plotDirectionManagerCompound.put(NBT_PLOT_DIRECTION_MANAGER_DATA, directionManager.serializeNBT(provider));
            plotDirectionManagersCompound.add(plotDirectionManagerCompound);
        }
        compound.put(NBT_PLOT_DIRECTION_MANAGERS, plotDirectionManagersCompound);
        compound.put(NBT_BASE_SETTINGS, baseSettings.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(final @NotNull HolderLookup.Provider provider, final @NotNull CompoundTag compound)
    {
        final EnumMap<PlotDirection, PlotDirectionManager> plotDirectionManagers = new EnumMap<>(PlotDirection.class);
        final ListTag plotDirectionManagersCompound = compound.getList(NBT_PLOT_DIRECTION_MANAGERS, ListTag.TAG_COMPOUND);
        for (final Tag plotDirectionManagerTag : plotDirectionManagersCompound)
        {
            if (plotDirectionManagerTag instanceof CompoundTag plotDirectionManagerCompound)
            {
                final PlotDirection plotDirection = PlotDirection.valueOf(plotDirectionManagerCompound.getString(NBT_PLOT_DIRECTION_MANAGER_DIRECTION));
                final PlotDirectionManager plotDirectionManager = new PlotDirectionManager(plotDirection);
                plotDirectionManager.deserializeNBT(provider, plotDirectionManagerCompound.getCompound(NBT_PLOT_DIRECTION_MANAGER_DATA));
                plotDirectionManagers.put(plotDirection, plotDirectionManager);
            }
        }

        this.plotDirectionManagers.clear();
        this.plotDirectionManagers.putAll(plotDirectionManagers);

        this.baseSettings = PlotSettings.deserializeNBT(provider, compound.getCompound(NBT_BASE_SETTINGS));
    }
}
