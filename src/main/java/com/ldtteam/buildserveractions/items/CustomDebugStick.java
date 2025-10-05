package com.ldtteam.buildserveractions.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Custom instance of the {@link DebugStickItem} that removes the operator conditional so that it can be used by anyone.
 */
public class CustomDebugStick extends DebugStickItem
{
    public CustomDebugStick()
    {
        super(new Item.Properties());
    }

    @Override
    protected boolean handleInteraction(
        @NotNull Player pPlayer,
        BlockState pStateClicked,
        @NotNull LevelAccessor pAccessor,
        @NotNull BlockPos pPos,
        boolean pShouldCycleState,
        @NotNull ItemStack pDebugStack)
    {
        Block block = pStateClicked.getBlock();
        StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
        Collection<Property<?>> collection = statedefinition.getProperties();
        String s = ForgeRegistries.BLOCKS.getKey(block).toString();
        if (collection.isEmpty())
        {
            message(pPlayer, Component.translatable(this.getDescriptionId() + ".empty", s));
            return false;
        }
        else
        {
            CompoundTag compoundtag = pDebugStack.getOrCreateTagElement("DebugProperty");
            String s1 = compoundtag.getString(s);
            Property<?> property = statedefinition.getProperty(s1);
            if (pShouldCycleState)
            {
                if (property == null)
                {
                    property = collection.iterator().next();
                }

                BlockState blockstate = cycleState(pStateClicked, property, pPlayer.isSecondaryUseActive());
                pAccessor.setBlock(pPos, blockstate, 18);
                message(pPlayer, Component.translatable(this.getDescriptionId() + ".update", property.getName(), getNameHelper(blockstate, property)));
            }
            else
            {
                property = getRelative(collection, property, pPlayer.isSecondaryUseActive());
                String s2 = property.getName();
                compoundtag.putString(s, s2);
                message(pPlayer, Component.translatable(this.getDescriptionId() + ".select", s2, getNameHelper(pStateClicked, property)));
            }

            return true;
        }
    }

    @Override
    @NotNull
    public String getDescriptionId()
    {
        return Items.DEBUG_STICK.getDescriptionId();
    }
}
