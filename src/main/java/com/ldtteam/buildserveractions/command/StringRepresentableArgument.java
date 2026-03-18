package com.ldtteam.buildserveractions.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A command argument type for enums that implement {@link StringRepresentable}.
 * <p>
 * Unlike NeoForge's {@link EnumArgument}, this uses {@link StringRepresentable#getSerializedName()}
 * for parsing and suggestions, allowing lowercase enum values in commands.
 *
 * @param <T> the enum type, which must implement {@link StringRepresentable}.
 */
public class StringRepresentableArgument<T extends Enum<T> & StringRepresentable> implements ArgumentType<T>
{
    private static final DynamicCommandExceptionType INVALID_VALUE = new DynamicCommandExceptionType(value -> Component.literal("Invalid value: " + value));

    private final Class<T> enumClass;

    private StringRepresentableArgument(final Class<T> enumClass)
    {
        this.enumClass = enumClass;
    }

    /**
     * Creates a new argument type for the given enum class.
     *
     * @param enumClass the enum class, which must implement {@link StringRepresentable}.
     * @param <R>       the enum type.
     * @return the argument type.
     */
    public static <R extends Enum<R> & StringRepresentable> StringRepresentableArgument<R> stringRepresentable(final Class<R> enumClass)
    {
        return new StringRepresentableArgument<>(enumClass);
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException
    {
        final String input = reader.readUnquotedString();
        for (final T constant : enumClass.getEnumConstants())
        {
            if (constant.getSerializedName().equals(input))
            {
                return constant;
            }
        }
        throw INVALID_VALUE.createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        return SharedSuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(StringRepresentable::getSerializedName), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return Arrays.stream(enumClass.getEnumConstants()).map(StringRepresentable::getSerializedName).collect(Collectors.toList());
    }

    /**
     * Argument type info for serialization and synchronization with clients.
     *
     * @param <T> the enum type.
     */
    public static class Info<T extends Enum<T> & StringRepresentable> implements ArgumentTypeInfo<StringRepresentableArgument<T>, Info<T>.Template>
    {
        /**
         * Default constructor.
         */
        public Info()
        {
        }

        @Override
        public void serializeToNetwork(final Template template, final FriendlyByteBuf buffer)
        {
            buffer.writeUtf(template.enumClass.getName());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Template deserializeFromNetwork(final FriendlyByteBuf buffer)
        {
            try
            {
                final String name = buffer.readUtf();
                return new Template((Class<T>) Class.forName(name));
            }
            catch (final ClassNotFoundException e)
            {
                return null;
            }
        }

        @Override
        public void serializeToJson(final Template template, final JsonObject json)
        {
            json.addProperty("enum", template.enumClass.getName());
        }

        @Override
        @NotNull
        public Template unpack(final StringRepresentableArgument<T> argument)
        {
            return new Template(argument.enumClass);
        }

        /**
         * Template for creating argument instances.
         */
        public class Template implements ArgumentTypeInfo.Template<StringRepresentableArgument<T>>
        {
            final Class<T> enumClass;

            Template(final Class<T> enumClass)
            {
                this.enumClass = enumClass;
            }

            @Override
            @NotNull
            public StringRepresentableArgument<T> instantiate(final @NotNull CommandBuildContext context)
            {
                return new StringRepresentableArgument<>(this.enumClass);
            }

            @Override
            @NotNull
            public ArgumentTypeInfo<StringRepresentableArgument<T>, ?> type()
            {
                return Info.this;
            }
        }
    }
}
