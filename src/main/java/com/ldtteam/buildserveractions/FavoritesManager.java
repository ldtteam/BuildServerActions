package com.ldtteam.buildserveractions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Client-side manager for the 10 favorite widget slots.
 * Persisted to &lt;gamedir&gt;/buildserveractions/favorites.nbt via NbtIo.
 */
public class FavoritesManager
{
    /**
     * Total number of available favorite slots.
     */
    public static final int SLOT_COUNT = 10;

    private static final String NBT_SLOTS_KEY = "slots";
    private static final String NBT_INDEX_KEY = "index";
    private static final String NBT_WIDGET_KEY = "widget";

    private static FavoritesManager instance;

    /** Ordered slots; null means the slot is empty. */
    private final ResourceLocation[] slots = new ResourceLocation[SLOT_COUNT];

    private File saveFile;

    private FavoritesManager() {}

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@link FavoritesManager} instance.
     */
    public static FavoritesManager getInstance()
    {
        if (instance == null)
        {
            instance = new FavoritesManager();
        }
        return instance;
    }

    /**
     * Call on client world join to set the save location and load persisted data.
     *
     * @param gameDir the Minecraft game directory.
     */
    public void load(final File gameDir)
    {
        final File dir = new File(gameDir, "buildserveractions");
        dir.mkdirs();
        this.saveFile = new File(dir, "favorites.nbt");

        if (!saveFile.exists())
        {
            return;
        }

        try
        {
            final CompoundTag root = NbtIo.read(saveFile.toPath());
            if (root == null)
            {
                return;
            }
            final ListTag list = root.getList(NBT_SLOTS_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
            {
                final CompoundTag entry = list.getCompound(i);
                final int index = entry.getInt(NBT_INDEX_KEY);
                final String widgetStr = entry.getString(NBT_WIDGET_KEY);
                if (index >= 0 && index < SLOT_COUNT && !widgetStr.isEmpty())
                {
                    slots[index] = ResourceLocation.tryParse(widgetStr);
                }
            }
        }
        catch (final IOException e)
        {
            clearAll();
        }
    }

    /**
     * Call on client world leave to clear in-memory state.
     */
    public void unload()
    {
        clearAll();
        saveFile = null;
    }

    /**
     * Get the widget ID assigned to a slot.
     *
     * @param slot 0-based slot index.
     * @return the resource location, or null if the slot is empty.
     */
    @Nullable
    public ResourceLocation getSlot(final int slot)
    {
        if (slot < 0 || slot >= SLOT_COUNT)
        {
            return null;
        }
        return slots[slot];
    }

    /**
     * Assign a widget to a slot, replacing any existing assignment. Persists immediately.
     *
     * @param slot     0-based slot index.
     * @param widgetId the widget resource location to assign.
     */
    public void setSlot(final int slot, final ResourceLocation widgetId)
    {
        if (slot < 0 || slot >= SLOT_COUNT)
        {
            return;
        }
        // Remove from any other slot first to avoid duplicates
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            if (widgetId.equals(slots[i]))
            {
                slots[i] = null;
            }
        }
        slots[slot] = widgetId;
        save();
    }

    /**
     * Clear a slot. Persists immediately.
     *
     * @param slot 0-based slot index.
     */
    public void clearSlot(final int slot)
    {
        if (slot < 0 || slot >= SLOT_COUNT)
        {
            return;
        }
        slots[slot] = null;
        save();
    }

    /**
     * Returns true if the given widget ID is assigned to any slot.
     *
     * @param widgetId the widget resource location.
     * @return whether it is a favorite.
     */
    public boolean isFavorite(final ResourceLocation widgetId)
    {
        for (final ResourceLocation slot : slots)
        {
            if (widgetId.equals(slot))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the slot index for a widget ID, or -1 if not favorited.
     *
     * @param widgetId the widget resource location.
     * @return the 0-based slot index, or -1.
     */
    public int getSlotFor(final ResourceLocation widgetId)
    {
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            if (widgetId.equals(slots[i]))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns how many slots are currently occupied.
     *
     * @return the number of non-empty favorite slots.
     */
    public int getFilledSlotCount()
    {
        int count = 0;
        for (final ResourceLocation slot : slots)
        {
            if (slot != null)
            {
                count++;
            }
        }
        return count;
    }

    private void clearAll()
    {
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            slots[i] = null;
        }
    }

    private void save()
    {
        if (saveFile == null)
        {
            return;
        }

        final ListTag list = new ListTag();
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            if (slots[i] != null)
            {
                final CompoundTag entry = new CompoundTag();
                entry.putInt(NBT_INDEX_KEY, i);
                entry.putString(NBT_WIDGET_KEY, slots[i].toString());
                list.add(entry);
            }
        }

        final CompoundTag root = new CompoundTag();
        root.put(NBT_SLOTS_KEY, list);

        try
        {
            NbtIo.write(root, saveFile.toPath());
        }
        catch (final IOException e)
        {
            // Non-fatal — next save attempt will retry
        }
    }
}
