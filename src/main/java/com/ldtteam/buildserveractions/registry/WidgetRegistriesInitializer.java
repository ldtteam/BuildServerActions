package com.ldtteam.buildserveractions.registry;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.util.records.SizeI;
import com.ldtteam.buildserveractions.handlers.WidgetActionHandlers;
import com.ldtteam.buildserveractions.util.Constants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

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
      DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "widget"), Constants.MOD_ID);

    /**
     * Register all widget layout entries.
     */
    public static void registerLayoutEntries(final IEventBus eventBus)
    {
        DEFERRED_LAYOUT_REGISTER.register(eventBus);

        WidgetRegistries.layoutSurvivalInventory = createLayoutEntry(id("layout-inv-survival"),
          InventoryScreen.class,
          builder -> builder.setAlignment(Alignment.MIDDLE_LEFT)
                       .setOffset(new SizeI(-10, 0)));

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

        WidgetRegistries.groupGamemodes = createGroupEntry(id("group-game-modes"));
    }

    /**
     * Register all widget entries.
     */
    public static void registerWidgetEntries(final IEventBus eventBus)
    {
        DEFERRED_WIDGET_REGISTER.register(eventBus);

        WidgetRegistries.gamemodeSurvivalWidget = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-survival"),
          builder -> builder.setName(GameType.SURVIVAL.getLongDisplayName())
                       .setDescription(Component.translatable("commands.gamemode.success.self", GameType.SURVIVAL.getLongDisplayName()))
                       .setIcon(new ItemStack(Items.IRON_SWORD))
                       .setHandler(WidgetActionHandlers.switchGameMode(GameType.SURVIVAL)));

        WidgetRegistries.gamemodeCreativeWidget = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-creative"),
          builder -> builder.setName(GameType.CREATIVE.getLongDisplayName())
                       .setDescription(Component.translatable("commands.gamemode.success.self", GameType.CREATIVE.getLongDisplayName()))
                       .setIcon(new ItemStack(Items.GRASS_BLOCK))
                       .setHandler(WidgetActionHandlers.switchGameMode(GameType.CREATIVE)));

        WidgetRegistries.gamemodeSpectatorWidget = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-spectator"),
          builder -> builder.setName(GameType.SPECTATOR.getLongDisplayName())
                       .setDescription(Component.translatable("commands.gamemode.success.self", GameType.SPECTATOR.getLongDisplayName()))
                       .setIcon(new ItemStack(Items.ENDER_EYE))
                       .setHandler(WidgetActionHandlers.switchGameMode(GameType.SPECTATOR)));

        WidgetRegistries.gamemodeAdventureWidget = createWidgetEntry(WidgetRegistries.groupGamemodes.getId(),
          id("gamemode-adventure"),
          builder -> builder.setName(GameType.ADVENTURE.getLongDisplayName())
                       .setDescription(Component.translatable("commands.gamemode.success.self", GameType.ADVENTURE.getLongDisplayName()))
                       .setIcon(new ItemStack(Items.GRASS_BLOCK))
                       .setHandler(WidgetActionHandlers.switchGameMode(GameType.ADVENTURE)));
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
      final Class<? extends Screen> screenClass,
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
    private static RegistryObject<WidgetRegistries.WidgetGroup> createGroupEntry(final ResourceLocation groupId)
    {
        final WidgetRegistries.WidgetGroup group = new WidgetRegistries.WidgetGroup(groupId);
        return DEFERRED_GROUP_REGISTER.register(groupId.getPath(), () -> group);
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
