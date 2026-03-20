package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.Plot;
import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to regenerate an existing plot by redrawing all its sections.
 * <p>
 * Usage: {@code /plots regenerate <direction> <id>}
 * <p>
 * Redraws all sections of the specified plot including the center road,
 * buildings area, decorations area, and all building/decoration columns.
 */
public class CommandPlotRegenerate implements ICommand
{
    /**
     * Creates a new instance of the plot regenerate command.
     */
    public CommandPlotRegenerate()
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
            .then(literal("regenerate")
                .then(argument(ARGUMENT_PLOT_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                    .then(argument(ARGUMENT_PLOT_ID, IntegerArgumentType.integer(1))
                        .executes(this::execute))));
    }

    /**
     * Executes the plot regenerate command.
     *
     * @param context the command context containing the direction and plot ID.
     * @return 1 on success, 0 if the plot was not found.
     */
    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);
        final int plotId = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_ID);

        final Plot plot = context.getSource().getLevel().getData(PLOT_MANAGER).getPlots(direction).get(plotId);
        if (plot == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_REGENERATE_NOT_FOUND, plotId, direction.name()));
            return 0;
        }

        final boolean success = context.getSource().getLevel().getData(PLOT_MANAGER).regeneratePlot(context.getSource().getLevel(), direction, plotId);
        if (success)
        {
            context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_REGENERATE_SUCCESS, plotId, plot.name()), false);
            return 1;
        }

        context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_REGENERATE_NOT_FOUND, plotId, direction.name()));
        return 0;
    }
}
