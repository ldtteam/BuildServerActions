package com.ldtteam.buildserveractions.registry;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.util.records.SizeI;
import com.ldtteam.buildserveractions.constants.Constants;
import com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.ItemWidgetCallbacks;
import com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks;
import com.ldtteam.buildserveractions.util.ClockItemStackUtilities;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.function.Consumer;

import static com.ldtteam.buildserveractions.handlers.FlightSpeedWidgetCallbacks.FLIGHT_SPEED_MULTIPLIER_KEY;
import static com.ldtteam.buildserveractions.handlers.GameModeWidgetCallbacks.WIDGET_GAME_MODE_KEY;
import static com.ldtteam.buildserveractions.handlers.SetTimeWidgetCallbacks.TIME_KEY;

/**
 * Initializer for all widget registry entries.
 */
public class WidgetRegistriesInitializer
{
    public static final DeferredRegister<WidgetRegistries.WidgetLayout> DEFERRED_LAYOUT_REGISTER =
      DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "widget-layouts"), Constants.MOD_ID);
    public static final DeferredRegister<WidgetRegistries.WidgetGroup>  DEFERRED_GROUP_REGISTER  =
      DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "widget-groups"), Constants.MOD_ID);
    public static final DeferredRegister<WidgetRegistries.Widget>       DEFERRED_WIDGET_REGISTER =
      DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "widgets"), Constants.MOD_ID);

    /**
     * Register all widget layout entries.
     */
    public static void registerLayoutEntries(final IEventBus eventBus)
    {
        DEFERRED_LAYOUT_REGISTER.register(eventBus);

        WidgetRegistries.layoutSurvivalInventory = createLayoutEntry(id("layout-inv-survival"),
          InventoryScreen.class,
          builder -> builder.setAlignment(Alignment.MIDDLE_LEFT)
                       .setOffset(screen -> {
                           SizeI size = new SizeI(-10, 0);
                           if (screen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookComponent().isVisible())
                           {
                               final SizeI.MutableSizeI mutable = size.toMutable();
                               mutable.width -= RecipeBookComponent.IMAGE_WIDTH;
                               size = mutable.toImmutable();
                           }
                           return size;
                       }));

        WidgetRegistries.layoutCreativeInventory =
          createLayoutEntry(id("layout-inv-creative"),
            CreativeModeInventoryScreen.class,
            builder -> builder.setAlignment(Alignment.MIDDLE_LEFT)
                         .setOffset(new SizeI(-10, 0)));
    }

    /**
     * Register all widget group entries.
     */
    public static void registerGroupEntries(final IEventBus eventBus)
    {
        DEFERRED_GROUP_REGISTER.register(eventBus);

        WidgetRegistries.groupGamemodes = createGroupEntry(id("group-game-modes"),
          builder ->
            builder.setSorter(new GameModeWidgetCallbacks.GameModeSorter()));

        WidgetRegistries.groupTime = createGroupEntry(id("group-time"),
          builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(TIME_KEY, Number.class).intValue())));

        WidgetRegistries.groupSpeed = createGroupEntry(id("group-speed"),
          builder -> builder.setSorter(Comparator.comparingInt(w -> w.getMetadataValue(FLIGHT_SPEED_MULTIPLIER_KEY, Number.class).intValue())));

        WidgetRegistries.groupItems = createGroupEntry(id("group-items"), builder -> {});
    }

    /**
     * Register all widget entries.
     */
    public static void registerWidgetEntries(final IEventBus eventBus)
    {
        DEFERRED_WIDGET_REGISTER.register(eventBus);

        WidgetRegistries.widgetGamemodeSurvival = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-survival"),
          builder -> builder.setName(GameModeWidgetCallbacks::name)
                       .setDescription(GameModeWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.IRON_SWORD))
                       .setHandler(GameModeWidgetCallbacks::handler)
                       .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SURVIVAL));

        WidgetRegistries.widgetGamemodeCreative = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-creative"),
          builder -> builder.setName(GameModeWidgetCallbacks::name)
                       .setDescription(GameModeWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.GRASS_BLOCK))
                       .setHandler(GameModeWidgetCallbacks::handler)
                       .addMetadata(WIDGET_GAME_MODE_KEY, GameType.CREATIVE));

        WidgetRegistries.widgetGamemodeSpectator = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-spectator"),
          builder -> builder.setName(GameModeWidgetCallbacks::name)
                       .setDescription(GameModeWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.ENDER_EYE))
                       .setHandler(GameModeWidgetCallbacks::handler)
                       .addMetadata(WIDGET_GAME_MODE_KEY, GameType.SPECTATOR));

        WidgetRegistries.widgetGamemodeAdventure = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-adventure"),
          builder -> builder.setName(GameModeWidgetCallbacks::name)
                       .setDescription(GameModeWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.MAP))
                       .setHandler(GameModeWidgetCallbacks::handler)
                       .addMetadata(WIDGET_GAME_MODE_KEY, GameType.ADVENTURE));

        WidgetRegistries.widgetTimeNoon = createWidgetEntry(WidgetRegistries.groupTime.getId(),
          id("time-noon"),
          builder -> builder.setName(SetTimeWidgetCallbacks::name)
                       .setIcon(ClockItemStackUtilities.createItemStack(0))
                       .setHandler(SetTimeWidgetCallbacks::handler)
                       .addMetadata(TIME_KEY, 6000));

        WidgetRegistries.widgetTimeMidnight = createWidgetEntry(WidgetRegistries.groupTime.getId(),
          id("time-midnight"),
          builder -> builder.setName(SetTimeWidgetCallbacks::name)
                       .setIcon(ClockItemStackUtilities.createItemStack(0.5f))
                       .setHandler(SetTimeWidgetCallbacks::handler)
                       .addMetadata(TIME_KEY, 18000));

        WidgetRegistries.widgetSpeed01 = createWidgetEntry(WidgetRegistries.groupSpeed.getId(),
          id("speed-01"),
          builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                       .setIcon(new ItemStack(Items.LEATHER_BOOTS))
                       .setHandler(FlightSpeedWidgetCallbacks::handler)
                       .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 1));

        WidgetRegistries.widgetSpeed02 = createWidgetEntry(WidgetRegistries.groupSpeed.getId(),
          id("speed-02"),
          builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                       .setIcon(new ItemStack(Items.IRON_BOOTS))
                       .setHandler(FlightSpeedWidgetCallbacks::handler)
                       .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 2));

        WidgetRegistries.widgetSpeed05 = createWidgetEntry(WidgetRegistries.groupSpeed.getId(),
          id("speed-05"),
          builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                       .setIcon(new ItemStack(Items.GOLDEN_BOOTS))
                       .setHandler(FlightSpeedWidgetCallbacks::handler)
                       .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 5));

        WidgetRegistries.widgetSpeed10 = createWidgetEntry(WidgetRegistries.groupSpeed.getId(),
          id("speed-10"),
          builder -> builder.setName(FlightSpeedWidgetCallbacks::name)
                       .setIcon(new ItemStack(Items.DIAMOND_BOOTS))
                       .setHandler(FlightSpeedWidgetCallbacks::handler)
                       .addMetadata(FLIGHT_SPEED_MULTIPLIER_KEY, 10));

        WidgetRegistries.widgetItemBarrierBlock = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-barrier-block"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.BARRIER))
                       .setHandler(ItemWidgetCallbacks::handler));

        WidgetRegistries.widgetItemDebugStick = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-debug-stick"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.DEBUG_STICK))
                       .setHandler(ItemWidgetCallbacks::handler));

        final ItemStack invisibleItemFrame = new ItemStack(Items.ITEM_FRAME);
        final CompoundTag invisibleItemFrameTag = invisibleItemFrame.getOrCreateTag();
        final CompoundTag invisibleItemFrameEntityTag = new CompoundTag();
        invisibleItemFrameEntityTag.putBoolean("Invisible", true);
        invisibleItemFrameTag.put("EntityTag", invisibleItemFrameEntityTag);

        WidgetRegistries.widgetItemInvisibleItemFrame = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-invisible-item-frame"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(invisibleItemFrame)
                       .setHandler(ItemWidgetCallbacks::handler));

        WidgetRegistries.widgetItemJigsawBlock = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-jigsaw-block"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.JIGSAW))
                       .setHandler(ItemWidgetCallbacks::handler));

        WidgetRegistries.widgetItemStructureBlock = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-structure-block"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.STRUCTURE_BLOCK))
                       .setHandler(ItemWidgetCallbacks::handler));

        WidgetRegistries.widgetItemStructureVoid = createWidgetEntry(WidgetRegistries.groupItems.getId(),
          id("item-structure-void"),
          builder -> builder.setName(ItemWidgetCallbacks::name)
                       .setDescription(ItemWidgetCallbacks::description)
                       .setIcon(new ItemStack(Items.STRUCTURE_VOID))
                       .setHandler(ItemWidgetCallbacks::handler));
    }

    /**
     * Internal method for wrapping the builder class for widget layouts.
     *
     * @param layoutId    the id of the layout.
     * @param screenClass the attached screen class type.
     * @param builder     the builder callback.
     * @return the created registry object.
     */
    private static RegistryObject<WidgetRegistries.WidgetLayout> createLayoutEntry(
      final ResourceLocation layoutId,
      final Class<? extends AbstractContainerScreen<?>> screenClass,
      final Consumer<WidgetRegistries.WidgetLayout.Builder> builder)
    {
        final WidgetRegistries.WidgetLayout.Builder layout = new WidgetRegistries.WidgetLayout.Builder(screenClass);
        builder.accept(layout);
        return DEFERRED_LAYOUT_REGISTER.register(layoutId.getPath(), layout::build);
    }

    /**
     * Internal method for wrapping the builder class for widget groups.
     *
     * @param groupId the id of the group.
     * @return the created registry object.
     */
    private static RegistryObject<WidgetRegistries.WidgetGroup> createGroupEntry(
      final ResourceLocation groupId,
      final Consumer<WidgetRegistries.WidgetGroup.Builder> builder)
    {
        final WidgetRegistries.WidgetGroup.Builder group = new WidgetRegistries.WidgetGroup.Builder(groupId);
        builder.accept(group);
        return DEFERRED_GROUP_REGISTER.register(groupId.getPath(), group::build);
    }

    /**
     * Internal method for wrapping the builder class for widgets.
     *
     * @param groupId  the group of the widget.
     * @param widgetId the id of the widget.
     * @param builder  the builder callback.
     * @return the created registry object.
     */
    private static RegistryObject<WidgetRegistries.Widget> createWidgetEntry(
      final ResourceLocation groupId,
      final ResourceLocation widgetId,
      final Consumer<WidgetRegistries.Widget.Builder> builder)
    {
        final WidgetRegistries.Widget.Builder widget = new WidgetRegistries.Widget.Builder(groupId, widgetId);
        builder.accept(widget);
        return DEFERRED_WIDGET_REGISTER.register(widgetId.getPath(), widget::build);
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
}
