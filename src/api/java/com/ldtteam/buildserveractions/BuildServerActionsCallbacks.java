package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.layouts.ActionRenderLayout;
import com.ldtteam.buildserveractions.widgets.WidgetBase;

/**
 * INTERNAL USE ONLY.
 * <p>
 * This class is responsible for connecting the API back to the underlying systems.
 */
public interface BuildServerActionsCallbacks
{
    /**
     * Register a new layout.
     *
     * @param layout the new layout.
     */
    void registerLayout(final ActionRenderLayout<?> layout);

    /**
     * Register a new widget.
     *
     * @param widget the new widget.
     */
    void registerWidget(final WidgetBase widget);
}
