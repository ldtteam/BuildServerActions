package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.ItemWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks;
import com.ldtteam.buildserveractions.registry.addons.DomumWidgets;
import com.ldtteam.buildserveractions.util.ClockItemStackUtilities;
import com.ldtteam.buildserveractions.widget.Widget;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static com.ldtteam.buildserveractions.constants.Constants.modId;
import static com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks.FLIGHT_SPEED_MULTIPLIER_KEY;
import static com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks.WIDGET_GAME_MODE_KEY;
import static com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks.TIME_KEY;
import static com.ldtteam.buildserveractions.registry.ModWidgetGroups.*;

/**
 * Registry for widgets.
 */
@SuppressWarnings({"java:S1104", "java:S1444", "unused"})
public class ModWidgets
{
    public static final ResourceKey<Registry<Widget>> REGISTRY_KEY      = ResourceKey.createRegistryKey(modId("widgets"));
    public static final DeferredRegister<Widget>      DEFERRED_REGISTER = DeferredRegister.create(REGISTRY_KEY, Constants.MOD_ID);

    public static final ResourceLocation GAMEMODE_SURVIVAL_ID  = modId("gamemode-survival");
    public static final ResourceLocation GAMEMODE_CREATIVE_ID  = modId("gamemode-creative");
    public static final ResourceLocation GAMEMODE_SPECTATOR_ID = modId("gamemode-spectator");
    public static final ResourceLocation GAMEMODE_ADVENTURE_ID = modId("gamemode-adventure");

    public static final ResourceLocation TIME_NOON_ID     = modId("time-noon");
    public static final ResourceLocation TIME_MIDNIGHT_ID = modId("time-midnight");

    public static final ResourceLocation SPEED_01_ID = modId("speed-01");
    public static final ResourceLocation SPEED_02_ID = modId("speed-02");
    public static final ResourceLocation SPEED_05_ID = modId("speed-05");
    public static final ResourceLocation SPEED_10_ID = modId("speed-10");

    public static final ResourceLocation ITEM_BARRIER_BLOCK_ID        = modId("item-barrier-block");
    public static final ResourceLocation ITEM_COMMAND_BLOCK_ID        = modId("item-command-block");
    public static final ResourceLocation ITEM_DEBUG_STICK_ID          = modId("item-debug-stick");
    public static final ResourceLocation ITEM_INVISIBLE_ITEM_FRAME_ID = modId("item-invisible-item-frame");
    public static final ResourceLocation ITEM_JIGSAW_BLOCK_ID         = modId("item-jigsaw-block");
    public static final ResourceLocation ITEM_STRUCTURE_BLOCK_ID      = modId("item-structure-block");
    public static final ResourceLocation ITEM_STRUCTURE_VOID_ID       = modId("item-structure-void");

    public static final RegistryObject<Widget> GAMEMODE_SURVIVAL = register(GROUP_GAME_MODES_ID,
        GAMEMODE_SURVIVAL_ID,
        builder -> builder.setName(GameModeWidgetCallbacks::name)
            .setDescription(GameModeWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.IRON_SWORD))
            .setHandler(GameModeWidgetCallbacks::handler)
            .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SURVIVAL));

    public static final RegistryObject<Widget> GAMEMODE_CREATIVE = register(GROUP_GAME_MODES_ID,
        GAMEMODE_CREATIVE_ID,
        builder -> builder.setName(GameModeWidgetCallbacks::name)
            .setDescription(GameModeWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.GRASS_BLOCK))
            .setHandler(GameModeWidgetCallbacks::handler)
            .addMetadata(WIDGET_GAME_MODE_KEY, GameType.CREATIVE));

    public static final RegistryObject<Widget> GAMEMODE_SPECTATOR = register(GROUP_GAME_MODES_ID,
        GAMEMODE_SPECTATOR_ID,
        builder -> builder.setName(GameModeWidgetCallbacks::name)
            .setDescription(GameModeWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.ENDER_EYE))
            .setHandler(GameModeWidgetCallbacks::handler)
            .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SPECTATOR));

    public static final RegistryObject<Widget> GAMEMODE_ADVENTURE = register(GROUP_GAME_MODES_ID,
        GAMEMODE_ADVENTURE_ID,
        builder -> builder.setName(GameModeWidgetCallbacks::name)
            .setDescription(GameModeWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.MAP))
            .setHandler(GameModeWidgetCallbacks::handler)
            .addMetadata(WIDGET_GAME_MODE_KEY, GameType.ADVENTURE));

    public static final RegistryObject<Widget> TIME_NOON = register(GROUP_TIME_ID,
        TIME_NOON_ID,
        builder -> builder.setName(SetTimeWidgetCallbacks::name)
            .setIcon(ClockItemStackUtilities.createItemStack(0))
            .setHandler(SetTimeWidgetCallbacks::handler)
            .addMetadata(TIME_KEY, 6000));

    public static final RegistryObject<Widget> TIME_MIDNIGHT = register(GROUP_TIME_ID,
        TIME_MIDNIGHT_ID,
        builder -> builder.setName(SetTimeWidgetCallbacks::name)
            .setIcon(ClockItemStackUtilities.createItemStack(0.5f))
            .setHandler(SetTimeWidgetCallbacks::handler)
            .addMetadata(TIME_KEY, 18000));

    public static final RegistryObject<Widget> SPEED_01 = register(GROUP_SPEED_ID,
        SPEED_01_ID,
        builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
            .setIcon(new ItemStack(Items.LEATHER_BOOTS))
            .setHandler(FlightSpeedWidgetCallbacks::handler)
            .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 1));

    public static final RegistryObject<Widget> SPEED_02 = register(GROUP_SPEED_ID,
        SPEED_02_ID,
        builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
            .setIcon(new ItemStack(Items.IRON_BOOTS))
            .setHandler(FlightSpeedWidgetCallbacks::handler)
            .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 2));

    public static final RegistryObject<Widget> SPEED_05 = register(GROUP_SPEED_ID,
        SPEED_05_ID,
        builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
            .setIcon(new ItemStack(Items.GOLDEN_BOOTS))
            .setHandler(FlightSpeedWidgetCallbacks::handler)
            .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 5));

    public static final RegistryObject<Widget> SPEED_10 = register(GROUP_SPEED_ID,
        SPEED_10_ID,
        builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
            .setIcon(new ItemStack(Items.DIAMOND_BOOTS))
            .setHandler(FlightSpeedWidgetCallbacks::handler)
            .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 10));

    public static final RegistryObject<Widget> ITEM_BARRIER_BLOCK = register(GROUP_ITEMS_ID,
        ITEM_BARRIER_BLOCK_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.BARRIER))
            .setHandler(ItemWidgetCallbacks::handler));

    public static final RegistryObject<Widget> ITEM_COMMAND_BLOCK = register(GROUP_ITEMS_ID,
        ITEM_COMMAND_BLOCK_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.COMMAND_BLOCK))
            .setHandler(ItemWidgetCallbacks::handler));

    public static final RegistryObject<Widget> ITEM_DEBUG_STICK = register(GROUP_ITEMS_ID,
        ITEM_DEBUG_STICK_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(ModItems.DEBUG_STICK.get()))
            .setHandler(ItemWidgetCallbacks::handler));

    public static final RegistryObject<Widget> ITEM_INVISIBLE_ITEM_FRAME = DEFERRED_REGISTER.register(ITEM_INVISIBLE_ITEM_FRAME_ID.getPath(), () -> {
        final ItemStack invisibleItemFrame = new ItemStack(Items.ITEM_FRAME);
        final CompoundTag invisibleItemFrameTag = invisibleItemFrame.getOrCreateTag();
        final CompoundTag invisibleItemFrameEntityTag = new CompoundTag();
        invisibleItemFrameEntityTag.putBoolean("Invisible", true);
        invisibleItemFrameTag.put("EntityTag", invisibleItemFrameEntityTag);

        return new Widget.Builder(GROUP_ITEMS_ID, ITEM_INVISIBLE_ITEM_FRAME_ID).setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(invisibleItemFrame)
            .setHandler(ItemWidgetCallbacks::handler)
            .build();
    });

    public static final RegistryObject<Widget> ITEM_JIGSAW_BLOCK = register(GROUP_ITEMS_ID,
        ITEM_JIGSAW_BLOCK_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.JIGSAW))
            .setHandler(ItemWidgetCallbacks::handler));

    public static final RegistryObject<Widget> ITEM_STRUCTURE_BLOCK = register(GROUP_ITEMS_ID,
        ITEM_STRUCTURE_BLOCK_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.STRUCTURE_BLOCK))
            .setHandler(ItemWidgetCallbacks::handler));

    public static final RegistryObject<Widget> ITEM_STRUCTURE_VOID = register(GROUP_ITEMS_ID,
        ITEM_STRUCTURE_VOID_ID,
        builder -> builder.setName(ItemWidgetCallbacks::name)
            .setDescription(ItemWidgetCallbacks::description)
            .setIcon(new ItemStack(Items.STRUCTURE_VOID))
            .setHandler(ItemWidgetCallbacks::handler));
    static
    {
        if (ModList.get().isLoaded("domum_ornamentum"))
        {
            DomumWidgets.init();
        }
    }
    /**
     * Register a widget with group ID, widget ID and a builder configurator.
     *
     * @param groupId      the resource location ID for the group.
     * @param widgetId     the resource location ID for the widget.
     * @param configurator a consumer to configure the builder.
     * @return the registry object.
     */
    public static RegistryObject<Widget> register(final ResourceLocation groupId, final ResourceLocation widgetId, final Consumer<Widget.Builder> configurator)
    {
        return DEFERRED_REGISTER.register(widgetId.getPath(), () -> {
            final Widget.Builder builder = new Widget.Builder(groupId, widgetId);
            configurator.accept(builder);
            return builder.build();
        });
    }
}
