package com.ldtteam.buildserveractions.client.button;

import com.ldtteam.blockui.BOGuiGraphics;
import com.ldtteam.blockui.Pane;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FastColor;

/**
 * Draws corner brackets to indicate an empty favorite slot.
 */
public class FavoritePlaceholderPane extends Pane
{
    private static final int COLOR = FastColor.ABGR32.color(255, ChatFormatting.DARK_GRAY.getColor());

    /**
     * Creates a new empty favorite slot placeholder pane.
     */
    public FavoritePlaceholderPane() {}

    @Override
    public void drawSelf(final BOGuiGraphics target, final double mx, final double my)
    {
        final int x1 = x;
        final int y1 = y;
        final int x2 = x + width;
        final int y2 = y + height;
        final int corner = Math.min(width, height) / 4;

        // Top-left
        target.fill(x1, y1, x1 + corner, y1 + 1, COLOR);
        target.fill(x1, y1, x1 + 1, y1 + corner, COLOR);
        // Top-right
        target.fill(x2 - corner, y1, x2, y1 + 1, COLOR);
        target.fill(x2 - 1, y1, x2, y1 + corner, COLOR);
        // Bottom-left
        target.fill(x1, y2 - 1, x1 + corner, y2, COLOR);
        target.fill(x1, y2 - corner, x1 + 1, y2, COLOR);
        // Bottom-right
        target.fill(x2 - corner, y2 - 1, x2, y2, COLOR);
        target.fill(x2 - 1, y2 - corner, x2, y2, COLOR);
    }
}
