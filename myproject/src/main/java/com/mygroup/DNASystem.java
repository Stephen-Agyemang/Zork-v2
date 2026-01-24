package com.mygroup;

/**
 * Handles DNA delivery quest mechanics:
 * - DNA countdown timer (3 moves to deliver from Olin to Julian)
 * - Spoilage prevention
 * - Quest completion rewards
 */
public class DNASystem {
    private final GameState state;

    public DNASystem(GameState state) {
        this.state = state;
    }

    /**
     * Start DNA countdown if needed when leaving Olin with DNA
     * DNA must be delivered to Julian within 3 moves or it spoils
     */
    public void startCountdownIfNeeded(Location previousLocation) {
        if (state.isDnaTaskActive() && !state.isDnaCountdownRunning() && 
            previousLocation.getName().equalsIgnoreCase("Olin")) {
            state.startDNATask(3);
        }
    }

    /**
     * Tick down the DNA timer and check for spoilage
     * Returns warning message if DNA is spoiling soon
     */
    public String tickCountdown() {
        if (!state.isDnaCountdownRunning()) {
            return "";
        }
        state.decrementDNAMoves();
        if (state.getDnaMovesLeft() <= 0) {
            // DNA spoiled
            state.getInventory().removeItem("BatMan's-D   NA");
            state.completeDNATask();
            state.subtractPoints(10);
            return "\nThe DNA was exposed too long and is destroyed (-10 points).";
        }
        if (state.getDnaMovesLeft() == 1) {
            return "\nHurry! The DNA will spoil after one more move.";
        }
        return "\nDNA sample integrity: " + state.getDnaMovesLeft() + " moves left.";
    }
}
