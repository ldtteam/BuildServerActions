package com.ldtteam.buildserveractions.widgets;

import com.ldtteam.buildserveractions.network.IMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Network widgets rely on automatically sending a {@link IMessage} to the server.
 *
 * @param <T>
 */
public interface NetworkWidget<T extends IMessage> extends WidgetBase
{
    @NotNull
    T createMessage();

    @NotNull
    T constructEmptyMessage();
}
