package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.ldtteam.buildserveractions.plots.PlotSize;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.command.EnumArgument;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_NEW_CREATED;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_NEW_NO_OFFICIAL;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

public class CommandNewPlot implements ICommand
{
    private static final String ARGUMENT_PLOT_NAME       = "name";
    private static final String ARGUMENT_PLOT_SIZE       = "size";
    private static final String ARGUMENT_PLOT_DIRECTION  = "direction";
    private static final String ARGUMENT_PLOT_EDGE_BLOCK = "edge-block";

    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("new")
                .then(argument(ARGUMENT_PLOT_NAME, StringArgumentType.string())
                    .then(argument(ARGUMENT_PLOT_SIZE, EnumArgument.enumArgument(PlotSize.class))
                        .then(argument(ARGUMENT_PLOT_DIRECTION, EnumArgument.enumArgument(PlotDirection.class))
                            .executes(this::executeWithoutBlock)
                            .then(argument(ARGUMENT_PLOT_EDGE_BLOCK, BlockStateArgument.block(context))
                                .executes(this::executeWithBlock))))));
    }

    private int executeWithoutBlock(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return execute(context, Blocks.WHITE_CONCRETE.defaultBlockState());
    }

    private int executeWithBlock(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final BlockInput block = BlockStateArgument.getBlock(context, ARGUMENT_PLOT_EDGE_BLOCK);
        return execute(context, block.getState());
    }

    private int execute(final CommandContext<CommandSourceStack> context, final BlockState edgeBlock) throws CommandSyntaxException
    {
        final String name = context.getArgument(ARGUMENT_PLOT_NAME, String.class);
        final PlotSize size = context.getArgument(ARGUMENT_PLOT_SIZE, PlotSize.class);
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_DIRECTION, PlotDirection.class);

        if (direction.equals(PlotDirection.OFFICIAL) && !context.getSource().getPlayerOrException().hasPermissions(Commands.LEVEL_GAMEMASTERS))
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_NEW_NO_OFFICIAL));
            return 0;
        }

        final int newPlotId = context.getSource().getLevel().getData(PLOT_MANAGER).createPlot(context.getSource().getLevel(), name, size, direction, edgeBlock);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_NEW_CREATED, name, newPlotId), false);

        return 1;
    }
}
