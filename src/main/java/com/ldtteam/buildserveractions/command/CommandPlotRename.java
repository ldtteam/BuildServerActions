package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_RENAME_NOT_FOUND;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_RENAME_SUCCESS;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to rename an existing plot.
 * <p>
 * Usage: {@code /plots rename <direction> <id> <name>}
 * <p>
 * Renames the specified plot to the new name provided.
 */
public class CommandPlotRename implements ICommand
{
    /**
     * Creates a new instance of the plot rename command.
     */
    public CommandPlotRename()
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
     * The argument name for the new plot name.
     */
    private static final String ARGUMENT_PLOT_NAME = "name";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("rename")
                .then(argument(ARGUMENT_PLOT_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                    .then(argument(ARGUMENT_PLOT_ID, IntegerArgumentType.integer(1))
                        .then(argument(ARGUMENT_PLOT_NAME, StringArgumentType.string())
                            .executes(this::execute)))));
    }

    /**
     * Executes the plot rename command.
     *
     * @param context the command context containing the direction, plot ID, and new name.
     * @return 1 on success, 0 if the plot was not found.
     */
    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);
        final int plotId = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_ID);
        final String newName = StringArgumentType.getString(context, ARGUMENT_PLOT_NAME);

        final boolean success = context.getSource().getLevel().getData(PLOT_MANAGER).renamePlot(direction, plotId, newName);

        if (success)
        {
            context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_RENAME_SUCCESS, plotId, newName), false);
            return 1;
        }
        else
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_RENAME_NOT_FOUND, plotId, direction.name()));
            return 0;
        }
    }
}
