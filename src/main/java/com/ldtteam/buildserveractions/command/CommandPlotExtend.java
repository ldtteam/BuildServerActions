package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.ldtteam.buildserveractions.plots.PlotExtendType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.server.command.EnumArgument;

import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

public class CommandPlotExtend implements ICommand
{
    private static final String ARGUMENT_PLOT_DIRECTION   = "direction";
    private static final String ARGUMENT_PLOT_ID          = "id";
    private static final String ARGUMENT_PLOT_EXTEND_TYPE = "extend-type";

    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("extend")
                .then(argument(ARGUMENT_PLOT_DIRECTION, EnumArgument.enumArgument(PlotDirection.class))
                    .then(argument(ARGUMENT_PLOT_ID, IntegerArgumentType.integer(1))
                        .then(argument(ARGUMENT_PLOT_EXTEND_TYPE, EnumArgument.enumArgument(PlotExtendType.class))
                            .executes(this::execute)))));
    }

    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);
        final int plotId = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_ID);
        final PlotExtendType extendType = context.getArgument(ARGUMENT_PLOT_EXTEND_TYPE, PlotExtendType.class);

        context.getSource().getLevel().getData(PLOT_MANAGER).extendPlot(context.getSource().getLevel(), direction, plotId, extendType);
        //context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_NEW_CREATED, name, newPlotId), false);

        return 1;
    }
}
