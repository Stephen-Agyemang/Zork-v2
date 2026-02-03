package com.mygroup;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameState {

    // Core Stats
    public int moveCount;
    public int points;

    // Location
    private Location currLocation;
    private Location lillyLocation;
    public HashSet<String> visitedLocations;
    private Location helpDropLocation;

    public boolean visitedHoover;
    public boolean visitedDuck;

    // Inventory / Resources
    private final ContainerItem inventory;
    public boolean couponsUnlimited;
    public String foodLockChoice; // "hoover" or "duck" once chosen for food

    // Food and survival
    private int hungerMoveCounter;
    private int foodDecayCounter;

    // Fun Typing Challenge
    private int wrongCommandCount;
    private boolean typingChallengeActive;
    private String[] typingWords;
    private int typingFails;
    private String typingContext; // "penalty" or "treadmill"

    // Quests and Tasks

    // Music
    private boolean sashaCalled;
    private Item pendingGuitar;
    private boolean musicTaskComplete;

    // Salmon and Snake
    private boolean salmonTaskComplete;
    private boolean snakeTaskComplete;

    // Macbook
    private boolean macbookTaskComplete;

    // Treadmill
    private boolean treadmillUsed;

    // DNA Task
    private boolean dnaTaskActive;
    private int dnaMovesLeft;
    private boolean dnaTaskComplete;
    private boolean dnaCountdownRunning;

    // Endgame
    private boolean finaleShown;
    private boolean bothDiningVisited; // Secret: true if both Hoover AND Duck visited
    private boolean dangerousBonusAwarded; // Prevent awarding +50 multiple times

    // NPCs and Quests
    private final Map<String, NPC> npcs;
    private final Map<String, Quest> quests;

    public GameState() {
        // Core Stats
        this.moveCount = 0;
        this.points = 0;

        // Inventory
        this.inventory = new ContainerItem("BackPack", "Container", "Player personal backpack");

        // Locations tracking
        this.currLocation = null; // Set by GameEngine when world is created
        this.visitedLocations = new HashSet<>();
        this.lillyLocation = null;
        this.helpDropLocation = null;
        this.visitedHoover = false;
        this.visitedDuck = false;

        // Food and Survival
        this.couponsUnlimited = false;
        this.hungerMoveCounter = 0;
        this.foodLockChoice = null;
        this.foodDecayCounter = 0;

        // Typing Challenge
        this.wrongCommandCount = 0;
        this.typingChallengeActive = false;
        this.typingWords = new String[0];
        this.typingFails = 0;
        this.typingContext = null;

        // Quests and Tasks
        this.treadmillUsed = false;
        this.salmonTaskComplete = false;
        this.snakeTaskComplete = false;
        this.macbookTaskComplete = false;
        this.sashaCalled = false;
        this.musicTaskComplete = false;

        // DNA Task
        this.dnaTaskActive = false;
        this.dnaMovesLeft = 0;
        this.dnaTaskComplete = false;
        this.dnaCountdownRunning = false;

        // Pending Item
        this.pendingGuitar = new Item("GlazedGuitar", "Music",
                "A four string guitar with a glowing green string, waiting for its true player.");

        // Endgame
        this.finaleShown = false;
        this.bothDiningVisited = false;
        this.dangerousBonusAwarded = false;

        // NPCs and Quests
        this.npcs = new HashMap<>();
        this.quests = new HashMap<>();
    }

    // Getter and Setter methods for all member variables

    // Location related methods (Location handling)
    public Location getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(Location newLocation) {
        this.currLocation = newLocation;
    }

    public boolean hasVisitedLocation(Location location) {
        if (location == null || location.getName() == null) {
            return false;
        }
        return visitedLocations.contains(location.getName());
    }

    // Alias used by router
    public void addVisitedLocation(Location location) {
        visitLocation(location);
    }

    public void visitLocation(Location location) {
        if (location != null && location.getName() != null) {
            visitedLocations.add(location.getName());
        }
    }

    public Location getHelpDropLocation() {
        return helpDropLocation;
    }

    public void setHelpDropLocation(Location location) {
        this.helpDropLocation = location;
    }

    // Inventory related methods (Inventory management)
    public ContainerItem getInventory() {
        return inventory;
    }

    public void addItemToInventory(Item item) {
        inventory.addItem(item);
    }

    public Item removeItemFromInventory(String itemName) {
        return inventory.removeItem(itemName);
    }

    // Move count and point methods
    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public void addMoveCount(int count) {
        this.moveCount += count;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public void subtractPoints(int pointsToSubtract) {
        this.points -= pointsToSubtract;
    }

    // Food and Survival methods
    public void visitHoover() {
        visitedHoover = true;
    }

    public void visitDuck() {
        visitedDuck = true;
    }

    public boolean canEat(String choice) {
        // If not yet locked, only lock to a non-empty choice
        if (foodLockChoice == null || foodLockChoice.isEmpty()) {
            if (choice != null && !choice.isEmpty()) {
                foodLockChoice = choice;
                return true;
            }
            return false;
        }
        // Already locked, compare safely
        return choice != null && foodLockChoice.equalsIgnoreCase(choice);
    }

    public void incrementHungerCounter() {
        hungerMoveCounter++;
    }

    public int getHungerCounter() {
        return hungerMoveCounter;
    }

    public void resetHungerCounter() {
        hungerMoveCounter = 0;
    }

    public void eatFood() {
        if (foodLockChoice != null && !foodLockChoice.isEmpty()) {
            hungerMoveCounter = 0;
            foodDecayCounter = 0;
        }
    }

    public void resetFoodDecayCounter() {
        foodDecayCounter = 0;
    }

    public void incrementFoodDecayCounter() {
        foodDecayCounter++;
    }

    public boolean isVisitedHoover() {
        return visitedHoover;
    }

    public boolean isVisitedDuck() {
        return visitedDuck;
    }

    public String getFoodLockChoice() {
        return foodLockChoice;
    }

    public int getFoodDecayCounter() {
        return foodDecayCounter;
    }

    public boolean isCouponsUnlimited() {
        return couponsUnlimited;
    }

    public void setCouponsUnlimited(boolean unlimited) {
        this.couponsUnlimited = unlimited;
    }

    public void setFoodLockChoice(String choice) {
        this.foodLockChoice = choice;
    }

    // Typing Challenge methods
    public void startTypingChallenge(String context) {
        typingChallengeActive = true;
        typingFails = 0;
        typingContext = context;
    }

    public void failTypingAttempt() {
        typingFails++;
    }

    public boolean isTypingChallengeActive() {
        return typingChallengeActive;
    }

    public void endTypingChallenge() {
        typingChallengeActive = false;
        typingContext = null;
    }

    public String getRandomTypingWord() {
        if (typingWords == null || typingWords.length == 0) {
            return "default";
        }
        int index = (int) (Math.random() * typingWords.length);
        return typingWords[index];
    }

    public void setTypingWords(String[] words) {
        if (words != null && words.length > 0) {
            this.typingWords = words;
        }
    }

    public String[] getTypingWords() {
        return typingWords;
    }

    public int getTypingFails() {
        return typingFails;
    }

    public String getTypingContext() {
        return typingContext;
    }

    public int getWrongCommandCount() {
        return wrongCommandCount;
    }

    public void incrementWrongCommandCount() {
        wrongCommandCount++;
    }

    public void resetWrongCommandCount() {
        wrongCommandCount = 0;
    }

    // Quests and Task methods
    public void completeSalmonTask() {
        salmonTaskComplete = true;
    }

    public boolean isSalmonTaskComplete() {
        return salmonTaskComplete;
    }

    public void completeSnakeTask() {
        snakeTaskComplete = true;
    }

    public boolean isSnakeTaskComplete() {
        return snakeTaskComplete;
    }

    public void completeMacbookTask() {
        macbookTaskComplete = true;
    }

    public boolean isMacbookTaskComplete() {
        return macbookTaskComplete;
    }

    public void callSasha() {
        sashaCalled = true;
    }

    public boolean isSashaCalled() {
        return sashaCalled;
    }

    public boolean allCoreQuestsDone() {
        return musicTaskComplete && dnaTaskComplete && salmonTaskComplete && snakeTaskComplete && macbookTaskComplete
                && treadmillUsed;
    }

    // Treadmill and Music methods
    public void useTreadmill() {
        treadmillUsed = true;
    }

    public boolean isTreadmillUsed() {
        return treadmillUsed;
    }

    public void completeMusicTask() {
        musicTaskComplete = true;
    }

    public boolean isMusicTaskComplete() {
        return musicTaskComplete;
    }

    // Pending Items and References
    public void setPendingGuitar(Item guitar) {
        pendingGuitar = guitar;
    }

    public Item getPendingGuitar() {
        return pendingGuitar;
    }

    public void setLillyLocation(Location location) {
        lillyLocation = location;
    }

    public Location getLillyLocation() {
        return lillyLocation;
    }

    // DNA Task methods
    public void startDNATask(int movesAllowed) {
        dnaTaskActive = true;
        dnaMovesLeft = movesAllowed;
        dnaCountdownRunning = true;
    }

    public void decrementDNAMoves() {
        if (dnaMovesLeft > 0) {
            dnaMovesLeft--;
        }
        if (dnaMovesLeft == 0) {
            dnaCountdownRunning = false;
        }
    }

    public void completeDNATask() {
        dnaTaskComplete = true;
        dnaTaskActive = false;
        dnaCountdownRunning = false;
    }

    public boolean isDnaTaskActive() {
        return dnaTaskActive;
    }

    public int getDnaMovesLeft() {
        return dnaMovesLeft;
    }

    public boolean isDnaTaskComplete() {
        return dnaTaskComplete;
    }

    public boolean isDnaCountdownRunning() {
        return dnaCountdownRunning;
    }

    // Endgame methods
    public void showFinale() {
        finaleShown = true;
    }

    public boolean isFinaleShown() {
        return finaleShown;
    }

    // NPC helpers
    public void addNpc(NPC npc) {
        if (npc != null && npc.getId() != null) {
            npcs.put(npc.getId().toLowerCase(), npc);
        }
    }

    public NPC getNpc(String id) {
        if (id == null)
            return null;
        return npcs.get(id.toLowerCase());
    }

    public NPC findNpcByName(String name) {
        if (name == null)
            return null;
        String normalized = name.toLowerCase();
        return npcs.values().stream()
                .filter(n -> n.getName() != null && n.getName().toLowerCase().equals(normalized))
                .findFirst()
                .orElse(null);
    }

    public void moveNpc(String id, String newLocationName) {
        NPC npc = getNpc(id);
        if (npc != null) {
            npc.setLocationName(newLocationName);
        }
    }

    // Quest helpers
    public void addQuest(Quest quest) {
        if (quest != null && quest.getId() != null) {
            quests.put(quest.getId().toLowerCase(), quest);
        }
    }

    public Quest getQuest(String id) {
        if (id == null)
            return null;
        return quests.get(id.toLowerCase());
    }

    public boolean isQuestComplete(String id) {
        Quest quest = getQuest(id);
        return quest != null && quest.getStatus() == Quest.Status.COMPLETED;
    }

    public void startQuest(String id) {
        Quest quest = getQuest(id);
        if (quest != null && quest.getStatus() == Quest.Status.NOT_STARTED) {
            quest.setStatus(Quest.Status.IN_PROGRESS);
        }
    }

    public void completeQuest(String id) {
        Quest quest = getQuest(id);
        if (quest != null) {
            quest.setStatus(Quest.Status.COMPLETED);
        }
    }

    // Hidden dangerous bonus tracking
    public void markBothDiningVisited() {
        bothDiningVisited = true;
    }

    public boolean isBothDiningVisited() {
        return bothDiningVisited;
    }

    public void awardDangerousBonus() {
        dangerousBonusAwarded = true;
    }

    public boolean isDangerousBonusAwarded() {
        return dangerousBonusAwarded;
    }
}