package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks;
import com.ldtteam.buildserveractions.widget.WidgetGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.function.Consumer;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks.FLIGHT_SPEED_MULTIPLIER_KEY;
import static com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks.TIME_KEY;

/**
 * Registry for widget groups.
 */
@SuppressWarnings({"java:S1104", "java:S1444", "unused"})
public class ModWidgetGroups
{
    public static final ResourceKey<Registry<WidgetGroup>> REGISTRY_KEY      = ResourceKey.createRegistryKey(modId("widget-groups"));
    public static final DeferredRegister<WidgetGroup>      DEFERRED_REGISTER = DeferredRegister.create(REGISTRY_KEY, Constants.MOD_ID);

    public static final ResourceLocation GROUP_GAME_MODES_ID = modId("group-game-modes");
    public static final ResourceLocation GROUP_TIME_ID       = modId("group-time");
    public static final ResourceLocation GROUP_SPEED_ID      = modId("group-speed");
    public static final ResourceLocation GROUP_ITEMS_ID      = modId("group-items");
    public static final ResourceLocation GROUP_WINDOWS_ID    = modId("group-windows");

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_GAME_MODES =
        register(GROUP_GAME_MODES_ID, builder -> builder.setSorter(new GameModeWidgetCallbacks.GameModeSorter()));

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_TIME =
        register(GROUP_TIME_ID, builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(TIME_KEY, Number.class).intValue())));

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_SPEED =
        register(GROUP_SPEED_ID, builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(FLIGHT_SPEED_MULTIPLIER_KEY, Number.class).intValue())));

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_ITEMS = register(GROUP_ITEMS_ID);

    public static final DeferredHolder<WidgetGroup, WidgetGroup> GROUP_WINDOWS = register(GROUP_WINDOWS_ID);

    /**
     * Register a widget group with an ID using default configuration.
     *
     * @param groupId the resource location ID for the group.
     * @return the registry object.
     */
    public static DeferredHolder<WidgetGroup, WidgetGroup> register(ResourceLocation groupId)
    {
        return register(groupId, builder -> {});
    }

    /**
     * Register a widget group with an ID and a builder configurator.
     *
     * @param groupId      the resource location ID for the group.
     * @param configurator a consumer to configure the builder.
     * @return the registry object.
     */
    public static DeferredHolder<WidgetGroup, WidgetGroup> register(ResourceLocation groupId, Consumer<WidgetGroup.Builder> configurator)
    {
        return DEFERRED_REGISTER.register(groupId.getPath(), () -> {
            final WidgetGroup.Builder builder = new WidgetGroup.Builder(groupId);
            configurator.accept(builder);
            return builder.build();
        });
    }
}
