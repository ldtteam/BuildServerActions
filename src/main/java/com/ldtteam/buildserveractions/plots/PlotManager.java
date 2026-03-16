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

public final class PlotManager implements INBTSerializable<CompoundTag>
{
    private static final String NBT_PLOT_DIRECTION_MANAGERS          = "plotDirectionManagers";
    private static final String NBT_PLOT_DIRECTION_MANAGER_DIRECTION = "direction";
    private static final String NBT_PLOT_DIRECTION_MANAGER_DATA      = "data";
    private static final String NBT_BASE_SETTINGS                    = "baseSettings";

    private final EnumMap<PlotDirection, PlotDirectionManager> plotDirectionManagers = new EnumMap<>(PlotDirection.class);

    @NotNull
    private PlotSettings baseSettings = new PlotSettings(5, 3, -61, Blocks.STONE_BRICKS.defaultBlockState());

    public int getPlotOffset(final PlotDirection direction)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).getOffset();
    }

    public void setPlotOffset(final PlotDirection direction, final int plotOffset)
    {
        plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).setOffset(plotOffset);
    }

    public int getCenterRoadSpacing()
    {
        return baseSettings.getCenterRoadSpacing();
    }

    public void setCenterRoadSpacing(final int centerRoadSpacing)
    {
        baseSettings.setCenterRoadSpacing(centerRoadSpacing);
    }

    public int getPlotRoadSpacing()
    {
        return baseSettings.getPlotRoadSpacing();
    }

    public void setPlotRoadSpacing(final int plotRoadSpacing)
    {
        baseSettings.setPlotRoadSpacing(plotRoadSpacing);
    }

    public int getPlotYLevel()
    {
        return baseSettings.getPlotYLevel();
    }

    public void setPlotYLevel(final int plotYLevel)
    {
        baseSettings.setPlotYLevel(plotYLevel);
    }

    public BlockState getRoadBlock()
    {
        return baseSettings.getRoadBlock();
    }

    public void setRoadBlock(final BlockState roadBlock)
    {
        baseSettings.setRoadBlock(roadBlock);
    }

    public Map<Integer, Plot> getPlots(final PlotDirection direction)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).getPlots();
    }

    public int createPlot(final ServerLevel level, final String name, final PlotSize size, final PlotDirection direction, final BlockState edgeBlock)
    {
        return plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).createPlot(level, name, size, edgeBlock, baseSettings);
    }

    public void extendPlot(final ServerLevel level, final PlotDirection direction, final int plotId, final PlotExtendType extendType)
    {
        plotDirectionManagers.computeIfAbsent(direction, PlotDirectionManager::new).extendPlot(level, plotId, extendType);
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
