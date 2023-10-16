package com.ldtteam.buildserveractions;

import com.ldtteam.buildserveractions.widgets.NetworkWidget;
import com.ldtteam.buildserveractions.widgets.Widget;

public class BuildServerActionsAPI
{
    private static BuildServerActionsCallbacks internalCallbacks;

    public static void setup(final BuildServerActionsCallbacks impl)
    {
        internalCallbacks = impl;
    }

    public static void addWidget(final Widget widget)
    {
        internalCallbacks.registerWidget(widget);
    }

    public static void addNetworkWidget(final NetworkWidget<?> widget)
    {
        internalCallbacks.registerWidget(widget);
    }
}
