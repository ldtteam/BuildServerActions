package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.registry.WidgetRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

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
     * The forge registry containing all the widget layouts.
     */
    @Nullable
    private IForgeRegistry<WidgetRegistries.WidgetLayout> widgetLayouts;

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
        }
        return instance;
    }

    /**
     * Assign the widget group registry.
     *
     * @param registry the registry.
     */
    void setWidgetLayoutRegistry(final IForgeRegistry<WidgetRegistries.WidgetLayout> registry)
    {
        this.widgetLayouts = registry;
    }

    /**
     * Get the layout for a specific screen type, possibly null.
     *
     * @param layoutType the layout type.
     * @return the found layout specification, if any.
     */
    @Nullable
    public WidgetRegistries.WidgetLayout getLayout(Class<?> layoutType)
    {
        if (this.widgetLayouts == null)
        {
            return null;
        }
        return this.widgetLayouts.getValues().stream().filter(f -> f.getScreenClass().equals(layoutType)).findFirst().orElse(null);
    }
}
