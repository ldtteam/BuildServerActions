package com.ldtteam.buildserveractions.plots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class Plot
{
    private static final String NBT_PLOT_ID               = "id";
    private static final String NBT_PLOT_NAME             = "name";
    private static final String NBT_PLOT_ANCHOR_POINT     = "anchorPoint";
    private static final String NBT_PLOT_SIZE             = "size";
    private static final String NBT_PLOT_EDGE_BLOCK       = "edgeBlock";
    private static final String NBT_PLOT_SETTINGS         = "settings";
    private static final String NBT_PLOT_BUILDING_COUNT   = "buildingCount";
    private static final String NBT_PLOT_DECORATIONS_COUNT = "decorationsCount";

    private final int          id;
    private final String       name;
    private final BlockPos     anchorPoint;
    private final PlotSize     size;
    private final BlockState   edgeBlock;
    private final PlotSettings settings;

    private int buildingCount;
    private int decorationsCount;

    public Plot(final int id, final String name, final BlockPos anchorPoint, final PlotSize size, final BlockState edgeBlock, final PlotSettings settings)
    {
        this.id = id;
        this.name = name;
        this.anchorPoint = anchorPoint;
        this.size = size;
        this.edgeBlock = edgeBlock;
        this.settings = settings;
    }

    public int id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public BlockPos anchorPoint()
    {
        return anchorPoint;
    }

    public PlotSize size()
    {
        return size;
    }

    public BlockState edgeBlock()
    {
        return edgeBlock;
    }

    public PlotSettings settings()
    {
        return settings;
    }

    public int addBuilding()
    {
        return buildingCount++;
    }

    public int addDecoration()
    {
        return decorationsCount++;
    }

    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = new CompoundTag();
        compound.putInt(NBT_PLOT_ID, id);
        compound.putString(NBT_PLOT_NAME, name);
        compound.put(NBT_PLOT_ANCHOR_POINT, NbtUtils.writeBlockPos(anchorPoint));
        compound.putString(NBT_PLOT_SIZE, size.name());
        compound.put(NBT_PLOT_EDGE_BLOCK, NbtUtils.writeBlockState(edgeBlock));
        compound.put(NBT_PLOT_SETTINGS, settings.serializeNBT());
        compound.putInt(NBT_PLOT_BUILDING_COUNT, buildingCount);
        compound.putInt(NBT_PLOT_DECORATIONS_COUNT, decorationsCount);
        return compound;
    }

    public static Plot deserializeNBT(final @NotNull HolderLookup.Provider provider, final @NotNull CompoundTag compound)
    {
        final int id = compound.getInt(NBT_PLOT_ID);
        final String name = compound.getString(NBT_PLOT_NAME);
        final BlockPos anchorPoint = NbtUtils.readBlockPos(compound, NBT_PLOT_ANCHOR_POINT).orElseThrow();
        final PlotSize size = PlotSize.valueOf(compound.getString(NBT_PLOT_SIZE));
        final BlockState edgeBlock = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), compound.getCompound(NBT_PLOT_EDGE_BLOCK));
        final PlotSettings settings = PlotSettings.deserializeNBT(provider, compound.getCompound(NBT_PLOT_SETTINGS));
        final Plot plot = new Plot(id, name, anchorPoint, size, edgeBlock, settings);
        plot.buildingCount = compound.getInt(NBT_PLOT_BUILDING_COUNT);
        plot.decorationsCount = compound.getInt(NBT_PLOT_DECORATIONS_COUNT);
        return plot;
    }
}
