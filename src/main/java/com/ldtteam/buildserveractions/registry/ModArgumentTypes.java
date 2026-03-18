package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.command.StringRepresentableArgument;
import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for custom command argument types.
 */
@SuppressWarnings("unused")
public class ModArgumentTypes
{
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> DEFERRED_REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Constants.MOD_ID);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, StringRepresentableArgument.Info<?>> STRING_REPRESENTABLE =
        DEFERRED_REGISTER.register("string_representable", () -> ArgumentTypeInfos.registerByClass(StringRepresentableArgument.class, new StringRepresentableArgument.Info()));
}
