package com.mygroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Handles all typing challenge mechanics:
 * - Challenge initiation
 * - Word pool generation
 * - Answer evaluation
 * - Treadmill typing sprint
 * - Penalty typing challenges
 */
public class TypingChallengeSystem {
    private final GameState state;
    private final RandomWordService wordService;

    public TypingChallengeSystem(GameState state) {
        this.state = state;
        this.wordService = new RandomWordService();
    }

    /**
     * Normalize a word for typing comparison (remove spaces/special chars,
     * lowercase)
     */
    private String normalizeWord(String word) {
        return word.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Start a typing challenge with a given context and word count
     * Builds word pool from game items
     */
    public void startChallenge(String context, int wordCount) {
        state.startTypingChallenge(context);

        // Build word pool dynamically from game state
        ArrayList<String> pool = new ArrayList<>();

        // Add items from current location
        for (Item item : state.getCurrLocation().getItems()) {
            String normalized = normalizeWord(item.getName());
            if (normalized.length() > 3) {
                pool.add(normalized);
            }
        }

        // Add items from inventory
        for (Item item : state.getInventory().getItems()) {
            String normalized = normalizeWord(item.getName());
            if (normalized.length() > 3) {
                pool.add(normalized);
            }
        }

        // Supplement with words from the external API to verify it works
        for (int i = 0; i < 2; i++) {
            String apiWord = normalizeWord(wordService.fetchRandomWord());
            if (apiWord.length() > 2) {
                pool.add(apiWord);
            }
        }

        // Ensure we have enough words
        if (pool.size() < wordCount) {
            pool.addAll(Arrays.asList("campus", "explore", "adventure", "quest", "journey", "challenge"));
        }

        // Pick random words from the pool
        Random r = new Random();
        String[] words = new String[wordCount];
        for (int i = 0; i < wordCount; i++) {
            words[i] = pool.get(r.nextInt(pool.size()));
        }
        state.setTypingWords(words);
    }

    /**
     * Evaluate the player's typing attempt
     * 
     * @return result message
     */
    public String evaluateAttempt(String userInput) {
        String[] parts = userInput.trim().split("\\s+");
        int correct = 0;

        // Check each required word to see if user typed it
        for (String requiredWord : state.getTypingWords()) {
            for (String userWord : parts) {
                String normalizedUserWord = normalizeWord(userWord);
                if (requiredWord.equals(normalizedUserWord)) {
                    correct++;
                    break;
                }
            }
        }

        boolean success = correct >= 3;
        if (success) {
            state.endTypingChallenge();
            state.resetWrongCommandCount();
            if ("treadmill".equals(state.getTypingContext())) {
                state.useTreadmill();
                state.addPoints(15);
                return "You crushed the treadmill typing sprint! +15 points." + maybeFinaleMessage();
            }
            // Penalty typing challenge - different messages based on whether it's first try
            // or recovery
            if (state.getTypingFails() == 0) {
                return "Impressive! Looks like those fingers know their way around a keyboard after all. " + correct
                        + "/" + state.getTypingWords().length + " correct. Keep exploring!";
            } else {
                return "Nice recovery! You typed " + correct + "/" + state.getTypingWords().length
                        + " words correctly. Back to the game.";
            }
        }

        state.failTypingAttempt();
        String scold = switch (state.getTypingFails()) {
            case 1 -> "Come on, even freshmen can type better than that!";
            case 2 -> "Seriously? The registrar's office types faster than you.";
            default -> "At this rate, you'll graduate before learning to type.";
        };
        if ("treadmill".equals(state.getTypingContext())) {
            scold = "Keep running!";
        }
        return scold + " You got " + correct + "/" + state.getTypingWords().length
                + " correct. Type the words again or type 'skip' to move on: "
                + String.join(", ", state.getTypingWords());
    }

    /**
     * Get the typing prompt text
     */
    public String getPrompt() {
        if ("treadmill".equals(state.getTypingContext())) {
            return "Treadmill sprint! Type at least 3 of these words in one line, or type 'skip' to hop off: "
                    + String.join(", ", state.getTypingWords());
        }
        return "Typing challenge! Type at least 3 of these words in one line to continue, or type 'skip' to ignore: "
                + String.join(", ", state.getTypingWords());
    }

    /**
     * Check if finale message should be shown
     */
    private String maybeFinaleMessage() {
        if (state.allCoreQuestsDone() && !state.isFinaleShown()) {
            state.showFinale();
            state.addPoints(10);
            return "\nCampus whispers: every quest is wrapped. You feel legendary. (+10 points)";
        }
        return "";
    }
}
