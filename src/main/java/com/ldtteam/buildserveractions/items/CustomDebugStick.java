package com.ldtteam.buildserveractions.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
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
    protected boolean handleInteraction(Player player, BlockState stateClicked, LevelAccessor accessor, BlockPos pos, boolean shouldCycleState, ItemStack debugStack)
    {
        Holder<Block> holder = stateClicked.getBlockHolder();
        StateDefinition<Block, BlockState> statedefinition = holder.value().getStateDefinition();
        Collection<Property<?>> collection = statedefinition.getProperties();
        if (collection.isEmpty())
        {
            message(player, Component.translatable(this.getDescriptionId() + ".empty", holder.getRegisteredName()));
            return false;
        }
        else
        {
            DebugStickState debugstickstate = debugStack.get(DataComponents.DEBUG_STICK_STATE);
            if (debugstickstate == null)
            {
                return false;
            }
            else
            {
                Property<?> property = debugstickstate.properties().get(holder);
                if (shouldCycleState)
                {
                    if (property == null)
                    {
                        property = collection.iterator().next();
                    }

                    BlockState blockstate = cycleState(stateClicked, property, player.isSecondaryUseActive());
                    accessor.setBlock(pos, blockstate, 18);
                    message(player, Component.translatable(this.getDescriptionId() + ".update", property.getName(), getNameHelper(blockstate, property)));
                }
                else
                {
                    property = getRelative(collection, property, player.isSecondaryUseActive());
                    debugStack.set(DataComponents.DEBUG_STICK_STATE, debugstickstate.withProperty(holder, property));
                    message(player, Component.translatable(this.getDescriptionId() + ".select", property.getName(), getNameHelper(stateClicked, property)));
                }

                return true;
            }
        }
    }

    @Override
    @NotNull
    public DataComponentMap components()
    {
        return Items.DEBUG_STICK.components();
    }

    @Override
    @NotNull
    public String getDescriptionId()
    {
        return Items.DEBUG_STICK.getDescriptionId();
    }
}
