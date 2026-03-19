package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.ldtteam.buildserveractions.plots.PlotManager;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_NEW_CREATED;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_NEW_NO_OFFICIAL;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.COMMAND_PLOT_NEW_SETUP_NOT_COMPLETE;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to create a new plot in the world.
 * <p>
 * Usage: {@code /plots new <name> <size> <direction> [edge-block]}
 * <p>
 * Creates a new plot with the specified parameters. Official plots require
 * gamemaster permissions. If no edge block is specified, white concrete is used.
 */
public class CommandPlotNew implements ICommand
{
    /**
     * Creates a new instance of the plot creation command.
     */
    public CommandPlotNew()
    {
    }

    /**
     * The argument name for the plot name.
     */
    private static final String ARGUMENT_PLOT_NAME = "name";

    /**
     * The argument name for the plot size.
     */
    private static final String ARGUMENT_PLOT_SIZE = "size";

    /**
     * The argument name for the plot direction.
     */
    private static final String ARGUMENT_PLOT_DIRECTION = "direction";

    /**
     * The argument name for the plot edge block.
     */
    private static final String ARGUMENT_PLOT_EDGE_BLOCK = "edge-block";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("new")
                .then(argument(ARGUMENT_PLOT_NAME, StringArgumentType.string())
                    .then(argument(ARGUMENT_PLOT_SIZE, StringRepresentableArgument.stringRepresentable(PlotSize.class))
                        .then(argument(ARGUMENT_PLOT_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                            .executes(this::executeWithoutBlock)
                            .then(argument(ARGUMENT_PLOT_EDGE_BLOCK, BlockStateArgument.block(context))
                                .executes(this::executeWithBlock))))));
    }

    /**
     * Executes the command without a custom edge block, using white concrete as default.
     *
     * @param context the command context.
     * @return 1 on success, 0 on failure.
     * @throws CommandSyntaxException if the command source is not a player.
     */
    private int executeWithoutBlock(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return execute(context, Blocks.WHITE_CONCRETE.defaultBlockState());
    }

    /**
     * Executes the command with a custom edge block specified by the player.
     *
     * @param context the command context.
     * @return 1 on success, 0 on failure.
     * @throws CommandSyntaxException if the command source is not a player.
     */
    private int executeWithBlock(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final BlockInput block = BlockStateArgument.getBlock(context, ARGUMENT_PLOT_EDGE_BLOCK);
        return execute(context, block.getState());
    }

    /**
     * Creates a new plot with the specified parameters.
     *
     * @param context   the command context containing all arguments.
     * @param edgeBlock the block state to use for the plot's edge/border.
     * @return 1 on success, 0 if the player lacks permission for official plots.
     * @throws CommandSyntaxException if the command source is not a player.
     */
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

        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        // Check if setup is complete
        if (!plotManager.isSetupComplete())
        {
            // Only gamemasters can run setup, others should be told to wait
            if (!context.getSource().hasPermission(Commands.LEVEL_GAMEMASTERS))
            {
                context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_NEW_SETUP_NOT_COMPLETE));
                return 0;
            }

            final String pendingCommand = reconstructCommand(name, size, direction, edgeBlock);
            CommandPlotSetup.startWithPendingCommand(context.getSource(), pendingCommand);
            return 1;
        }

        final Integer newPlotId = plotManager.createPlot(context.getSource().getLevel(), name, size, direction, edgeBlock);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_NEW_CREATED, name, newPlotId), false);

        return 1;
    }

    /**
     * Reconstructs the command string from the arguments for deferred execution.
     *
     * @param name      the plot name.
     * @param size      the plot size.
     * @param direction the plot direction.
     * @param edgeBlock the edge block state.
     * @return the reconstructed command string.
     */
    private String reconstructCommand(final String name, final PlotSize size, final PlotDirection direction, final BlockState edgeBlock)
    {
        final StringBuilder command = new StringBuilder();
        command.append("/plots new ");
        command.append("\"").append(name).append("\" ");
        command.append(size.getSerializedName()).append(" ");
        command.append(direction.getSerializedName());

        // Add edge block if not the default
        if (!edgeBlock.equals(Blocks.WHITE_CONCRETE.defaultBlockState()))
        {
            command.append(" ").append(BuiltInRegistries.BLOCK.getKey(edgeBlock.getBlock()));
        }

        return command.toString();
    }
}
