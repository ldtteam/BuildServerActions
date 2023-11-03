package com.ldtteam.buildserveractions.client;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.blockui.views.BOWindow;
import net.minecraft.client.gui.screens.Screen;

/**
 * Special type of screen that hooks to a different underlying screen below it.
 * Passes along events.
 */
public class AttachedScreen extends BOScreen
{
    private final Screen attachedToScreen;

    /**
     * Create a GuiScreen from a BlockOut window.
     *
     * @param w BlockOut screen.
     */
    public AttachedScreen(final BOWindow w, final Screen attachedToScreen)
    {
        super(w);
        this.attachedToScreen = attachedToScreen;
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers)
    {
        if (super.keyPressed(key, scanCode, modifiers))
        {
            return true;
        }
        return this.attachedToScreen.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(final char ch, final int key)
    {
        if (super.charTyped(ch, key))
        {
            return true;
        }
        return this.attachedToScreen.charTyped(ch, key);
    }

    @Override
    public boolean mouseClicked(final double mxIn, final double myIn, final int keyCode)
    {
        if (super.mouseClicked(mxIn, myIn, keyCode))
        {
            return true;
        }
        return this.attachedToScreen.mouseClicked(mxIn, myIn, keyCode);
    }

    @Override
    public boolean mouseScrolled(final double mx, final double my, final double scrollDiff)
    {
        if (super.mouseScrolled(mx, my, scrollDiff))
        {
            return true;
        }
        return this.attachedToScreen.mouseScrolled(mx, my, scrollDiff);
    }

    @Override
    public boolean mouseDragged(final double xIn, final double yIn, final int speed, final double deltaX, final double deltaY)
    {
        if (super.mouseDragged(xIn, yIn, speed, deltaX, deltaY))
        {
            return true;
        }
        return this.attachedToScreen.mouseDragged(xIn, yIn, speed, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(final double mxIn, final double myIn, final int keyCode)
    {
        if (super.mouseReleased(mxIn, myIn, keyCode))
        {
            return true;
        }
        return this.attachedToScreen.mouseReleased(mxIn, myIn, keyCode);
    }

    @Override
    public boolean keyReleased(final int pKeyCode, final int pScanCode, final int pModifiers)
    {
        if (super.keyReleased(pKeyCode, pScanCode, pModifiers))
        {
            return true;
        }
        return this.attachedToScreen.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void mouseMoved(final double pMouseX, final double pMouseY)
    {
        super.mouseMoved(pMouseX, pMouseY);
        this.attachedToScreen.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public void onClose()
    {
        super.onClose();
        this.attachedToScreen.onClose();
    }

    @Override
    public void tick()
    {
        super.tick();
        this.attachedToScreen.tick();
    }
}
