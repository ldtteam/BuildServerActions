package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.Plot;
import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.ldtteam.buildserveractions.command.StringRepresentableArgument.stringRepresentable;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.*;
import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

/**
 * Command to list plots with pagination support.
 * <p>
 * Usage: {@code /plots list <official|unofficial> [page]}
 * <p>
 * Displays plots 10 at a time with their ID, name, and clickable coordinates
 * that teleport the player to the plot's anchor point when clicked.
 */
public class CommandPlotList implements ICommand
{
    /**
     * Creates a new instance of the plot list command.
     */
    public CommandPlotList()
    {
    }

    /**
     * The argument name for the plot direction.
     */
    private static final String ARGUMENT_DIRECTION = "direction";

    /**
     * The argument name for the page number.
     */
    private static final String ARGUMENT_PAGE = "page";

    /**
     * The number of plots to display per page.
     */
    private static final int PLOTS_PER_PAGE = 10;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots")
            .then(literal("list")
                .then(argument(ARGUMENT_DIRECTION, stringRepresentable(PlotDirection.class))
                    .executes(ctx -> execute(ctx, 1))
                    .then(argument(ARGUMENT_PAGE, IntegerArgumentType.integer(1))
                        .executes(ctx -> execute(ctx, IntegerArgumentType.getInteger(ctx, ARGUMENT_PAGE))))));
    }

    /**
     * Executes the plot list command, displaying a paginated list of plots.
     *
     * @param context the command context containing the source and level information.
     * @param page    the page number to display (1-indexed).
     * @return 1 on success, 0 on failure (no plots or invalid page).
     */
    private int execute(final CommandContext<CommandSourceStack> context, final int page)
    {
        final PlotDirection direction = context.getArgument(ARGUMENT_DIRECTION, PlotDirection.class);
        final Map<Integer, Plot> plotMap = context.getSource().getLevel().getData(PLOT_MANAGER).getPlots(direction);
        final String directionName = direction.getSerializedName();

        if (plotMap.isEmpty())
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_LIST_EMPTY));
            return 0;
        }

        final List<Plot> plots = new ArrayList<>(plotMap.values());
        plots.sort(Comparator.comparingInt(Plot::id));

        final int totalPlots = plots.size();
        final int totalPages = (int) Math.ceil(totalPlots / (double) PLOTS_PER_PAGE);

        if (page > totalPages)
        {
            context.getSource().sendFailure(Component.translatable(COMMAND_PLOT_LIST_INVALID_PAGE, page, totalPages));
            return 0;
        }

        final int startIndex = (page - 1) * PLOTS_PER_PAGE;
        final int endIndex = Math.min(startIndex + PLOTS_PER_PAGE, totalPlots);

        // Header
        context.getSource().sendSuccess(() -> Component.translatable(COMMAND_PLOT_LIST_HEADER, page, totalPages, totalPlots)
            .withStyle(ChatFormatting.GOLD), false);

        // Plot entries
        for (int i = startIndex; i < endIndex; i++)
        {
            final Plot plot = plots.get(i);
            final BlockPos anchor = plot.anchorPoint();

            final MutableComponent coordsComponent = Component.literal("[" + anchor.getX() + ", " + anchor.getY() + ", " + anchor.getZ() + "]")
                .withStyle(style -> style
                    .withColor(ChatFormatting.AQUA)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots teleport " + directionName + " " + plot.id()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(COMMAND_PLOT_LIST_CLICK_TO_TELEPORT))));

            final MutableComponent entry = Component.translatable(COMMAND_PLOT_LIST_ENTRY, plot.id(), plot.name())
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(" "))
                .append(coordsComponent);

            context.getSource().sendSuccess(() -> entry, false);
        }

        // Navigation footer
        if (totalPages > 1)
        {
            final MutableComponent navigation = Component.literal("");

            if (page > 1)
            {
                navigation.append(Component.literal("[<< ")
                    .append(Component.translatable(COMMAND_PLOT_LIST_PREV))
                    .append("]")
                    .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots list " + directionName + " " + (page - 1)))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(COMMAND_PLOT_LIST_PAGE, page - 1)))));
            }

            if (page > 1 && page < totalPages)
            {
                navigation.append(Component.literal(" | ").withStyle(ChatFormatting.GRAY));
            }

            if (page < totalPages)
            {
                navigation.append(Component.literal("[")
                    .append(Component.translatable(COMMAND_PLOT_LIST_NEXT))
                    .append(" >>]")
                    .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plots list " + directionName + " " + (page + 1)))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(COMMAND_PLOT_LIST_PAGE, page + 1)))));
            }

            context.getSource().sendSuccess(() -> navigation, false);
        }

        return 1;
    }
}
