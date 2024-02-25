package com.ldtteam.buildserveractions;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.util.records.SizeI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manager class for all layouts.
 */
public class LayoutManager
{
    /**
     * The singleton instance.
     */
    private static LayoutManager instance;

    /**
     * The different layout instances.
     */
    private final Map<Class<? extends Screen>, WidgetLayout> layouts = new HashMap<>();

    /**
     * Obtain the {@link LayoutManager} instance.
     *
     * @return the singleton instance.
     */
    public static LayoutManager getInstance()
    {
        if (instance == null)
        {
            instance = new LayoutManager();

            instance.layouts.put(InventoryScreen.class, new WidgetLayout.Builder(InventoryScreen.class).setAlignment(Alignment.MIDDLE_LEFT).setOffset(screen -> {
                SizeI size = new SizeI(-10, 0);
                if (screen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookComponent().isVisible())
                {
                    final SizeI.MutableSizeI mutable = size.toMutable();
                    mutable.width -= RecipeBookComponent.IMAGE_WIDTH;
                    size = mutable.toImmutable();
                }
                return size;
            }).build());

            instance.layouts.put(CreativeModeInventoryScreen.class,
              new WidgetLayout.Builder(CreativeModeInventoryScreen.class).setAlignment(Alignment.MIDDLE_LEFT).setOffset(screen -> new SizeI(-10, 0)).build());
        }
        return instance;
    }

    /**
     * Get the layout for a specific screen type, possibly null.
     *
     * @param layoutType the layout type.
     * @return the found layout specification, if any.
     */
    @Nullable
    public WidgetLayout getLayout(Class<?> layoutType)
    {
        return this.layouts.values().stream().filter(f -> f.getScreenClass().equals(layoutType)).findFirst().orElse(null);
    }

    /**
     * Widget layout registry instance.
     */
    public static class WidgetLayout
    {
        /**
         * The class type of which screen the widget layout gets attached to.
         */
        private final Class<? extends AbstractContainerScreen<?>> screenClass;

        /**
         * The maximum amount of button columns visible at once.
         */
        private final int maxGroups;

        /**
         * The maximum amount of buttons visible per column.
         */
        private final int maxButtonsInGroup;

        /**
         * The alignment of the window compared to the attached screen.
         */
        private final Alignment alignment;

        /**
         * An additional offset compared to the position where the window is aligned.
         */
        private final Function<AbstractContainerScreen<?>, SizeI> offsetFunc;

        /**
         * Default internal constructor.
         */
        public WidgetLayout(
          final Class<? extends AbstractContainerScreen<?>> screenClass,
          final int maxGroups,
          final int maxButtonsInGroup,
          final Alignment alignment,
          final Function<AbstractContainerScreen<?>, SizeI> offsetFunc)
        {
            this.screenClass = screenClass;
            this.maxGroups = maxGroups;
            this.maxButtonsInGroup = maxButtonsInGroup;
            this.alignment = alignment;
            this.offsetFunc = offsetFunc;
        }

        /**
         * Get the class type of which screen the widget layout gets attached to.
         */
        public Class<? extends AbstractContainerScreen<?>> getScreenClass()
        {
            return screenClass;
        }

        /**
         * Get the maximum amount of button columns visible at once.
         *
         * @return the selected amount.
         */
        public int getMaxGroups()
        {
            return maxGroups;
        }

        /**
         * Get the maximum amount of buttons visible per column.
         *
         * @return the selected amount.
         */
        public int getMaxButtonsInGroup()
        {
            return maxButtonsInGroup;
        }

        /**
         * Get the alignment of the window compared to the attached screen.
         *
         * @return the selected alignment.
         */
        public Alignment getAlignment()
        {
            return alignment;
        }

        /**
         * Get an additional offset compared to the position where the window is aligned.
         *
         * @return the selected offset.
         */
        public Function<AbstractContainerScreen<?>, SizeI> getOffsetFunction()
        {
            return offsetFunc;
        }

        @Override
        public int hashCode()
        {
            return screenClass.hashCode();
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            final WidgetLayout that = (WidgetLayout) o;

            return screenClass.equals(that.screenClass);
        }

        /**
         * Builder class for {@link WidgetLayout} instances.
         */
        public static class Builder
        {
            /**
             * The class type of which screen the widget layout gets attached to.
             */
            private final Class<? extends AbstractContainerScreen<?>> screenClass;

            /**
             * The maximum amount of button columns visible at once.
             */
            private int maxGroups = 5;

            /**
             * The maximum amount of buttons visible per column.
             */
            private int maxButtonsInGroup = 5;

            /**
             * The alignment of the window compared to the attached screen.
             */
            private Alignment alignment = Alignment.TOP_LEFT;

            /**
             * An additional offset compared to the position where the window is aligned.
             */
            private Function<AbstractContainerScreen<?>, SizeI> offsetFunc = screen -> new SizeI(0, 0);

            /**
             * Default constructor.
             *
             * @param screenClass the class type of the window which to attach to.
             */
            public Builder(final Class<? extends AbstractContainerScreen<?>> screenClass)
            {
                this.screenClass = screenClass;
            }

            /**
             * Set the maximum amount of button columns visible at once.
             * Defaults to 5.
             *
             * @param maxGroups the new amount.
             * @return builder for chaining.
             */
            public Builder setMaxGroups(final int maxGroups)
            {
                this.maxGroups = maxGroups;
                return this;
            }

            /**
             * Set the maximum amount of buttons visible per column.
             * Defaults to 5.
             *
             * @param maxButtonsInGroup the new amount.
             * @return builder for chaining.
             */
            public Builder setMaxButtonsInGroup(final int maxButtonsInGroup)
            {
                this.maxButtonsInGroup = maxButtonsInGroup;
                return this;
            }

            /**
             * Set the alignment of the window compared to the attached screen.
             * Defaults to {@link Alignment#TOP_LEFT}.
             *
             * @param alignment the new alignment.
             * @return builder for chaining.
             */
            public Builder setAlignment(final Alignment alignment)
            {
                this.alignment = alignment;
                return this;
            }

            /**
             * Set an additional offset compared to the position where the window is aligned.
             * Defaults to 0 on both axis.
             *
             * @param offset the new offset.
             * @return builder for chaining.
             */
            public Builder setOffset(final SizeI offset)
            {
                this.offsetFunc = screen -> offset;
                return this;
            }

            /**
             * Set an additional offset compared to the position where the window is aligned.
             * Defaults to 0 on both axis.
             *
             * @param offsetFunc a function to determine the new offset.
             * @return builder for chaining.
             */
            public Builder setOffset(final Function<AbstractContainerScreen<?>, SizeI> offsetFunc)
            {
                this.offsetFunc = offsetFunc;
                return this;
            }

            /**
             * Finalize construction of the {@link WidgetLayout} instance.
             *
             * @return the instance.
             */
            public WidgetLayout build()
            {
                return new WidgetLayout(screenClass, maxGroups, maxButtonsInGroup, alignment, offsetFunc);
            }
        }
    }
}
