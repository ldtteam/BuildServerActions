package com.ldtteam.buildserveractions.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A virtual {@link ContainerLevelAccess} implementation for opening menus without a physical block.
 * <p>
 * The key insight is how vanilla's stillValid() works:
 * <pre>
 * protected static boolean stillValid(ContainerLevelAccess access, Player player, Block targetBlock) {
 *     return access.evaluate(
 *         (level, pos) -> level.getBlockState(pos).is(targetBlock) && player.canInteractWithBlock(pos, 4.0),
 *         true  // <-- DEFAULT VALUE when evaluate() returns Optional.empty()
 *     );
 * }
 * </pre>
 * <p>
 * This implementation:
 * <ul>
 *   <li>{@link #evaluate}: Returns {@link Optional#empty()}, causing stillValid() to use its
 *       default value of {@code true} - the menu stays open without checking for a physical block</li>
 *   <li>{@link #execute}: Actually runs the callback with the player's level/position, unlike
 *       {@link ContainerLevelAccess#NULL} which does nothing - items are properly returned on close</li>
 * </ul>
 * <p>
 * Comparison:
 * <ul>
 *   <li>{@code ContainerLevelAccess.create(level, pos)}: evaluate() returns actual result, block check fails, menu closes</li>
 *   <li>{@code ContainerLevelAccess.NULL}: evaluate() returns empty (menu stays open), but execute() does nothing (items lost)</li>
 *   <li>{@code VirtualContainerLevelAccess}: evaluate() returns empty (menu stays open), execute() runs (items returned)</li>
 * </ul>
 */
public class VirtualContainerLevelAccess implements ContainerLevelAccess
{
    private final Level level;
    private final BlockPos pos;

    /**
     * Creates a virtual container level access for the given player.
     *
     * @param player the player opening the menu.
     * @return a new VirtualContainerLevelAccess instance.
     */
    public static VirtualContainerLevelAccess create(final Player player)
    {
        return new VirtualContainerLevelAccess(player.level(), player.blockPosition());
    }

    private VirtualContainerLevelAccess(final Level level, final BlockPos pos)
    {
        this.level = level;
        this.pos = pos;
    }

    /**
     * Returns {@link Optional#empty()} so that stillValid() uses its default value of {@code true}.
     * This allows the menu to stay open even without a physical block at the position.
     */
    @Override
    @NotNull
    public <T> Optional<T> evaluate(final @NotNull BiFunction<Level, BlockPos, T> levelPosConsumer)
    {
        return Optional.empty();
    }

    /**
     * Actually executes the callback with the stored level and position.
     * This ensures items are properly returned to the player when the menu closes,
     * unlike {@link ContainerLevelAccess#NULL} which skips execution entirely.
     */
    @Override
    public void execute(final BiConsumer<Level, BlockPos> levelPosConsumer)
    {
        levelPosConsumer.accept(this.level, this.pos);
    }
}
