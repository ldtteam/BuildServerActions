package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.widgets.Widget;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for all widgets.
 */
public class WidgetManager
{
    /**
     * The singleton instance.
     */
    private static WidgetManager instance;

    /**
     * The map of widgets, separated by widget group.
     */
    private final Map<ResourceLocation, List<Widget>> widgetGroups = new HashMap<>();

    /**
     * Obtain the {@link WidgetManager} instance.
     *
     * @return the singleton instance.
     */
    public static WidgetManager getInstance()
    {
        if (instance == null)
        {
            instance = new WidgetManager();
        }
        return instance;
    }

    /**
     * Adds a widget to the manager, may only be called internally.
     *
     * @param widget the new widget.
     */
    void addWidget(final Widget widget)
    {
        this.widgetGroups.putIfAbsent(widget.getGroupId(), new ArrayList<>());
        this.widgetGroups.get(widget.getGroupId()).add(widget);
    }
}
