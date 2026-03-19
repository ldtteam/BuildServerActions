package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Once set, this cannot be changed (settings are locked after initial setup).
     */
    @Nullable
    private PlotSettings baseSettings = null;

    /**
     * Checks if the base settings have been configured.
     *
     * @return true if setup has been completed, false otherwise.
     */
    public boolean isSetupComplete()
    {
        return baseSettings != null;
    }

    /**
     * Gets the base settings for plot creation.
     *
     * @return the base settings, or null if setup is not complete.
     */
    @Nullable
    public PlotSettings getBaseSettings()
    {
        return baseSettings;
    }

    /**
     * Sets the base settings for plot creation.
     * This can only be called once - after settings are set, they are locked.
     *
     * @param settings the settings to apply.
     * @return true if settings were applied, false if settings were already locked.
     */
    public boolean setBaseSettings(final PlotSettings settings)
    {
        if (baseSettings != null)
        {
            return false;
        }
        this.baseSettings = settings;
        return true;
    }

    /**
     * Sets the offset for a specific plot direction.
     *
     * @param direction the direction to set the offset for.
     * @param offset    the offset value in blocks.
     */
    public void setDirectionOffset(final PlotDirection direction, final int offset)
    {
        plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).setOffset(offset);
    }

    /**
     * Creates a new plot manager instance with default settings.
     */
    public PlotManager()
    {
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
     * @return the ID of the newly created plot or null if setup is not completed.
     */
    public Integer createPlot(final ServerLevel level, final String name, final PlotSize size, final PlotDirection direction, final BlockState edgeBlock)
    {
        if (baseSettings != null)
        {
            return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).createPlot(level, name, size, edgeBlock, baseSettings);
        }
        return null;
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
        if (baseSettings != null)
        {
            compound.put(NBT_BASE_SETTINGS, baseSettings.serializeNBT());
        }
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

        if (compound.contains(NBT_BASE_SETTINGS))
        {
            this.baseSettings = PlotSettings.deserializeNBT(provider, compound.getCompound(NBT_BASE_SETTINGS));
        }
    }
}
