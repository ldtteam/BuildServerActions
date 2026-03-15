package com.ldtteam.buildserveractions.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public interface ICommand
{
    LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context);

    default LiteralArgumentBuilder<CommandSourceStack> literal(final String name)
    {
        return LiteralArgumentBuilder.literal(name);
    }

    default <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(final String name, final ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
