package com.ldtteam.buildserveractions.registry;

import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.ItemWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.domum.OpenCutterWidgetCallbacks;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.Widget;
import com.ldtteam.buildserveractions.registry.WidgetRegistries.WidgetGroup;
import com.ldtteam.buildserveractions.util.ClockItemStackUtilities;
import com.ldtteam.domumornamentum.block.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

import java.util.Comparator;
import java.util.function.Consumer;

import static com.ldtteam.buildserveractions.EventHandler.WIDGET_GROUP_REGISTRY_KEY;
import static com.ldtteam.buildserveractions.EventHandler.WIDGET_REGISTRY_KEY;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_DOMUM_OPEN_CUTTER_DESC;
import static com.ldtteam.buildserveractions.constants.TranslationConstants.WIDGET_DOMUM_OPEN_CUTTER_NAME;
import static com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks.FLIGHT_SPEED_MULTIPLIER_KEY;
import static com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks.WIDGET_GAME_MODE_KEY;
import static com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks.TIME_KEY;

/**
 * Initializer for all widget registry entries.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WidgetRegistriesInitializer
{
    private static final ResourceLocation WIDGET_GROUP_GAME_MODES = id("group-game-modes");
    private static final ResourceLocation WIDGET_GROUP_TIME       = id("group-time");
    private static final ResourceLocation WIDGET_GROUP_SPEED      = id("group-speed");
    private static final ResourceLocation WIDGET_GROUP_ITEMS      = id("group-items");
    private static final ResourceLocation WIDGET_GROUP_DOMUM      = id("group-domum-ornamentum");

    /**
     * Internal method for wrapping the builder class for widget groups.
     *
     * @param groupId the id of the group.
     */
    private static void createGroupEntry(final RegisterHelper<WidgetGroup> helper, final ResourceLocation groupId, final Consumer<WidgetRegistries.WidgetGroup.Builder> builder)
    {
        final WidgetRegistries.WidgetGroup.Builder group = new WidgetRegistries.WidgetGroup.Builder(groupId);
        builder.accept(group);
        helper.register(groupId, group.build());
    }

    /**
     * Internal method for wrapping the builder class for widgets.
     *
     * @param groupId  the group of the widget.
     * @param widgetId the id of the widget.
     * @param builder  the builder callback.
     */
    private static void createWidgetEntry(
      final RegisterHelper<Widget> helper,
      final ResourceLocation groupId,
      final ResourceLocation widgetId,
      final Consumer<WidgetRegistries.Widget.Builder> builder)
    {
        final WidgetRegistries.Widget.Builder widget = new WidgetRegistries.Widget.Builder(groupId, widgetId);
        builder.accept(widget);
        helper.register(widgetId.getPath(), widget.build());
    }

    /**
     * Quick constructor for mod resource ids.
     *
     * @param name the name of the resource id.
     * @return the resource.
     */
    private static ResourceLocation id(String name)
    {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    @SubscribeEvent
    public static void register(final RegisterEvent event)
    {
        event.register(WIDGET_GROUP_REGISTRY_KEY, helper -> {
            createGroupEntry(helper, WIDGET_GROUP_GAME_MODES, builder -> builder.setSorter(new GameModeWidgetCallbacks.GameModeSorter()));

            createGroupEntry(helper, WIDGET_GROUP_TIME, builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(TIME_KEY, Number.class).intValue())));

            createGroupEntry(helper,
              WIDGET_GROUP_SPEED,
              builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(FLIGHT_SPEED_MULTIPLIER_KEY, Number.class).intValue())));

            createGroupEntry(helper, WIDGET_GROUP_ITEMS, builder -> {});

            if (ModList.get().isLoaded("domum_ornamentum"))
            {
                createGroupEntry(helper, WIDGET_GROUP_DOMUM, builder -> {});
            }
        });

        event.register(WIDGET_REGISTRY_KEY, helper -> {
            createWidgetEntry(helper,
              WIDGET_GROUP_GAME_MODES,
              id("gamemode-survival"),
              builder -> builder.setName(GameModeWidgetCallbacks::name)
                           .setDescription(GameModeWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.IRON_SWORD))
                           .setHandler(GameModeWidgetCallbacks::handler)
                           .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SURVIVAL));

            createWidgetEntry(helper,
              WIDGET_GROUP_GAME_MODES,
              id("gamemode-creative"),
              builder -> builder.setName(GameModeWidgetCallbacks::name)
                           .setDescription(GameModeWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.GRASS_BLOCK))
                           .setHandler(GameModeWidgetCallbacks::handler)
                           .addMetadata(WIDGET_GAME_MODE_KEY, GameType.CREATIVE));

            createWidgetEntry(helper,
              WIDGET_GROUP_GAME_MODES,
              id("gamemode-spectator"),
              builder -> builder.setName(GameModeWidgetCallbacks::name)
                           .setDescription(GameModeWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.ENDER_EYE))
                           .setHandler(GameModeWidgetCallbacks::handler)
                           .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SPECTATOR));

            createWidgetEntry(helper,
              WIDGET_GROUP_GAME_MODES,
              id("gamemode-adventure"),
              builder -> builder.setName(GameModeWidgetCallbacks::name)
                           .setDescription(GameModeWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.MAP))
                           .setHandler(GameModeWidgetCallbacks::handler)
                           .addMetadata(WIDGET_GAME_MODE_KEY, GameType.ADVENTURE));

            createWidgetEntry(helper,
              WIDGET_GROUP_TIME,
              id("time-noon"),
              builder -> builder.setName(SetTimeWidgetCallbacks::name)
                           .setIcon(ClockItemStackUtilities.createItemStack(0))
                           .setHandler(SetTimeWidgetCallbacks::handler)
                           .addMetadata(TIME_KEY, 6000));

            createWidgetEntry(helper,
              WIDGET_GROUP_TIME,
              id("time-midnight"),
              builder -> builder.setName(SetTimeWidgetCallbacks::name)
                           .setIcon(ClockItemStackUtilities.createItemStack(0.5f))
                           .setHandler(SetTimeWidgetCallbacks::handler)
                           .addMetadata(TIME_KEY, 18000));

            createWidgetEntry(helper,
              WIDGET_GROUP_SPEED,
              id("speed-01"),
              builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                           .setIcon(new ItemStack(Items.LEATHER_BOOTS))
                           .setHandler(FlightSpeedWidgetCallbacks::handler)
                           .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 1));

            createWidgetEntry(helper,
              WIDGET_GROUP_SPEED,
              id("speed-02"),
              builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                           .setIcon(new ItemStack(Items.IRON_BOOTS))
                           .setHandler(FlightSpeedWidgetCallbacks::handler)
                           .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 2));

            createWidgetEntry(helper,
              WIDGET_GROUP_SPEED,
              id("speed-05"),
              builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                           .setIcon(new ItemStack(Items.GOLDEN_BOOTS))
                           .setHandler(FlightSpeedWidgetCallbacks::handler)
                           .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 5));

            createWidgetEntry(helper,
              WIDGET_GROUP_SPEED,
              id("speed-10"),
              builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                           .setIcon(new ItemStack(Items.DIAMOND_BOOTS))
                           .setHandler(FlightSpeedWidgetCallbacks::handler)
                           .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 10));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-barrier-block"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.BARRIER))
                           .setHandler(ItemWidgetCallbacks::handler));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-command-block"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.COMMAND_BLOCK))
                           .setHandler(ItemWidgetCallbacks::handler));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-debug-stick"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(ModItems.DEBUG_STICK.get()))
                           .setHandler(ItemWidgetCallbacks::handler));

            final ItemStack invisibleItemFrame = new ItemStack(Items.ITEM_FRAME);
            final CompoundTag invisibleItemFrameTag = invisibleItemFrame.getOrCreateTag();
            final CompoundTag invisibleItemFrameEntityTag = new CompoundTag();
            invisibleItemFrameEntityTag.putBoolean("Invisible", true);
            invisibleItemFrameTag.put("EntityTag", invisibleItemFrameEntityTag);

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-invisible-item-frame"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(invisibleItemFrame)
                           .setHandler(ItemWidgetCallbacks::handler));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-jigsaw-block"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.JIGSAW))
                           .setHandler(ItemWidgetCallbacks::handler));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-structure-block"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.STRUCTURE_BLOCK))
                           .setHandler(ItemWidgetCallbacks::handler));

            createWidgetEntry(helper,
              WIDGET_GROUP_ITEMS,
              id("item-structure-void"),
              builder -> builder.setName(ItemWidgetCallbacks::name)
                           .setDescription(ItemWidgetCallbacks::description)
                           .setIcon(new ItemStack(Items.STRUCTURE_VOID))
                           .setHandler(ItemWidgetCallbacks::handler));

            if (ModList.get().isLoaded("domum_ornamentum"))
            {
                createWidgetEntry(helper,
                  WIDGET_GROUP_DOMUM,
                  id("domum-open-cutter"),
                  builder -> builder.setName(Component.translatable(WIDGET_DOMUM_OPEN_CUTTER_NAME))
                               .setDescription(Component.translatable(WIDGET_DOMUM_OPEN_CUTTER_DESC))
                               .setIcon(ModBlocks.getInstance().getArchitectsCutter().asItem().getDefaultInstance())
                               .setHandler(OpenCutterWidgetCallbacks::handler));
            }
        });
    }
}
