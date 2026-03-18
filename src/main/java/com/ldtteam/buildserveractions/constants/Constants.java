package com.ldtteam.buildserveractions.constants;

import net.minecraft.resources.ResourceLocation;

/**
 * Some constants needed for the whole mod.
 */
public class Constants
{
    /**
     * The mod identifier used for registration and resource locations.
     */
    public static final String MOD_ID = "buildserveractions";

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants()
    {
    }

    /**
     * Generate a {@link ResourceLocation} for the mod.
     *
     * @param path the string path.
     * @return the generated resource location.
     */
    public static ResourceLocation modId(final String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
