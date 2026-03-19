package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.ldtteam.buildserveractions.plots.PlotManager;
import com.ldtteam.buildserveractions.plots.PlotSettings;
import com.ldtteam.buildserveractions.plots.PlotSetupManager;
import com.ldtteam.buildserveractions.plots.PlotSetupManager.SetupSession;
import com.ldtteam.buildserveractions.plots.PlotSetupManager.SetupStep;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Guided setup command for configuring plot base settings.
 * <p>
 * Usage: {@code /plots setup} to start or continue setup
 * <p>
 * Walks the user through configuring all required settings before plots can be created.
 * Settings are locked after initial setup is complete.
 */
public class CommandPlotSetup implements ICommand
{
    private static final String ARGUMENT_VALUE = "value";
    private static final String ARGUMENT_BLOCK = "block";

    /**
     * Creates a new instance of the plot setup command.
     */
    public CommandPlotSetup()
    {
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("setup")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS)
                    && !source.getLevel().getData(PLOT_MANAGER).isSetupComplete())
                .executes(this::executeStart)
                .then(literal("center-road")
                    .then(argument(ARGUMENT_VALUE, IntegerArgumentType.integer(1))
                        .executes(ctx -> executeSetInt(ctx, SetupStep.CENTER_ROAD_SPACING))))
                .then(literal("plot-road")
                    .then(argument(ARGUMENT_VALUE, IntegerArgumentType.integer(1))
                        .executes(ctx -> executeSetInt(ctx, SetupStep.PLOT_ROAD_SPACING))))
                .then(literal("north-offset")
                    .then(argument(ARGUMENT_VALUE, IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetInt(ctx, SetupStep.NORTH_OFFSET))))
                .then(literal("south-offset")
                    .then(argument(ARGUMENT_VALUE, IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetInt(ctx, SetupStep.SOUTH_OFFSET))))
                .then(literal("road-block")
                    .then(argument(ARGUMENT_BLOCK, BlockStateArgument.block(context))
                        .executes(this::executeSetBlock)))
                .then(literal("y-level")
                    .then(argument(ARGUMENT_VALUE, IntegerArgumentType.integer(-64, 320))
                        .executes(ctx -> executeSetInt(ctx, SetupStep.Y_LEVEL))))
                .then(literal("confirm")
                    .executes(this::executeConfirm))
                .then(literal("cancel")
                    .executes(this::executeCancel))
                .then(literal("restart")
                    .executes(this::executeRestart)));
    }

    /**
     * Starts the setup process or shows the current step if already in progress.
     */
    private int executeStart(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        // Check if setup is already complete
        if (plotManager.isSetupComplete())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            return 0;
        }

        final PlotSetupManager setupManager = PlotSetupManager.getInstance();
        SetupSession session = setupManager.getSession(player.getUUID());

        // Start new session if none exists
        if (session == null)
        {
            session = setupManager.startSession(player.getUUID(), null);
            context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_STARTED)
                .withStyle(ChatFormatting.GREEN), false);
        }

        // Show current step
        sendStepPrompt(context.getSource(), session);
        return 1;
    }

    /**
     * Starts setup with a pending command to execute after completion.
     *
     * @param context        the command source stack.
     * @param pendingCommand the command to execute after setup.
     */
    public static void startWithPendingCommand(final CommandSourceStack context, final String pendingCommand) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getPlayerOrException();
        final PlotSetupManager setupManager = PlotSetupManager.getInstance();

        SetupSession session = setupManager.getSession(player.getUUID());
        if (session == null)
        {
            session = setupManager.startSession(player.getUUID(), pendingCommand);
            context.sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_STARTED)
                .withStyle(ChatFormatting.GREEN), false);
            context.sendSuccess(() -> Component.translatable(COMMAND_PLOT_NEW_SETUP_REQUIRED)
                .withStyle(ChatFormatting.YELLOW), false);
        }

        sendStepPrompt(context, session);
    }

    /**
     * Sets an integer value for the current step.
     */
    private int executeSetInt(final CommandContext<CommandSourceStack> context, final SetupStep expectedStep) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        if (plotManager.isSetupComplete())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            return 0;
        }

        final PlotSetupManager setupManager = PlotSetupManager.getInstance();
        final SetupSession session = setupManager.getSession(player.getUUID());

        if (session == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_NO_SESSION));
            return 0;
        }

        if (session.getCurrentStep() != expectedStep)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_WRONG_STEP));
            sendStepPrompt(context.getSource(), session);
            return 0;
        }

        final int value = IntegerArgumentType.getInteger(context, ARGUMENT_VALUE);

        switch (expectedStep)
        {
            case CENTER_ROAD_SPACING -> session.setCenterRoadSpacing(value);
            case PLOT_ROAD_SPACING -> session.setPlotRoadSpacing(value);
            case NORTH_OFFSET -> session.setNorthOffset(value);
            case SOUTH_OFFSET -> session.setSouthOffset(value);
            case Y_LEVEL -> session.setYLevel(value);
            default ->
            {
                return 0;
            }
        }

        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_VALUE_SET, value)
            .withStyle(ChatFormatting.GREEN), false);

        session.advanceStep();
        sendStepPrompt(context.getSource(), session);
        return 1;
    }

    /**
     * Sets the road block for the current step.
     */
    private int executeSetBlock(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        if (plotManager.isSetupComplete())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            return 0;
        }

        final PlotSetupManager setupManager = PlotSetupManager.getInstance();
        final SetupSession session = setupManager.getSession(player.getUUID());

        if (session == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_NO_SESSION));
            return 0;
        }

        if (session.getCurrentStep() != SetupStep.ROAD_BLOCK)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_WRONG_STEP));
            sendStepPrompt(context.getSource(), session);
            return 0;
        }

        final BlockInput block = BlockStateArgument.getBlock(context, ARGUMENT_BLOCK);
        final BlockState blockState = block.getState();
        session.setRoadBlock(blockState);

        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_VALUE_SET,
            blockState.getBlock().getName()).withStyle(ChatFormatting.GREEN), false);

        session.advanceStep();
        sendStepPrompt(context.getSource(), session);
        return 1;
    }

    /**
     * Confirms and applies all settings.
     */
    private int executeConfirm(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        if (plotManager.isSetupComplete())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            return 0;
        }

        final PlotSetupManager setupManager = PlotSetupManager.getInstance();
        final SetupSession session = setupManager.getSession(player.getUUID());

        if (session == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_NO_SESSION));
            return 0;
        }

        if (session.getCurrentStep() != SetupStep.CONFIRM)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_WRONG_STEP));
            sendStepPrompt(context.getSource(), session);
            return 0;
        }

        // Apply settings
        final PlotSettings settings = session.buildSettings();
        final boolean applied = plotManager.setBaseSettings(settings);

        if (!applied)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            setupManager.removeSession(player.getUUID());
            return 0;
        }

        // Apply direction offsets
        plotManager.setDirectionOffset(PlotDirection.OFFICIAL, session.getNorthOffset());
        plotManager.setDirectionOffset(PlotDirection.UNOFFICIAL, session.getSouthOffset());

        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_COMPLETE)
            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD), true);

        // Execute pending command if any
        final String pendingCommand = session.getPendingCommand();

        // Clear all sessions since setup is now locked - no one else can complete it
        setupManager.clearAllSessions();

        if (pendingCommand != null && !pendingCommand.isEmpty())
        {
            context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_EXECUTING_PENDING)
                .withStyle(ChatFormatting.GRAY), false);
            player.getServer().getCommands().performPrefixedCommand(context.getSource(), pendingCommand);
        }

        return 1;
    }

    /**
     * Cancels the setup process.
     */
    private int executeCancel(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotSetupManager setupManager = PlotSetupManager.getInstance();

        if (!setupManager.hasSession(player.getUUID()))
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_NO_SESSION));
            return 0;
        }

        setupManager.removeSession(player.getUUID());
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_CANCELLED)
            .withStyle(ChatFormatting.YELLOW), false);
        return 1;
    }

    /**
     * Restarts the setup from the beginning, keeping current values.
     */
    private int executeRestart(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final PlotManager plotManager = context.getSource().getLevel().getData(PLOT_MANAGER);

        if (plotManager.isSetupComplete())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_ALREADY_COMPLETE));
            return 0;
        }

        final PlotSetupManager setupManager = PlotSetupManager.getInstance();
        final SetupSession session = setupManager.getSession(player.getUUID());

        if (session == null)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_SETUP_NO_SESSION));
            return 0;
        }

        session.restartFromBeginning();
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_RESTARTED)
            .withStyle(ChatFormatting.YELLOW), false);
        sendStepPrompt(context.getSource(), session);
        return 1;
    }

    /**
     * Sends the prompt for the current setup step.
     */
    private static void sendStepPrompt(final CommandSourceStack source, final SetupSession session)
    {
        final SetupStep step = session.getCurrentStep();
        final int stepNum = step.getStepNumber();
        final int totalSteps = SetupStep.getTotalSteps();

        // Header
        source.sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_STEP_HEADER, stepNum, totalSteps)
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        // Step description (without the "Default: X" part, we'll show that separately)
        final String descriptionKey = getDescriptionKey(step);
        if (descriptionKey != null)
        {
            source.sendSuccess(() -> Component.translatable(descriptionKey)
                .withStyle(ChatFormatting.GRAY), false);
        }

        // For CONFIRM step, show summary and confirm button
        if (step == SetupStep.CONFIRM)
        {
            sendConfirmationSummary(source, session);
            return;
        }

        // Show default and current value on separate lines
        final Object originalDefault = step.getDefaultValue();
        final Object currentValue = getCurrentValue(session, step);
        final String currentValueStr = formatCurrentValue(currentValue);
        final Component currentDisplayValue = formatDisplayValue(currentValue);
        final Component defaultDisplayValue = formatDisplayValue(originalDefault);
        final boolean hasCustomValue = !currentValueStr.equals(formatCurrentValue(originalDefault));

        // Default value line (dark aqua)
        source.sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_DEFAULT_VALUE, defaultDisplayValue)
            .withStyle(ChatFormatting.DARK_AQUA), false);

        // Current value line (yellow) - only show if different from default
        if (hasCustomValue)
        {
            source.sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_CURRENT_VALUE, currentDisplayValue)
                .withStyle(ChatFormatting.YELLOW), false);
        }

        // Show clickable buttons
        final String commandBase = "/plots setup " + step.getCommandName() + " ";

        // [Set Value] button - suggests command
        final MutableComponent setValueButton = Component.literal("[")
            .append(Component.translatable(COMMAND_PLOT_SETUP_SET_VALUE))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.AQUA)
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandBase))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal(commandBase))));

        // [Keep Current] or [Use Default] button - runs command with current value
        final MutableComponent keepButton;
        final String keepCommand = commandBase + currentValueStr;
        keepButton = Component.literal(" [")
            .append(Component.translatable(hasCustomValue ? COMMAND_PLOT_SETUP_KEEP_CURRENT : COMMAND_PLOT_SETUP_USE_DEFAULT, currentDisplayValue))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, keepCommand))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal(keepCommand))));

        // [Cancel] button
        final MutableComponent cancelButton = Component.literal(" [")
            .append(Component.translatable(COMMAND_PLOT_SETUP_CANCEL_BUTTON))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.RED)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots setup cancel"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal("/plots setup cancel"))));

        source.sendSuccess(() -> setValueButton.append(keepButton).append(cancelButton), false);
    }

    /**
     * Sends the confirmation summary showing all configured values.
     */
    private static void sendConfirmationSummary(final CommandSourceStack source, final SetupSession session)
    {
        source.sendSuccess(() -> Component.translatable(COMMAND_PLOT_SETUP_CONFIRM_PROMPT)
            .withStyle(ChatFormatting.YELLOW), false);

        // Show configured values
        source.sendSuccess(() -> Component.literal("  Center Road: " + session.getCenterRoadSpacing())
            .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("  Plot Road: " + session.getPlotRoadSpacing())
            .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("  North Offset: " + session.getNorthOffset())
            .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("  South Offset: " + session.getSouthOffset())
            .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("  Road Block: ")
            .append(session.getRoadBlock().getBlock().getName())
            .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("  Y Level: " + session.getYLevel())
            .withStyle(ChatFormatting.GRAY), false);

        // Confirm button
        final MutableComponent confirmButton = Component.literal("[")
            .append(Component.translatable(COMMAND_PLOT_SETUP_CONFIRM_BUTTON))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.GREEN)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots setup confirm"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal("/plots setup confirm"))));

        // Restart button
        final MutableComponent restartButton = Component.literal(" [")
            .append(Component.translatable(COMMAND_PLOT_SETUP_RESTART_BUTTON))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.YELLOW)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots setup restart"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal("/plots setup restart"))));

        // Cancel button
        final MutableComponent cancelButton = Component.literal(" [")
            .append(Component.translatable(COMMAND_PLOT_SETUP_CANCEL_BUTTON))
            .append("]")
            .withStyle(style -> style
                .withColor(ChatFormatting.RED)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots setup cancel"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.literal("/plots setup cancel"))));

        source.sendSuccess(() -> confirmButton.append(restartButton).append(cancelButton), false);
    }

    /**
     * Gets the translation key for a step's description.
     */
    private static String getDescriptionKey(final SetupStep step)
    {
        return switch (step)
        {
            case CENTER_ROAD_SPACING -> COMMAND_PLOT_SETUP_DESC_CENTER_ROAD;
            case PLOT_ROAD_SPACING -> COMMAND_PLOT_SETUP_DESC_PLOT_ROAD;
            case NORTH_OFFSET -> COMMAND_PLOT_SETUP_DESC_NORTH_OFFSET;
            case SOUTH_OFFSET -> COMMAND_PLOT_SETUP_DESC_SOUTH_OFFSET;
            case ROAD_BLOCK -> COMMAND_PLOT_SETUP_DESC_ROAD_BLOCK;
            case Y_LEVEL -> COMMAND_PLOT_SETUP_DESC_Y_LEVEL;
            case CONFIRM -> null;
        };
    }

    /**
     * Gets the current value for a step from the session.
     */
    private static Object getCurrentValue(final SetupSession session, final SetupStep step)
    {
        return switch (step)
        {
            case CENTER_ROAD_SPACING -> session.getCenterRoadSpacing();
            case PLOT_ROAD_SPACING -> session.getPlotRoadSpacing();
            case NORTH_OFFSET -> session.getNorthOffset();
            case SOUTH_OFFSET -> session.getSouthOffset();
            case ROAD_BLOCK -> session.getRoadBlock();
            case Y_LEVEL -> session.getYLevel();
            case CONFIRM -> null;
        };
    }

    /**
     * Formats the current value for use in a command string.
     */
    private static String formatCurrentValue(final Object value)
    {
        if (value instanceof BlockState blockState)
        {
            return BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
        }
        return String.valueOf(value);
    }

    /**
     * Formats a value for display to the user.
     */
    private static Component formatDisplayValue(final Object value)
    {
        if (value instanceof BlockState blockState)
        {
            return blockState.getBlock().getName();
        }
        return value != null ? Component.literal(String.valueOf(value)) : Component.empty();
    }
}
