package com.mygroup;

/**
 * Handles all food-related game logic:
 * - Food inventory management
 * - Hunger mechanics
 * - Food decay
 * - Dining location restrictions
 * - Coupon consumption
 */
public class FoodSystem {
    private final GameState state;

    public FoodSystem(GameState state) {
        this.state = state;
    }

    /**
     * Count food items currently in inventory
     */
    public int getFoodCount() {
        int count = 0;
        for (Item item : state.getInventory().getItems()) {
            if (isFoodItem(item)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if an item is a food item (Meal or Food type)
     */
    public boolean isFoodItem(Item item) {
        return item.getType().equalsIgnoreCase("food") || item.getType().equalsIgnoreCase("meal");
    }

    /**
     * Check if a location is a dining location (where food can be purchased)
     */
    public boolean isDiningLocation(Location loc) {
        String n = loc.getName().toLowerCase();
        return n.equals("hoover") || n.equals("the fluttering duck");
    }

    /**
     * Consume one food coupon from inventory
     * @return true if coupon was consumed, false if none available
     */
    public boolean consumeCoupon() {
        for (Item item : state.getInventory().getItems()) {
            if (item.getType().equalsIgnoreCase("coupon")) {
                state.getInventory().removeItem(item.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * Count food coupons in inventory
     */
    public int getCouponCount() {
        int count = 0;
        for (Item item : state.getInventory().getItems()) {
            if (item.getType().equalsIgnoreCase("coupon")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Apply hunger effects after a move
     * - Increment hunger counter
     * - Consume food if hungry
     * - Apply move penalty if no food available
     * - Check for food decay
     */
    public String applyHungerAfterMove() {
        state.incrementHungerCounter();
        state.incrementFoodDecayCounter();
        StringBuilder message = new StringBuilder();

        // Check for hunger - player must eat if counter reaches 5
        if (state.getHungerCounter() >= 5) {
            state.resetHungerCounter();
            // Try to consume one food
            for (Item item : state.getInventory().getItems()) {
                if (isFoodItem(item)) {
                    state.getInventory().removeItem(item.getName());
                    message.append("\nYou eat ").append(item.getName()).append(" to keep your energy up.");
                    return message.toString();
                }
            }
            // No food available -> move penalty
            state.addMoveCount(2);
            message.append("\nYou're starving—moving slower (+2 moves).");
        }

        // Check for food decay - food spoils after 5 moves if not eaten
        if (state.getFoodDecayCounter() >= 5) {
            state.resetFoodDecayCounter();
            for (Item item : state.getInventory().getItems()) {
                if (isFoodItem(item)) {
                    state.getInventory().removeItem(item.getName());
                    message.append("\nOne of your foods spoiled and was discarded.");
                    return message.toString();
                }
            }
        }

        return message.toString();
    }

    /**
     * Check dining location penalties
     * Rule: Can only visit ONE dining location (Hoover OR Duck, not both)
     * Visiting second location: +10 moves, -25 points
     * SECRET: Getting food from BOTH locations unlocks potential for +50 dangerous bonus if all quests complete
     */
    public String checkDiningLocationPenalty() {
        String locationName = state.getCurrLocation().getName().toLowerCase();
        
        if (locationName.equals("hoover")) {
            if (!state.isVisitedHoover() && state.isVisitedDuck()) {
                // Second dining location visited - apply penalty
                state.addMoveCount(10);
                state.subtractPoints(25);
                state.visitHoover();
                // Only mark both dining if they've gotten food from first location too
                return "\n⚠️  WARNING: You broke the dining rule! +10 moves, -25 points.";
            } else if (!state.isVisitedHoover() && !state.isVisitedDuck()) {
                // First dining location - warn player
                state.visitHoover();
                return "\n📍 Note: There's another dining location on campus (The Fluttering Duck). Choose wisely which one to visit—you should only visit ONE!";
            }
        } else if (locationName.equals("the fluttering duck")) {
            if (!state.isVisitedDuck() && state.isVisitedHoover()) {
                // Second dining location visited - apply penalty
                state.addMoveCount(10);
                state.subtractPoints(25);
                state.visitDuck();
                // Only mark both dining if they've gotten food from first location too
                return "\n⚠️  WARNING: You broke the dining rule! +10 moves, -25 points.";
            } else if (!state.isVisitedDuck() && !state.isVisitedHoover()) {
                // First dining location - warn player
                state.visitDuck();
                return "\n📍 Note: There's another dining location on campus (Hoover). Choose wisely which one to visit—you should only visit ONE!";
            }
        }
        return "";
    }

    /**
     * Mark that player got food from the second dining location (triggers dangerous bonus potential)
     * Call this when they successfully take food from the SECOND dining place they visit
     */
    public void markFoodFromSecondDining() {
        String locationName = state.getCurrLocation().getName().toLowerCase();
        // Only mark if they've visited both locations and are getting food from the second one
        if ((locationName.equals("hoover") && state.isVisitedDuck() && state.isVisitedHoover()) ||
            (locationName.equals("the fluttering duck") && state.isVisitedHoover() && state.isVisitedDuck())) {
            state.markBothDiningVisited();
        }
    }
}
