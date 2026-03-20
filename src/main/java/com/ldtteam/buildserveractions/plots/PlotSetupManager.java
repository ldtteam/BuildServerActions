package com.ldtteam.buildserveractions.plots;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the guided setup process for plot configuration.
 * <p>
 * Tracks per-player setup sessions and stores pending commands that triggered
 * the setup wizard. Sessions are stored in-memory and cleared on disconnect.
 */
public final class PlotSetupManager
{
    /**
     * Default value for center road spacing.
     */
    public static final int DEFAULT_CENTER_ROAD_SPACING = 5;

    /**
     * Default value for plot road spacing.
     */
    public static final int DEFAULT_PLOT_ROAD_SPACING = 3;

    /**
     * Default value for north (official) direction offset.
     */
    public static final int DEFAULT_NORTH_OFFSET = 3;

    /**
     * Default value for south (unofficial) direction offset.
     */
    public static final int DEFAULT_SOUTH_OFFSET = 3;

    /**
     * Default road block state.
     */
    public static final BlockState DEFAULT_ROAD_BLOCK = Blocks.STONE_BRICKS.defaultBlockState();

    /**
     * Default Y level for plots (based on superflat world).
     */
    public static final int DEFAULT_Y_LEVEL = -61;

    private static PlotSetupManager instance;

    /**
     * Map of player UUIDs to their active setup sessions.
     */
    private final Map<UUID, SetupSession> activeSessions = new HashMap<>();

    private PlotSetupManager()
    {
    }

    /**
     * Gets the singleton instance of the setup manager.
     *
     * @return the setup manager instance.
     */
    public static PlotSetupManager getInstance()
    {
        if (instance == null)
        {
            instance = new PlotSetupManager();
        }
        return instance;
    }

    /**
     * Starts a new setup session for a player.
     *
     * @param playerId       the UUID of the player.
     * @param pendingCommand the command to execute after setup completes, or null.
     * @return the newly created session.
     */
    public SetupSession startSession(final UUID playerId, @Nullable final String pendingCommand)
    {
        final SetupSession session = new SetupSession(pendingCommand);
        activeSessions.put(playerId, session);
        return session;
    }

    /**
     * Gets the active session for a player.
     *
     * @param playerId the UUID of the player.
     * @return the session, or null if no active session exists.
     */
    @Nullable
    public SetupSession getSession(final UUID playerId)
    {
        return activeSessions.get(playerId);
    }

    /**
     * Checks if a player has an active setup session.
     *
     * @param playerId the UUID of the player.
     * @return true if a session exists, false otherwise.
     */
    public boolean hasSession(final UUID playerId)
    {
        return activeSessions.containsKey(playerId);
    }

    /**
     * Removes the setup session for a player.
     *
     * @param playerId the UUID of the player.
     */
    public void removeSession(final UUID playerId)
    {
        activeSessions.remove(playerId);
    }

    /**
     * Clears all active setup sessions.
     * Called when setup is completed to clean up any dangling sessions from other players.
     */
    public void clearAllSessions()
    {
        activeSessions.clear();
    }

    /**
     * The steps in the setup wizard.
     */
    public enum SetupStep
    {
        /**
         * Step for configuring the center road spacing between plot rows.
         */
        CENTER_ROAD_SPACING("center-road", DEFAULT_CENTER_ROAD_SPACING),
        /**
         * Step for configuring the road spacing between individual plots.
         */
        PLOT_ROAD_SPACING("plot-road", DEFAULT_PLOT_ROAD_SPACING),
        /**
         * Step for configuring the north (official) direction offset.
         */
        NORTH_OFFSET("north-offset", DEFAULT_NORTH_OFFSET),
        /**
         * Step for configuring the south (unofficial) direction offset.
         */
        SOUTH_OFFSET("south-offset", DEFAULT_SOUTH_OFFSET),
        /**
         * Step for configuring the road block type.
         */
        ROAD_BLOCK("road-block", DEFAULT_ROAD_BLOCK),
        /**
         * Step for configuring the Y level for plots.
         */
        Y_LEVEL("y-level", DEFAULT_Y_LEVEL),
        /**
         * Final confirmation step to apply all settings.
         */
        CONFIRM("confirm", null);

        private final String commandName;
        private final Object defaultValue;

        SetupStep(final String commandName, final Object defaultValue)
        {
            this.commandName = commandName;
            this.defaultValue = defaultValue;
        }

        /**
         * Gets the command name for this step.
         *
         * @return the command name used in /plots setup.
         */
        public String getCommandName()
        {
            return commandName;
        }

        /**
         * Gets the default value for this step.
         *
         * @return the default value, or null if not applicable.
         */
        @Nullable
        public Object getDefaultValue()
        {
            return defaultValue;
        }

        /**
         * Gets the next step in the wizard.
         *
         * @return the next step, or null if this is the last step.
         */
        @Nullable
        public SetupStep next()
        {
            final SetupStep[] values = values();
            final int nextOrdinal = this.ordinal() + 1;
            if (nextOrdinal < values.length)
            {
                return values[nextOrdinal];
            }
            return null;
        }

        /**
         * Gets the step number (1-indexed) for display.
         *
         * @return the step number.
         */
        public int getStepNumber()
        {
            return ordinal() + 1;
        }

        /**
         * Gets the total number of steps.
         *
         * @return the total step count.
         */
        public static int getTotalSteps()
        {
            return values().length;
        }
    }

    /**
     * Represents an active setup session for a player.
     */
    public static final class SetupSession
    {
        private SetupStep currentStep = SetupStep.CENTER_ROAD_SPACING;
        private int centerRoadSpacing = DEFAULT_CENTER_ROAD_SPACING;
        private int plotRoadSpacing = DEFAULT_PLOT_ROAD_SPACING;
        private int northOffset = DEFAULT_NORTH_OFFSET;
        private int southOffset = DEFAULT_SOUTH_OFFSET;
        private BlockState roadBlock = DEFAULT_ROAD_BLOCK;
        private int yLevel = DEFAULT_Y_LEVEL;

        @Nullable
        private final String pendingCommand;

        /**
         * Creates a new setup session.
         *
         * @param pendingCommand the command to execute after setup, or null.
         */
        public SetupSession(@Nullable final String pendingCommand)
        {
            this.pendingCommand = pendingCommand;
        }

        /**
         * Gets the current step in the setup process.
         *
         * @return the current step.
         */
        public SetupStep getCurrentStep()
        {
            return currentStep;
        }

        /**
         * Advances to the next step.
         *
         * @return true if advanced, false if already at the last step.
         */
        public boolean advanceStep()
        {
            final SetupStep next = currentStep.next();
            if (next != null)
            {
                currentStep = next;
                return true;
            }
            return false;
        }

        /**
         * Resets to the first step, keeping current values as the new defaults.
         */
        public void restartFromBeginning()
        {
            currentStep = SetupStep.CENTER_ROAD_SPACING;
        }

        /**
         * Gets the pending command to execute after setup.
         *
         * @return the pending command, or null if none.
         */
        @Nullable
        public String getPendingCommand()
        {
            return pendingCommand;
        }

        /**
         * Gets the center road spacing value.
         *
         * @return the center road spacing.
         */
        public int getCenterRoadSpacing()
        {
            return centerRoadSpacing;
        }

        /**
         * Sets the center road spacing value.
         *
         * @param centerRoadSpacing the center road spacing to set.
         */
        public void setCenterRoadSpacing(final int centerRoadSpacing)
        {
            this.centerRoadSpacing = centerRoadSpacing;
        }

        /**
         * Gets the plot road spacing value.
         *
         * @return the plot road spacing.
         */
        public int getPlotRoadSpacing()
        {
            return plotRoadSpacing;
        }

        /**
         * Sets the plot road spacing value.
         *
         * @param plotRoadSpacing the plot road spacing to set.
         */
        public void setPlotRoadSpacing(final int plotRoadSpacing)
        {
            this.plotRoadSpacing = plotRoadSpacing;
        }

        /**
         * Gets the north offset value.
         *
         * @return the north offset.
         */
        public int getNorthOffset()
        {
            return northOffset;
        }

        /**
         * Sets the north offset value.
         *
         * @param northOffset the north offset to set.
         */
        public void setNorthOffset(final int northOffset)
        {
            this.northOffset = northOffset;
        }

        /**
         * Gets the south offset value.
         *
         * @return the south offset.
         */
        public int getSouthOffset()
        {
            return southOffset;
        }

        /**
         * Sets the south offset value.
         *
         * @param southOffset the south offset to set.
         */
        public void setSouthOffset(final int southOffset)
        {
            this.southOffset = southOffset;
        }

        /**
         * Gets the road block state.
         *
         * @return the road block state.
         */
        public BlockState getRoadBlock()
        {
            return roadBlock;
        }

        /**
         * Sets the road block state.
         *
         * @param roadBlock the road block state to set.
         */
        public void setRoadBlock(final BlockState roadBlock)
        {
            this.roadBlock = roadBlock;
        }

        /**
         * Gets the Y level value.
         *
         * @return the Y level.
         */
        public int getYLevel()
        {
            return yLevel;
        }

        /**
         * Sets the Y level value.
         *
         * @param yLevel the Y level to set.
         */
        public void setYLevel(final int yLevel)
        {
            this.yLevel = yLevel;
        }

        /**
         * Builds the PlotSettings from the session configuration.
         *
         * @return the configured PlotSettings.
         */
        public PlotSettings buildSettings()
        {
            return new PlotSettings(centerRoadSpacing, plotRoadSpacing, yLevel, roadBlock);
        }
    }
}
