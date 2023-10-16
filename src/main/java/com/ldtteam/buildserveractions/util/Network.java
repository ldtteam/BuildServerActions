package com.ldtteam.buildserveractions.util;

import com.ldtteam.buildserveractions.network.NetworkChannel;

public class Network
{
    /**
     * The network instance.
     */
    private static NetworkChannel networkInstance;

    /**
     * Get the network handler.
     *
     * @return the network handler.
     */
    public static NetworkChannel getNetwork()
    {
        if (networkInstance == null)
        {
            networkInstance = new NetworkChannel("net-channel");
        }
        return networkInstance;
    }
}