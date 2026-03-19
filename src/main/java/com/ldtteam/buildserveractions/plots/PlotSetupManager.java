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
        CENTER_ROAD_SPACING("center-road", DEFAULT_CENTER_ROAD_SPACING),
        PLOT_ROAD_SPACING("plot-road", DEFAULT_PLOT_ROAD_SPACING),
        NORTH_OFFSET("north-offset", DEFAULT_NORTH_OFFSET),
        SOUTH_OFFSET("south-offset", DEFAULT_SOUTH_OFFSET),
        ROAD_BLOCK("road-block", DEFAULT_ROAD_BLOCK),
        Y_LEVEL("y-level", DEFAULT_Y_LEVEL),
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
         * @return the command name used in /plots setup <name>.
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

        // Getters and setters for configuration values

        public int getCenterRoadSpacing()
        {
            return centerRoadSpacing;
        }

        public void setCenterRoadSpacing(final int centerRoadSpacing)
        {
            this.centerRoadSpacing = centerRoadSpacing;
        }

        public int getPlotRoadSpacing()
        {
            return plotRoadSpacing;
        }

        public void setPlotRoadSpacing(final int plotRoadSpacing)
        {
            this.plotRoadSpacing = plotRoadSpacing;
        }

        public int getNorthOffset()
        {
            return northOffset;
        }

        public void setNorthOffset(final int northOffset)
        {
            this.northOffset = northOffset;
        }

        public int getSouthOffset()
        {
            return southOffset;
        }

        public void setSouthOffset(final int southOffset)
        {
            this.southOffset = southOffset;
        }

        public BlockState getRoadBlock()
        {
            return roadBlock;
        }

        public void setRoadBlock(final BlockState roadBlock)
        {
            this.roadBlock = roadBlock;
        }

        public int getYLevel()
        {
            return yLevel;
        }

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
