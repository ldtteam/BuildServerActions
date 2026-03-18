package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.ldtteam.buildserveractions.plots.PlotExtendType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to extend an existing plot by adding more building or decoration columns.
 * <p>
 * Usage: {@code /plots extend <direction> <id> <extend-type>}
 * <p>
 * Extends the specified plot by adding additional columns for buildings or decorations.
 */
public class CommandPlotExtend implements ICommand
{
    /**
     * Creates a new instance of the plot extend command.
     */
    public CommandPlotExtend()
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

    /**
     * The argument name for the extension type.
     */
    private static final String ARGUMENT_PLOT_EXTEND_TYPE = "extend-type";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("extend")
                .then(argument(ARGUMENT_PLOT_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                    .then(argument(ARGUMENT_PLOT_ID, IntegerArgumentType.integer(1))
                        .then(argument(ARGUMENT_PLOT_EXTEND_TYPE, StringRepresentableArgument.stringRepresentable(PlotExtendType.class))
                            .executes(this::execute)))));
    }

    /**
     * Executes the plot extend command.
     *
     * @param context the command context containing the direction, plot ID, and extend type.
     * @return 1 on success.
     */
    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);
        final int plotId = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_ID);
        final PlotExtendType extendType = context.getArgument(ARGUMENT_PLOT_EXTEND_TYPE, PlotExtendType.class);

        context.getSource().getLevel().getData(PLOT_MANAGER).extendPlot(context.getSource().getLevel(), direction, plotId, extendType);

        return 1;
    }
}
