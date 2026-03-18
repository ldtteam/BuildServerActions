package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to view and modify global plot settings.
 * <p>
 * Usage: {@code /plots settings <subcommand> [value]}
 * <p>
 * Subcommands:
 * <ul>
 *   <li>{@code offset <direction> [value]} - Get/set the offset for a direction</li>
 *   <li>{@code y-level [value]} - Get/set the Y level for plots</li>
 *   <li>{@code center-road-spacing [value]} - Get/set the center road spacing</li>
 *   <li>{@code plot-road-spacing [value]} - Get/set the plot road spacing</li>
 *   <li>{@code road-block [block]} - Get/set the road block type</li>
 * </ul>
 * Requires owner-level permissions.
 */
public class CommandPlotSettings implements ICommand
{
    /**
     * Creates a new instance of the plot settings command.
     */
    public CommandPlotSettings()
    {
    }

    /**
     * The argument name for the plot offset direction.
     */
    private static final String ARGUMENT_PLOT_OFFSET_DIRECTION = "direction";

    /**
     * The argument name for the offset value.
     */
    private static final String ARGUMENT_PLOT_OFFSET_DIRECTION_OFFSET = "offset";

    /**
     * The argument name for spacing values.
     */
    private static final String ARGUMENT_PLOT_SPACING = "spacing";

    /**
     * The argument name for the Y level.
     */
    private static final String ARGUMENT_PLOT_Y_LEVEL = "y-level";

    /**
     * The argument name for the road block.
     */
    private static final String ARGUMENT_PLOT_ROAD_BLOCK = "road-block";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("settings")
                .requires(source -> source.hasPermission(Commands.LEVEL_OWNERS))
                .then(literal("offset")
                    .then(argument(ARGUMENT_PLOT_OFFSET_DIRECTION, StringRepresentableArgument.stringRepresentable(PlotDirection.class))
                        .executes(this::executeGetOffset)
                        .then(argument(ARGUMENT_PLOT_OFFSET_DIRECTION_OFFSET, IntegerArgumentType.integer(0))
                            .executes(this::executeSetOffset))))
                .then(literal("y-level")
                    .executes(this::executeGetYLevel)
                    .then(argument(ARGUMENT_PLOT_Y_LEVEL, IntegerArgumentType.integer())
                        .executes(this::executeSetYLevel)))
                .then(literal("center-road-spacing")
                    .executes(this::executeGetCenterRoadSpacing)
                    .then(argument(ARGUMENT_PLOT_SPACING, IntegerArgumentType.integer(0))
                        .executes(this::executeSetCenterRoadSpacing)))
                .then(literal("plot-road-spacing")
                    .executes(this::executeGetPlotRoadSpacing)
                    .then(argument(ARGUMENT_PLOT_SPACING, IntegerArgumentType.integer(0))
                        .executes(this::executeSetPlotRoadSpacing)))
                .then(literal("road-block")
                    .executes(this::executeGetRoadBlock)
                    .then(argument(ARGUMENT_PLOT_ROAD_BLOCK, BlockStateArgument.block(context))
                        .executes(this::executeSetRoadBlock))));
    }

    /**
     * Gets the current offset for the specified direction.
     *
     * @param context the command context containing the direction argument.
     * @return 1 on success.
     */
    private int executeGetOffset(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_OFFSET_DIRECTION, PlotDirection.class);
        final int offset = context.getSource().getLevel().getData(PLOT_MANAGER).getPlotOffset(direction);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_OFFSET_GET, direction.name(), offset), false);

        return 1;
    }

    /**
     * Sets the offset for the specified direction.
     *
     * @param context the command context containing the direction and offset arguments.
     * @return 1 on success.
     */
    private int executeSetOffset(final CommandContext<CommandSourceStack> context)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_PLOT_OFFSET_DIRECTION, PlotDirection.class);
        final int offset = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_OFFSET_DIRECTION_OFFSET);
        context.getSource().getLevel().getData(PLOT_MANAGER).setPlotOffset(direction, offset);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_OFFSET_SET, direction.name(), offset), false);

        return 1;
    }

    /**
     * Gets the current center road spacing.
     *
     * @param context the command context.
     * @return 1 on success.
     */
    private int executeGetCenterRoadSpacing(final CommandContext<CommandSourceStack> context)
    {
        final int spacing = context.getSource().getLevel().getData(PLOT_MANAGER).getCenterRoadSpacing();
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_CENTER_ROAD_SPACING_GET, spacing), false);

        return 1;
    }

    /**
     * Sets the center road spacing.
     *
     * @param context the command context containing the spacing argument.
     * @return 1 on success.
     */
    private int executeSetCenterRoadSpacing(final CommandContext<CommandSourceStack> context)
    {
        final int spacing = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_SPACING);
        context.getSource().getLevel().getData(PLOT_MANAGER).setCenterRoadSpacing(spacing);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_CENTER_ROAD_SPACING_SET, spacing), false);

        return 1;
    }

    /**
     * Gets the current plot road spacing.
     *
     * @param context the command context.
     * @return 1 on success.
     */
    private int executeGetPlotRoadSpacing(final CommandContext<CommandSourceStack> context)
    {
        final int spacing = context.getSource().getLevel().getData(PLOT_MANAGER).getPlotRoadSpacing();
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_PLOT_ROAD_SPACING_GET, spacing), false);

        return 1;
    }

    /**
     * Sets the plot road spacing.
     *
     * @param context the command context containing the spacing argument.
     * @return 1 on success.
     */
    private int executeSetPlotRoadSpacing(final CommandContext<CommandSourceStack> context)
    {
        final int spacing = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_SPACING);
        context.getSource().getLevel().getData(PLOT_MANAGER).setPlotRoadSpacing(spacing);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_PLOT_ROAD_SPACING_SET, spacing), false);

        return 1;
    }

    /**
     * Gets the current Y level for plots.
     *
     * @param context the command context.
     * @return 1 on success.
     */
    private int executeGetYLevel(final CommandContext<CommandSourceStack> context)
    {
        final int yLevel = context.getSource().getLevel().getData(PLOT_MANAGER).getPlotYLevel();
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_Y_LEVEL_GET, yLevel), false);

        return 1;
    }

    /**
     * Sets the Y level for plots.
     *
     * @param context the command context containing the y-level argument.
     * @return 1 on success.
     */
    private int executeSetYLevel(final CommandContext<CommandSourceStack> context)
    {
        final int yLevel = IntegerArgumentType.getInteger(context, ARGUMENT_PLOT_Y_LEVEL);
        context.getSource().getLevel().getData(PLOT_MANAGER).setPlotYLevel(yLevel);
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_Y_LEVEL_SET, yLevel), false);

        return 1;
    }

    /**
     * Gets the current road block type.
     *
     * @param context the command context.
     * @return 1 on success.
     */
    private int executeGetRoadBlock(final CommandContext<CommandSourceStack> context)
    {
        final BlockState roadBlock = context.getSource().getLevel().getData(PLOT_MANAGER).getRoadBlock();
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_ROAD_BLOCK_GET, roadBlock.getBlock().getName()), false);

        return 1;
    }

    /**
     * Sets the road block type.
     *
     * @param context the command context containing the road-block argument.
     * @return 1 on success.
     */
    private int executeSetRoadBlock(final CommandContext<CommandSourceStack> context)
    {
        final BlockInput roadBlock = BlockStateArgument.getBlock(context, ARGUMENT_PLOT_ROAD_BLOCK);
        context.getSource().getLevel().getData(PLOT_MANAGER).setRoadBlock(roadBlock.getState());
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETTINGS_ROAD_BLOCK_SET, roadBlock.getState().getBlock().getName()), false);

        return 1;
    }
}
