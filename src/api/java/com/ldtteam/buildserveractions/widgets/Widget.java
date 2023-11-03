package com.ldtteam.buildserveractions.widgets;

/**
 * Implementation for any widget present on the actions GUI.
 */
public interface Widget extends WidgetBase
{
    /**
     * Callback for the widget, this method is only invoked on the client side, networking to the server has to be implemented manually for each widget.
     */
    void handle();
}
