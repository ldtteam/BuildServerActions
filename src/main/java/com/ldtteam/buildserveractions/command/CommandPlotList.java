package com.ldtteam.buildserveractions.command;

import com.ldtteam.buildserveractions.plots.Plot;
import com.ldtteam.buildserveractions.plots.PlotDirection;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Map;

import static com.ldtteam.buildserveractions.registry.ModDataAttachmentTypes.PLOT_MANAGER;

public class CommandPlotList implements ICommand
{
    public LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context)
    {
        return literal("plots").then(literal("list").executes(this::execute));
    }

    private int execute(final CommandContext<CommandSourceStack> context)
    {
        final Map<Integer, Plot> plots = context.getSource().getLevel().getData(PLOT_MANAGER).getPlots(PlotDirection.OFFICIAL);

        context.getSource().sendSuccess(() -> Component.literal(String.valueOf(plots.size())), false);

        return 1;
    }
}
