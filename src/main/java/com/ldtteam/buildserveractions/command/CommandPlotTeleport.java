package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.Plot;
import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;

import java.util.EnumSet;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to teleport to a plot's anchor point.
 * <p>
 * Usage: {@code /plots teleport <direction> <id>}
 * <p>
 * Teleports the player to the specified plot's anchor point and rotates them
 * to face the plot direction.
 */
public class CommandPlotTeleport implements ICommand
{
    /**
     * Creates a new instance of the plot teleport command.
     */
    public CommandPlotTeleport()
    {
    }

    /**
     * The argument name for the plot direction.
     */
    private static final String ARGUMENT_PLOT_DIRECTION = "direction";

    /**
     * The argument name for the plot ID.
     */
    private static final String ARGUMENT_PLOT_ID = "id";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("teleport")
                .then(argument(ARGUMENT_PLOT_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                    .then(argument(ARGUMENT_PLOT_ID, IntegerArgumentType.integer(1))
                        .executes(this::execute))));
    }

    /**
     * Executes the plot teleport command.
     *
     * @param context the command context containing the direction and plot ID.
     * @return 1 on success, 0 if the plot was not found or no entity is executing the command.
     */
    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);
        final int plotId = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_ID);

        final Entity entity = context.getSource().getEntity();
        if (entity == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_TELEPORT_NO_ENTITY));
            return 0;
        }

        final Plot plot = context.getSource().getLevel().getData(PLOT_MANAGER).getPlots(direction).get(plotId);
        if (plot == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_TELEPORT_NOT_FOUND, plotId, direction.name()));
            return 0;
        }

        final BlockPos anchor = plot.anchorPoint();
        final double x = anchor.getX() + 0.5;
        final double y = anchor.above().getY();
        final double z = anchor.getZ() + 0.5;

        final float yaw = direction.getDirection().toYRot();
        final float pitch = 0.0f;

        entity.teleportTo(
            context.getSource().getLevel(),
            x,
            y,
            z,
            EnumSet.noneOf(RelativeMovement.class),
            yaw,
            pitch
        );

        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_TELEPORT_SUCCESS, plot.name()), false);
        return 1;
    }
}
