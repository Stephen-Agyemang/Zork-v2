package com.mygroup;

import junit.framework.TestCase;

/** Regression coverage for movement-driven survival and dining confirmation. */
public class GameMechanicsTest extends TestCase {

    public void testJumpConsumesItsFullMovementCostForHunger() {
        GameEngine engine = new GameEngine();
        engine.createWorld();

        engine.processCommand("go south"); // East College -> Olin (1 move)
        engine.processCommand("go south"); // Olin -> CDI (1 move)
        engine.processCommand("jump");     // CDI -> Lilly (2 moves)
        String output = engine.processCommand("go west"); // fifth move

        assertTrue(output.contains("You eat StarterSnack"));
        assertFalse(engine.getGameState().getInventory().hasItem("StarterSnack"));
    }

    public void testSecondDiningHallRequiresConfirmationBeforePenalty() {
        GameEngine engine = new GameEngine();
        engine.createWorld();

        engine.processCommand("go east");
        assertTrue(engine.processCommand("go south").contains("type the command again"));
        engine.processCommand("go south"); // Enter Hoover after confirmation.
        engine.processCommand("go north");

        String warning = engine.processCommand("go east");
        assertTrue(warning.contains("SECOND dining location"));
        assertEquals("The warning must not move or penalize the player.", 3, engine.getGameState().getMoveCount());
        assertEquals(0, engine.getGameState().getPoints());

        String entry = engine.processCommand("go east");
        assertTrue(entry.contains("+10 moves, -25 points"));
        assertEquals(14, engine.getGameState().getMoveCount());
        assertEquals(-25, engine.getGameState().getPoints());
    }
}
