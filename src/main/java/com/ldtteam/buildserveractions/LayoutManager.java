package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;

import java.util.HashMap;
import java.util.Map;

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
     * The map of widgets, separated by widget group.
     */
    private final Map<Class<?>, ActionRenderLayout<?>> screenLayouts = new HashMap<>();

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
     * Register a layout for the given screen.
     *
     * @param layout the layout specification.
     */
    void registerLayout(final ActionRenderLayout<?> layout)
    {
        this.screenLayouts.putIfAbsent(layout.getScreenClass(), layout);
    }

    /**
     * Get the layout for a specific screen type, possibly null.
     *
     * @param layoutType the layout type.
     * @return the found layout specification, if any.
     */
    public ActionRenderLayout<?> getLayout(Class<?> layoutType)
    {
        return this.screenLayouts.get(layoutType);
    }
}
