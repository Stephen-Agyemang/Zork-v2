package com.mygroup;

import org.springframework.stereotype.Component;

@Component
public class GameEngine {

    // Core game state managed by GameEngine
    private final GameState state;

    // System handling specific game mechanics
    private final FoodSystem foodSystem;
    private final TypingChallengeSystem typingChallengeSystem;
    private final DNASystem dnaSystem;
    private final QuestSystem questSystem;
    private final CommandRouter commandRouter;
    private boolean worldBuilt;
    private boolean debugLogging;

    public GameEngine() {
        this(new GameState());
    }

    public GameState getGameState() {
        return this.state;
    }

    public GameEngine(GameState state) {
        this.state = state;
        this.foodSystem = new FoodSystem(state);
        this.typingChallengeSystem = new TypingChallengeSystem(state);
        this.dnaSystem = new DNASystem(state);
        this.questSystem = new QuestSystem(state);
        this.commandRouter = new CommandRouter(state, foodSystem, typingChallengeSystem, dnaSystem, questSystem);
        this.worldBuilt = false;
        this.debugLogging = false;
    }

    public final void createWorld() {
        WorldBuilder builder = new WorldBuilder(state);
        builder.buildWorld();
        worldBuilt = true;
        debug("World built and ready.");
    }

    public void setDebugLogging(boolean enabled) {
        this.debugLogging = enabled;
    }

    public String processCommand(String userInput) {
        if (!worldBuilt) {
            return "Game world is not initialized. Call createWorld() before processing commands.";
        }
        String locationName = (state.getCurrLocation() != null) ? state.getCurrLocation().getName() : "Unknown";
        debug("Processing command: " + userInput + " | moves=" + state.getMoveCount() + " | location=" + locationName);
        try {
            return commandRouter.routeCommand(userInput);
        } catch (Exception ex) {
            debug("Command error: " + ex.getMessage());
            return "Unexpected error while processing command. Please try again.";
        }
    }

    // Expose end-game summary via quest system
    public String getEndGameSummary() {
        return questSystem.getEndGameSummary();
    }

    private void debug(String msg) {
        if (debugLogging) {
            System.out.println("[GameEngine] " + msg);
        }
    }
}
