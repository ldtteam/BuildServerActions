package com.ldtteam.buildserveractions.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * Base interface for all mod commands.
 * Provides common utility methods for building Brigadier command structures.
 */
public interface ICommand
{
    /**
     * Builds the command structure for registration with the Brigadier dispatcher.
     *
     * @param context the command build context providing access to registries and other build-time resources.
     * @return the root literal argument builder for this command.
     */
    LiteralArgumentBuilder<CommandSourceStack> build(final CommandBuildContext context);

    /**
     * Creates a literal argument builder with the specified name.
     *
     * @param name the literal string that players must type to match this argument.
     * @return a new literal argument builder.
     */
    default LiteralArgumentBuilder<CommandSourceStack> literal(final String name)
    {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a required argument builder with the specified name and type.
     *
     * @param name the name of the argument, used for retrieval in command execution.
     * @param type the argument type that defines how the argument is parsed.
     * @param <T>  the type of value this argument produces.
     * @return a new required argument builder.
     */
    default <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(final String name, final ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
