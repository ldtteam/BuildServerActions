package com.ldtteam.buildserveractions.util;

import com.ldtteam.buildserveractions.constants.Constants;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtils
{
    /**
     * Quick constructor for mod resource ids.
     *
     * @param name the name of the resource id.
     * @return the resource.
     */
    public static ResourceLocation id(final String name)
    {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
