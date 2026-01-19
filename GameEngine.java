import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;


public class GameEngine {

    private Location currLocation;
    private ContainerItem myInventory;
    private int moveCount;
    private int points;
    private HashSet<String> visitedLocations;
    private Location helpDropLocation;
    private boolean visitedHoover;
    private boolean visitedDuck;
    private boolean sashaCalled;
    private boolean couponsUnlimited;
    private int hungerMoveCounter;
    private String foodLockChoice; // "hoover" or "duck" once chosen for food
    private int wrongCommandCount;
    private boolean typingChallengeActive;
    private String[] typingWords;
    private int typingFails;
    private boolean treadmillUsed;
    private boolean salmonTaskComplete;
    private boolean snakeTaskComplete;
    private boolean macbookTaskComplete;
    private boolean dnaTaskActive;
    private int dnaMovesLeft;
    private boolean dnaTaskComplete;
    private boolean dnaCountdownRunning;
    private Item pendingGuitar;
    private Location lillyLocation;
    private boolean musicTaskComplete;
    private int foodDecayCounter;
    private String typingContext; // "penalty" or "treadmill"
    private boolean finaleShown;

    public void createWorld() {

        Location julian = new Location("Julian", "Science Building filled with cool nerdy stuff duh!");
        Location hoover = new Location("Hoover", "The place for Food! :)");
        Location olin = new Location("Olin", "Biology building for the super nerds!");
        Location gcpa = new Location("GCPA", "The Musical and theatrical place on campus!");
        Location roylibrary = new Location("Roy Library", "It's just a quiet place for nerds to study again dude!");
        Location cdi = new Location("CDI", "Center for Diversity and Inclusion, a place to celebrate diversity on campus.");
        Location lilly = new Location("Lilly Building", "The place for general healthy living and wellness, I mean gym, athletes rooms, and health center.");
        lillyLocation = lilly;
        Location duck = new Location("The Fluttering Duck", "The famous schools 5Star restaurant known for good food and music on Thursdays.");
        Location ub = new Location ("The Union Building", "The central hub for student activities, events, and dining options on campus.");
        Location admin = new Location("Administration Building", "The place where all the important school offices and staff are located.");
        Location mason = new Location("Mason Hall", "A residence hall where students live and hang out.");
        Location reese = new Location("Reese Hall", "Another residence hall known to have the RES staff office and student community.");
        Location humbert = new Location("Humbert Hall", "A residence hall the houses cool first year students");

        myInventory = new ContainerItem("Inventory", "Container", "Player Backpack to store items collected");
        Item inventoryItem1 = new Item("Help", "Guide", "A small guide to help you navigate through the game");
        myInventory.addItem(inventoryItem1);
        // Starter coupons
        myInventory.addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        myInventory.addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        myInventory.addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        // Starter snack (counts as food)
        myInventory.addItem(new Item("StarterSnack", "Food", "A quick bite to keep you going."));
        
        // Initialize tracking variables
        moveCount = 0;
        points = 0;
        visitedLocations = new HashSet<>();
        helpDropLocation = null;
        visitedHoover = false;
        visitedDuck = false;
        sashaCalled = false;
        couponsUnlimited = false;
        hungerMoveCounter = 0;
        foodDecayCounter = 0;
        foodLockChoice = null;
        wrongCommandCount = 0;
        typingChallengeActive = false;
        typingWords = new String[0];
        typingFails = 0;
        typingContext = "penalty";
        treadmillUsed = false;
        salmonTaskComplete = false;
        snakeTaskComplete = false;
        macbookTaskComplete = false;
        dnaTaskActive = false;
        dnaMovesLeft = 0;
        dnaTaskComplete = false;
        dnaCountdownRunning = false;
        pendingGuitar = new Item("GlazedGuitar", "Music", "A four string guitar with a glowing green string, waiting for its true player.");
        musicTaskComplete = false;
        finaleShown = false;


        cdi.connect("north",olin);
        olin.connect("south", cdi);

        julian.connect("jump", cdi);

        julian.connect("north", hoover);
        hoover.connect("south", julian);

        julian.connect("east",lilly);
        lilly.connect("west", julian);

        lilly.connect("north",gcpa);
        gcpa.connect("south", lilly);

        gcpa.connect("north", ub);
        ub.connect("south", gcpa);

        ub.connect("cross", mason);

        mason.connect("jump", admin);

        mason.connect("south", reese);
        reese.connect("north", mason);
  
        reese.connect("east", humbert);
        humbert.connect("west", reese);

        roylibrary.connect("east", duck);
        duck.connect("west", roylibrary);

        duck.connect("north", admin);
        admin.connect("south", duck);

        roylibrary.connect("jump", admin);

        hoover.connect("east", gcpa);
        gcpa.connect("west", hoover);

        hoover.connect("west", olin);
        olin.connect("east", hoover);

        hoover.connect("north", roylibrary);
        roylibrary.connect("south", hoover);


        Item cdiItem = new Item("Phone", "Communication", "A smartphone used to call Sasha for help");
        cdi.addItem(cdiItem);

        Item lillyItem2 = new Item("Treadmill", "Exercise", "A high-tech treadmill used for running exercises and energy(points) boost.");
        lilly.addItem(lillyItem2);


        Item duckItem1 = new Item("Classic Burger", "Meal", "A juicy burger with lettuce, tomato, pickles, and cheese");
        duck.addItem(duckItem1);

        Item duckItem2 = new Item("Mac and Cheese", "Meal", "Creamy macaroni and cheese topped with breadcrumbs");
        duck.addItem(duckItem2);

        Item duckItem3 = new Item("Microphone", "Music", "A wireless microphone used by performers at The Fluttering Duck");
        duck.addItem(duckItem3);


        Item julianItem1 = new Item("Statue", "Art", "Bust statue of Percy Lavon Julian");
        julian.addItem(julianItem1);

        Item julianItem2 = new Item("Macbook", "Electronics", "An lost invaluable school macbook exclusive to the President recently spotted at Julian");
        julian.addItem(julianItem2);


        Item hooverItem1 = new Item("Fountain", "Food", "Dispenser containing a variety of soda drinks or water");
        hoover.addItem(hooverItem1);

        Item hooverItem2 = new Item("Sandwich", "Food", "A delicious sandwich with turkey, lettuce, tomato, and cheese");
        hoover.addItem(hooverItem2);

        Item hooverItem3 = new Item("Microphone", "Music", "An important stolen music microphone left by a music student during their lunch");
        hoover.addItem(hooverItem3);

        Item hooverItem4 = new Item("EndangeredSalmon", "Fish", "A rare species of salmon that has to be protected due to its endangered status");
        hoover.addItem(hooverItem4);


        Item humberItem1 = new Item("Yang", "Music", "A torn half of a music sheet which seems like it should be soomewhere else...");
        humbert.addItem(humberItem1);

        Item reeseItem1 = new Item("Yin", "Music", "A torn half of a music sheet which seems like it should be soomewhere else...");
        reese.addItem(reeseItem1);


        Item gcpaItem1 = new Item("Piano", "Music", "Huge piano in a practice room for music students");
        gcpa.addItem(gcpaItem1);


        Item olinItem1 = new Item("Specimen", "Reptile", "Stuffed snake on display for biology students or visitors");
        olin.addItem(olinItem1);

    
        Item roylibraryItem1 = new Item( "Bookshelf", "Furniture", "Shelf filled with books of all kinds");
        roylibrary.addItem(roylibraryItem1);


        currLocation = julian;


        ContainerItem duckContainer = new ContainerItem("TreasureBox", "Container", "A wooden mystery box filled with snakes that should not be opened...");
        Item snake = new Item("Snakes", "Reptile", "A bunch of venomous snakes slithering around inside the box");
        duckContainer.addItem(snake);
        duck.addItem(duckContainer);

        ContainerItem hooverContainer = new ContainerItem("ToGoBox", "Container", "A to-go box, and it feels like there's food inside...");
        Item chickenAmerican = new Item("Chicken", "Food", "A chicken piece with American cheese melted on top");
        hooverContainer.addItem(chickenAmerican);
        Item cutleries = new Item("Cutleries", "Utensils", "A set of plastic cutleries to eat your food with");
        hooverContainer.addItem(cutleries); 
        hoover.addItem(hooverContainer);
        
        ContainerItem gcpaContainer = new ContainerItem("GuitarCase", "Container", "A big black guitar case with an ominously musical aura around it...");
        gcpa.addItem(gcpaContainer);

        ContainerItem olinContainer1 = new ContainerItem("PCRMachine", "Container", "A big grey box used to teach Biology, but it feels like someone's inside of it...");
        Item dnaSample = new Item("BatMan's-D   NA", "Molecule", "Little test tubes containing DNA of BatMan.");
        olinContainer1.addItem(dnaSample); // 
        olin.addItem(olinContainer1);

        ContainerItem olinContainer2 = new ContainerItem("Aquarium", "Container", "A beautiful aquarium filled with special water that a rare Salmon fish needs to survive");
        olin.addItem(olinContainer2);

        ContainerItem julianContainer = new ContainerItem("DisplayCase", "Container", "A glass case once used to display the Monalisa painting, but it seems like something else is inside now...");
        Item rareArtifact = new Item("AncientArtifact", "History", "A mysterious artifact from ancient times.");
        julianContainer.addItem(rareArtifact);
        julian.addItem(julianContainer);

        ContainerItem roylibraryContainer = new ContainerItem("SecretDrawer", "Container", "A hidden drawer in the bookshelf that seems to contain something valuable...");
        Item oldManuscript = new Item("OldManuscript", "Literature", "A fragile manuscript filled with ancient writings and illustrations.");
        roylibraryContainer.addItem(oldManuscript);
        roylibrary.addItem(roylibraryContainer);

        ContainerItem adminContainer = new ContainerItem("MacbookCase", "Container", "A sleek case designed to hold and protect the pretigious Macbook laptop.");
        admin.addItem(adminContainer);

    }

    // Counts food items currently in inventory
    private int foodCount() {
        int count = 0;
        for (Item item : myInventory.getItems()) {
            if (isFoodItem(item)) {
                count++;
            }
        }
        return count;
    }

    private boolean isFoodItem(Item item) {
        return item.getType().equalsIgnoreCase("food") || item.getType().equalsIgnoreCase("meal");
    }

    private boolean isDiningLocation(Location loc) {
        String n = loc.getName().toLowerCase();
        return n.equals("hoover") || n.equals("the fluttering duck");
    }

    private boolean consumeCoupon() {
        for (Item item : myInventory.getItems()) {
            if (item.getType().equalsIgnoreCase("coupon")) {
                myInventory.removeItem(item.getName());
                return true;
            }
        }
        return false;
    }

    private void applyHungerAfterMove(StringBuilder message) {
        hungerMoveCounter++;
        foodDecayCounter++;
        if (hungerMoveCounter >= 5) {
            hungerMoveCounter = 0;
            // Try to consume one food
            for (Item item : myInventory.getItems()) {
                if (isFoodItem(item)) {
                    myInventory.removeItem(item.getName());
                    message.append("\nYou eat ").append(item.getName()).append(" to keep your energy up.");
                    return;
                }
            }
            // No food available -> move penalty
            moveCount += 2;
            message.append("\nYou're starving—moving slower (+2 moves).");
        }

        if (foodDecayCounter >= 5) {
            foodDecayCounter = 0;
            for (Item item : myInventory.getItems()) {
                if (isFoodItem(item)) {
                    myInventory.removeItem(item.getName());
                    message.append("\nOne of your foods spoiled and was discarded.");
                    return;
                }
            }
        }
    }

    private String normalizeWord(String word) {
        // Remove all spaces, special chars, make lowercase for easy typing
        return word.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private void startTypingChallenge(String context, int wordCount) {
        typingChallengeActive = true;
        typingContext = context;
        typingFails = 0;
        
        // Build word pool dynamically from game state
        ArrayList<String> pool = new ArrayList<>();
        
        // Add location names player has visited
        for (String locName : visitedLocations) {
            String normalized = normalizeWord(locName);
            if (normalized.length() > 3) {
                pool.add(normalized);
            }
        }
        
        // Add items from current location
        for (Item item : currLocation.getItems()) {
            String normalized = normalizeWord(item.getName());
            if (normalized.length() > 3) {
                pool.add(normalized);
            }
        }
        
        // Add items from inventory
        for (Item item : myInventory.getItems()) {
            String normalized = normalizeWord(item.getName());
            if (normalized.length() > 3) {
                pool.add(normalized);
            }
        }
        
        // Ensure we have enough words
        if (pool.size() < wordCount) {
            pool.addAll(Arrays.asList("campus", "explore", "adventure", "quest", "journey", "challenge"));
        }
        
        // Pick random words from the pool
        Random r = new Random();
        typingWords = new String[wordCount];
        for (int i = 0; i < wordCount; i++) {
            typingWords[i] = pool.get(r.nextInt(pool.size()));
        }
    }

    private String evaluateTypingChallenge(String userInput) {
        String[] parts = userInput.trim().split("\\s+");
        int correct = 0;
        
        // Check each required word to see if user typed it
        for (String requiredWord : typingWords) {
            for (String userWord : parts) {
                String normalizedUserWord = normalizeWord(userWord);
                // requiredWord is already normalized when created
                if (requiredWord.equals(normalizedUserWord)) {
                    correct++;
                    break; // Found this word, move to next required word
                }
            }
        }

        boolean success = correct >= 3;
        if (success) {
            typingChallengeActive = false;
            wrongCommandCount = 0;
            if ("treadmill".equals(typingContext)) {
                treadmillUsed = true;
                points += 15;
                return "You crushed the treadmill typing sprint! +15 points." + maybeFinaleMessage();
            }
            // Penalty typing challenge - different messages based on whether it's first try or recovery
            if (typingFails == 0) {
                return "Impressive! Looks like those fingers know their way around a keyboard after all. " + correct + "/" + typingWords.length + " correct. Keep exploring!";
            } else {
                return "Nice recovery! You typed " + correct + "/" + typingWords.length + " words correctly. Back to the game.";
            }
        }

        typingFails++;
        String scold = switch (typingFails) {
            case 1 -> "Come on, even freshmen can type better than that!";
            case 2 -> "Seriously? The registrar's office types faster than you.";
            default -> "At this rate, you'll graduate before learning to type.";
        };
        if ("treadmill".equals(typingContext)) {
            scold = "Keep running!";
        }
        return scold + " You got " + correct + "/" + typingWords.length + " correct. Type the words again or type 'skip' to move on: " + String.join(", ", typingWords);
    }

    private String typingPrompt() {
        if ("treadmill".equals(typingContext)) {
            return "Treadmill sprint! Type at least 3 of these words in one line, or type 'skip' to hop off: " + String.join(", ", typingWords);
        }
        return "Typing challenge! Type at least 3 of these words in one line to continue, or type 'skip' to ignore: " + String.join(", ", typingWords);
    }

    private int couponCount() {
        int count = 0;
        for (Item item : myInventory.getItems()) {
            if (item.getType().equalsIgnoreCase("coupon")) {
                count++;
            }
        }
        return count;
    }

    private void unlockCouponsUnlimited() {
        couponsUnlimited = true;
    }

    private boolean allCoreQuestsDone() {
        return musicTaskComplete && dnaTaskComplete && salmonTaskComplete && snakeTaskComplete && macbookTaskComplete && treadmillUsed;
    }

    private String maybeFinaleMessage() {
        if (allCoreQuestsDone() && !finaleShown) {
            finaleShown = true;
            points += 10;
            return "\nCampus whispers: every quest is wrapped. You feel legendary. (+10 points)";
        }
        return "";
    }

    public String getEndGameSummary() {
        // Apply efficiency bonus if earned
        boolean efficiencyBonus = false;
        if (moveCount < 70 && allCoreQuestsDone()) {
            points += 10;
            efficiencyBonus = true;
        }

        // Determine ranking
        String ranking;
        if (points >= 140) {
            ranking = "🏆 CAMPUS LEGEND";
        } else if (points >= 100) {
            ranking = "⭐ TRUE EXPLORER";
        } else if (points >= 70) {
            ranking = "📚 DEDICATED ADVENTURER";
        } else if (points >= 40) {
            ranking = "🎒 CASUAL WANDERER";
        } else {
            ranking = "🚶 NOVICE EXPLORER";
        }

        // Check if achieved secret win
        boolean secretWin = visitedHoover && visitedDuck;

        StringBuilder summary = new StringBuilder();
        summary.append("\n");
        summary.append("═══════════════════════════════════════════════════════════════\n");
        summary.append("                     🎮 GAME OVER - FINAL RESULTS 🎮\n");
        summary.append("═══════════════════════════════════════════════════════════════\n\n");
        
        summary.append("📊 FINAL STATISTICS:\n");
        summary.append("   Total Moves: ").append(moveCount).append("\n");
        summary.append("   Final Score: ").append(points).append(" points\n");
        summary.append("   Ranking: ").append(ranking).append("\n\n");

        summary.append("✅ QUEST COMPLETION:\n");
        summary.append("   DNA Delivery: ").append(dnaTaskComplete ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Music Quest: ").append(musicTaskComplete ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Wildlife Rescue (Salmon): ").append(salmonTaskComplete ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Wildlife Rescue (Snakes): ").append(snakeTaskComplete ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   MacBook Return: ").append(macbookTaskComplete ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Treadmill Challenge: ").append(treadmillUsed ? "✓ Complete" : "✗ Incomplete").append("\n\n");

        if (efficiencyBonus) {
            summary.append("🌟 EFFICIENCY BONUS: Completed all quests in under 70 moves! (+10 points)\n\n");
        }

        if (secretWin) {
            summary.append("🎊 SECRET ACHIEVEMENT UNLOCKED: The True Explorer!\n");
            summary.append("   You visited both dining locations and faced the penalty.\n");
            summary.append("   Only the bravest explorers see everything the campus offers!\n\n");
        }

        summary.append("═══════════════════════════════════════════════════════════════\n");
        summary.append("Thanks for playing Zork v2! Your adventure has been recorded.\n");
        summary.append("═══════════════════════════════════════════════════════════════\n");

        return summary.toString();
    }

    private void spawnGuitarAtLilly() {
        if (lillyLocation != null && !lillyLocation.hasItem(pendingGuitar.getName())) {
            lillyLocation.addItem(pendingGuitar);
        }
    }

    private String handleMusicCase(ContainerItem guitarCase) {
        boolean hasGuitar = guitarCase.hasItem("GlazedGuitar");
        boolean hasMic = guitarCase.hasItem("Microphone");
        boolean hasYin = guitarCase.hasItem("Yin");
        boolean hasYang = guitarCase.hasItem("Yang");
        boolean hasSheet = guitarCase.hasItem("CompleteSheet");

        // Auto-combine Yin/Yang if both present
        if (hasYin && hasYang) {
            guitarCase.removeItem("Yin");
            guitarCase.removeItem("Yang");
            guitarCase.addItem(new Item("CompleteSheet", "Music", "Yin and Yang fused into one complete sheet."));
            hasSheet = true;
        }

        if (hasGuitar && hasMic && hasSheet && !musicTaskComplete) {
            musicTaskComplete = true;
            points += 15;
            return "\nThe guitarist and singer cheer—the show is saved! Music quest complete (+15 points)." + maybeFinaleMessage();
        }
        return "";
    }

    private void combineSheetsInInventory() {
        if (myInventory.hasItem("Yin") && myInventory.hasItem("Yang")) {
            myInventory.removeItem("Yin");
            myInventory.removeItem("Yang");
            myInventory.addItem(new Item("CompleteSheet", "Music", "Yin and Yang fused into one complete sheet."));
        }
    }

    private void startDnaCountdownIfNeeded(Location previousLocation) {
        if (dnaTaskActive && !dnaCountdownRunning && previousLocation.getName().equalsIgnoreCase("Olin")) {
            dnaCountdownRunning = true;
            dnaMovesLeft = 3;
        }
    }

    private String tickDnaCountdown() {
        if (!dnaCountdownRunning) {
            return "";
        }
        dnaMovesLeft--;
        if (dnaMovesLeft <= 0) {
            // DNA spoiled
            myInventory.removeItem("BatMan's-D   NA");
            dnaCountdownRunning = false;
            dnaTaskActive = false;
            points -= 10;
            return "\nThe DNA was exposed too long and is destroyed (-10 points).";
        }
        if (dnaMovesLeft == 1) {
            return "\nHurry! The DNA will spoil after one more move.";
        }
        return "\nDNA sample integrity: " + dnaMovesLeft + " moves left.";
    }

    // Helper method to check for Hoover/Duck penalty
    private String checkDiningLocationPenalty() {
        String locationName = currLocation.getName().toLowerCase();
        
        if (locationName.equals("hoover")) {
            if (!visitedHoover && visitedDuck) {
                // Second dining location visited - apply penalty
                moveCount += 10;
                points -= 25;
                visitedHoover = true;
                return "\n⚠️  WARNING: You broke the dining rule! +10 moves, -25 points.";
            } else if (!visitedHoover && !visitedDuck) {
                // First dining location - warn player
                visitedHoover = true;
                return "\n📍 Note: There's another dining location on campus (The Fluttering Duck). Choose wisely which one to visit—you should only visit ONE!";
            }
        } else if (locationName.equals("the fluttering duck")) {
            if (!visitedDuck && visitedHoover) {
                // Second dining location visited - apply penalty
                moveCount += 10;
                points -= 25;
                visitedDuck = true;
                return "\n⚠️  WARNING: You broke the dining rule! +10 moves, -25 points.";
            } else if (!visitedDuck && !visitedHoover) {
                // First dining location - warn player
                visitedDuck = true;
                return "\n📍 Note: There's another dining location on campus (Hoover). Choose wisely which one to visit—you should only visit ONE!";
            }
        }
        return "";
    }


    public String processCommand(String userInput) {

        String[] words = userInput.toLowerCase().trim().split(" ");

        if (userInput.trim().isEmpty()) {
            return "Enter a command...";
        }

        // Typing challenge gate
        if (typingChallengeActive) {
            if (userInput.equalsIgnoreCase("skip")) {
                typingChallengeActive = false;
                if ("treadmill".equals(typingContext)) {
                    return "You step off the treadmill. No bonus, but you can keep exploring.";
                }
                return "Fine, skipping the typing drill. Back to adventuring.";
            }
            return evaluateTypingChallenge(userInput);
        }

        String command = words[0];

        switch (command) {
            case "look" -> {
                StringBuilder lkOutput = new StringBuilder();
                lkOutput.append(currLocation.getName())
                        .append(" - ")
                        .append(currLocation.getDescription());
                if (currLocation.getName().equalsIgnoreCase("Roy Library")) {
                    lkOutput.append("\n(Oh Oh the Librarian is saying something you might want to listen, * If you ever carry BatMan's DNA from Olin, rush it to Julian within three moves or it spoils *)");
                }
                if (currLocation.getName().equalsIgnoreCase("CDI")) {
                    lkOutput.append("\n(You gotta give Sasha a call here with the phone she's got some good tea for you.)");
                }
                if (currLocation.getName().equalsIgnoreCase("Lilly Building")) {
                    lkOutput.append("\n(OH! The guy Sasha was talking about is here and surprisingly hitting the strings pretty well! But blah blah you gotta take it and step");
                }
                if (currLocation.getName().equalsIgnoreCase("GCPA")) {
                    lkOutput.append("\n(A stage manager mutters: 'We still need a guitar, a mic, and the complete sheet before the show.')");
                }
                if (currLocation.getName().equalsIgnoreCase("Administration Building")) {
                    lkOutput.append("\n(A secretary asks: 'Did you bring back the President's MacBook?')");
                }
                if (currLocation.getName().equalsIgnoreCase("Olin")) {
                    lkOutput.append("\n(A lab poster: 'Return endangered animals to the Aquarium. Keep DNA cold and hurry to Julian!')");
                }
                if (currLocation.getName().equalsIgnoreCase("The Fluttering Duck")) {
                    lkOutput.append("\n(A chef warns: 'One meal source only—Duck or Hoover. Coupons required unless you're the bio hero.')");
                }
                if (currLocation.getName().equalsIgnoreCase("Hoover")) {
                    lkOutput.append("\n(A cashier smiles: 'Stick with Hoover or you'll pay a penalty. Coupons please—unless biology owes you.')");
                }
                ArrayList<Item> locationItems = currLocation.getItems();
                for (Item item : locationItems) {
                    lkOutput.append("\n+ ").append(item.getName());
                }
                return lkOutput.toString();

            }

            case "examine" -> {
                if (words.length > 1) {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                    
                    // Check if examining Help item from inventory or location
                    if (Item.normalizeName(itemName).equals(Item.normalizeName("Help"))) {
                        if (myInventory.hasItem(itemName) || currLocation.hasItem(itemName)) {
                            return """
                            ╔═══════════════════════════════════════════════════════════════╗
                            ║                    ZORK v2 - GAME GUIDE                      ║
                            ╚═══════════════════════════════════════════════════════════════╝
                            
                            🎮 OBJECTIVE: Explore the campus, collect valuable items, and manage your moves wisely.
                            Track your moves and points with the 'status' command. Goal: maximize points!
                            
                            ─────────────────────── BASIC COMMANDS ──────────────────────────
                            
                            LOOK
                              Shows the name, description, and all items at your current location.
                              
                            EXAMINE <item>
                              Get detailed information about an item. For example: 'examine phone'
                              Use this on the Help item (this guide!) to read it anytime.
                            
                            ──────────────────── MOVEMENT COMMANDS ────────────────────────
                            
                            GO <direction>
                              (Costs: 1 move) Move in cardinal directions: north, south, east, or west.
                              Example: 'go north' to move to an adjacent location.
                              ⚠️  Warning: Only works with cardinal directions!
                            
                            JUMP
                              (Costs: 2 moves) Special teleport ability. Try using it at different locations!
                              Some places on campus might be ideal for jumping—explore to find them.
                            
                            CROSS
                              (Costs: 2 moves) Special bridge action. Try using it at different locations!
                              Perhaps a central campus hub might have a crossing path...
                            
                            ──────────────────── INVENTORY COMMANDS ───────────────────────
                            
                            TAKE <item>
                              Pick up an item from your current location. Add it to your inventory.
                              Example: 'take phone'
                            
                            TAKE <item> FROM <container>
                              Remove an item from a container (like a box or case) at your location.
                              Example: 'take chicken from togbox'
                            
                            DROP <item>
                              Remove an item from your inventory and leave it at your current location.
                              Note: Dropping your Help guide? Use 'status' to find where you dropped it!
                            
                            PUT <item> IN <container>
                              Place an item from your inventory into a container at your current location.
                              Example: 'put sandwich in backpack'
                            
                            INVENTORY
                              Display all items you're currently carrying in your backpack.
                            
                            ──────────────────── GAME STATUS COMMANDS ────────────────────
                            
                            STATUS (or SCORE)
                              See your current moves used, total points, and location.
                              Keep track of these to optimize your route!
                            
                            HELP
                              Directs you to this guide. You must have the Help item to read it.
                            
                            QUIT
                              Exit the game.
                            
                            ═════════════════════ STRATEGIC GAMEPLAY TIPS ═════════════════
                            
                            📊 MOVE SYSTEM:
                              • GO = 1 move  |  JUMP/CROSS = 2 moves each
                              • Plan your route carefully—moves add up fast!
                              • Visit high-value items and avoid wasting moves.
                            
                            ⚠️  CRITICAL RULE - DINING LOCATIONS:
                              There are TWO dining areas: Hoover and The Fluttering Duck.
                                                            • You can visit EITHER location but NOT BOTH in one game.
                                                            • Visiting both results in: +10 MOVES added + 25 POINTS lost.
                                                            • First visit locks your food source (Hoover OR Duck).
                                                            • Food pickups at dining spots require a coupon unless you've earned unlimited coupons.
                                                            • Carry max 3 foods; food spoils every few moves and hunger slows you if you run out.
                            
                            💀 DEAD ENDS (Worth the Cost!):
                              The Duck, Humbert, Admin, and CDI are dead-ends with few exits.
                              BUT they contain HIGH-VALUE items worth the extra moves!
                              • Plan your route to collect them efficiently.
                              • Don't waste moves if you won't grab items.
                            
                            🗺️  THE HELP GUIDE (This Item):
                              • You start with this guide in your backpack.
                              • If you drop it, remember: type 'help' and it will tell you WHERE you dropped it.
                              • Drop it wisely—you need it to review commands, but carrying it takes inventory space.

                                                        🔬 QUEST REMINDERS:
                                                            • DNA: Grab from Olin, then deliver to Julian within 3 moves or it spoils (-10 pts). Success: +15 pts.
                                                            • Wildlife Rescue: Move EndangeredSalmon and Snakes into Aquarium at Olin for bonuses; both done unlock unlimited dining coupons.
                                                            • Music: Call Sasha by taking the Phone; guitar spawns at Lilly. Put guitar + microphone + complete sheet (Yin+Yang) into GuitarCase at GCPA.
                                                            • MacBook: Return Macbook to MacbookCase at Administration for points.
                                                            • Treadmill: Use treadmill at Lilly for a typing sprint bonus.
                            
                            ─────────────────────────────────────────────────────────────────
                            Good luck, explorer! Make every move count and collect wisely!
                            """;
                        }
                    }
                    
                    if (currLocation.hasItem(itemName)) {
                        return currLocation.getItem(itemName).toString();
                    } else {
                        return "Cannot find that item";
                    }
                } else {
                    return "Examine what? Give me a full sentence.";
                }

            }

            case "go" -> {
                if (words.length > 1) {
                    if (currLocation.canMove(words[1])) {
                        Location previous = currLocation;
                        currLocation = currLocation.getLocation(words[1]);
                        moveCount++;
                        visitedLocations.add(currLocation.getName());
                        startDnaCountdownIfNeeded(previous);
                        StringBuilder msg = new StringBuilder();
                        msg.append("You go ").append(words[1]).append(" to ").append(currLocation.getName());
                        msg.append(checkDiningLocationPenalty());
                        msg.append(tickDnaCountdown());
                        applyHungerAfterMove(msg);
                        return msg.toString();
                    } else if (!words[1].equals("north") && !words[1].equals("south")
                            && !words[1].equals("west") && !words[1].equals("east")) {
                        return "Enter a valid direction...something like north or south you know :)";
                    } else {
                        return "You cannot go in that direction";
                    }
                } else {
                    return "Go where? Give me a full sentence";
                }

            }

            case "jump" -> {
                String locationName = currLocation.getName().toLowerCase();
                if (locationName.equals("julian") || locationName.equals("mason hall") || locationName.equals("roy library")) {
                    if (currLocation.canMove("jump")) {
                        Location previous = currLocation;
                        currLocation = currLocation.getLocation("jump");
                        moveCount += 2;
                        visitedLocations.add(currLocation.getName());
                        startDnaCountdownIfNeeded(previous);
                        StringBuilder msg = new StringBuilder();
                        msg.append("You jump to ").append(currLocation.getName());
                        msg.append(tickDnaCountdown());
                        applyHungerAfterMove(msg);
                        return msg.toString();
                    }
                }
                return "This place doesn't seem right for jumping...";

            }

            case "cross" -> {
                if (!currLocation.canMove("cross")) {
                    return "You can't cross from here.";
                }
 
                Location previousLocation = currLocation;
                currLocation = currLocation.getLocation("cross");
                moveCount += 2;
                visitedLocations.add(currLocation.getName());
                startDnaCountdownIfNeeded(previousLocation);
                StringBuilder crossMsg = new StringBuilder();
                crossMsg.append("You cross from ").append(previousLocation.getName()).append(" to ").append(currLocation.getName());
                crossMsg.append(tickDnaCountdown());
                applyHungerAfterMove(crossMsg);
                return crossMsg.toString();

            }

            case "status", "score" -> {
                return """
                        === GAME STATUS ===
                        Moves: %d
                        Points: %d
                        Current Location: %s
                        Food: %d/3
                        Coupons: %s
                        Quests — DNA: %s, Music: %s, Wildlife: %s, MacBook: %s, Treadmill: %s
                        """.formatted(
                        moveCount,
                        points,
                        currLocation.getName(),
                        foodCount(),
                        couponsUnlimited ? "unlimited" : String.valueOf(couponCount()),
                        dnaTaskComplete ? "done" : (dnaTaskActive ? "in progress" : "not started"),
                        musicTaskComplete ? "done" : "in progress",
                        (salmonTaskComplete && snakeTaskComplete) ? "done" : "in progress",
                        macbookTaskComplete ? "done" : "in progress",
                        treadmillUsed ? "done" : "not tried"
                );
            }


            case "inventory" -> {
                StringBuilder inOutput = new StringBuilder();
                ArrayList<Item> inventoryItems = myInventory.getItems();
                if (inventoryItems.isEmpty()) {
                    return "Oops! Your inventory is empty.";
                } else {
                    inOutput.append("Your inventory has: ");
                    for (Item item : inventoryItems) {
                        inOutput.append("\n+ ").append(item.getName());
                    }
                }
                return inOutput.toString();

            }


            case "use" -> {
                if (words.length < 2) {
                    return "Use what?";
                }
                String target = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                if (currLocation.getName().equalsIgnoreCase("Lilly Building") && Item.normalizeName(target).equals(Item.normalizeName("Treadmill"))) {
                    if (treadmillUsed) {
                        return "You've already sprinted on this treadmill.";
                    }
                    startTypingChallenge("treadmill", 5);
                    return typingPrompt();
                }
                return "You can't use that here.";

            }


            case "take" -> {
                if (words.length > 1) {

                    int fromIndex = -1;
                    for (int i = 0; i < words.length; i++) {
                        if (words[i].equals("from")) {
                            fromIndex = i;
                        }
                    }

                    if (fromIndex != -1) {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
                        String containerName = String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, words.length));

                        if (!currLocation.hasItem(containerName)) {
                            return "Cannot find that container in here";
                        }
                        if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                            return "The container does not exist in here";
                        }

                        if (!container.hasItem(itemName)) {
                            return "The " + containerName + " doesn't contain this item";
                        }
                        Item taken = container.removeItem(itemName);
                        // Food lock, coupons, and capacity
                        if (isFoodItem(taken)) {
                            if (foodLockChoice != null && currLocation.getName().toLowerCase().contains("hoover") && foodLockChoice.equals("duck")) {
                                container.addItem(taken);
                                return "You committed to Duck for food. You can't take food from Hoover.";
                            }
                            if (foodLockChoice != null && currLocation.getName().toLowerCase().contains("fluttering duck") && foodLockChoice.equals("hoover")) {
                                container.addItem(taken);
                                return "You committed to Hoover for food. You can't take food from Duck.";
                            }
                            if (isDiningLocation(currLocation) && !couponsUnlimited) {
                                if (!consumeCoupon()) {
                                    container.addItem(taken);
                                    return "You need a food coupon to take this meal.";
                                }
                            }
                            if (foodCount() >= 3) {
                                container.addItem(taken);
                                return "Your backpack can only hold 3 foods.";
                            }
                            if (foodLockChoice == null && (currLocation.getName().equalsIgnoreCase("Hoover") || currLocation.getName().equalsIgnoreCase("The Fluttering Duck"))) {
                                foodLockChoice = currLocation.getName().toLowerCase().contains("hoover") ? "hoover" : "duck";
                            }
                        }

                        myInventory.addItem(taken);
                        // Phone triggers Sasha call
                        if (Item.normalizeName(taken.getName()).equals(Item.normalizeName("Phone")) && !sashaCalled) {
                            sashaCalled = true;
                            spawnGuitarAtLilly();
                            return "You picked up the phone. Sasha calls: 'That glowing guitar's at Lilly with a football player. Go grab it!'";
                        }
                        // DNA pickup starts task
                        if (Item.normalizeName(taken.getName()).contains("batman's-d")) {
                            dnaTaskActive = true;
                            dnaCountdownRunning = false;
                        }
                        combineSheetsInInventory();
                        return "You picked up  " + itemName;
                    } else {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                        if (currLocation.hasItem(itemName)) {
                            Item taken = currLocation.removeItem(itemName);
                            if (isFoodItem(taken)) {
                                if (foodLockChoice != null && currLocation.getName().toLowerCase().contains("hoover") && foodLockChoice.equals("duck")) {
                                    currLocation.addItem(taken);
                                    return "You committed to Duck for food. You can't take food from Hoover.";
                                }
                                if (foodLockChoice != null && currLocation.getName().toLowerCase().contains("fluttering duck") && foodLockChoice.equals("hoover")) {
                                    currLocation.addItem(taken);
                                    return "You committed to Hoover for food. You can't take food from Duck.";
                                }
                                if (isDiningLocation(currLocation) && !couponsUnlimited) {
                                    if (!consumeCoupon()) {
                                        currLocation.addItem(taken);
                                        return "You need a food coupon to take this meal.";
                                    }
                                }
                                if (foodCount() >= 3) {
                                    currLocation.addItem(taken);
                                    return "Your backpack can only hold 3 foods.";
                                }
                                if (foodLockChoice == null && (currLocation.getName().equalsIgnoreCase("Hoover") || currLocation.getName().equalsIgnoreCase("The Fluttering Duck"))) {
                                    foodLockChoice = currLocation.getName().toLowerCase().contains("hoover") ? "hoover" : "duck";
                                }
                            }
                            myInventory.addItem(taken);

                            combineSheetsInInventory();

                            if (Item.normalizeName(taken.getName()).equals(Item.normalizeName("GlazedGuitar")) && !sashaCalled) {
                                currLocation.addItem(myInventory.removeItem(taken.getName()));
                                return "You don't actually see that guitar yet. Maybe someone knows where it is?";
                            }
                            return "You picked up  " + itemName;
                        } else {
                            return "Cannot find that item here";
                        }
                    }
                } else {
                    return "Take what? Give me a full sentence.";
                }

            }


            case "drop" -> {
                if (words.length > 1) {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));

                    if (myInventory.hasItem(itemName)) {
                        Item droppedItem = myInventory.removeItem(itemName);
                        currLocation.addItem(droppedItem);
                        
                        // Track if Help item is dropped
                        if (Item.normalizeName(itemName).equals(Item.normalizeName("Help"))) {
                            helpDropLocation = currLocation;
                        }

                        // DNA delivery success only at Julian
                        if (Item.normalizeName(itemName).contains("batman's-d") && currLocation.getName().equalsIgnoreCase("Julian") && !dnaTaskComplete) {
                            dnaTaskComplete = true;
                            dnaTaskActive = false;
                            dnaCountdownRunning = false;
                            points += 15;
                            return "You dropped " + itemName + ". The lab tech secures the sample. DNA delivery complete! +15 points." + maybeFinaleMessage();
                        }
                        
                        return "You dropped " + itemName;
                    } else {
                        return "Cannot find that item in your inventory";
                    }
                } else {
                    return "Drop what? Give me a full sentence.";
                }

            }


            case "put" -> {
                if (words.length >= 4) {

                    int inIndex = -1;
                    for (int i = 0; i < words.length; i++) {
                        if (words[i].equals("in")) {
                            inIndex = i;
                        }
                    }

                    if (inIndex != -1) {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, inIndex));
                        String containerName = String.join(" ", Arrays.copyOfRange(words, inIndex + 1, words.length));

                        if (!myInventory.hasItem(itemName)) {
                            return "You don't have this item.";
                        }

                        if (!currLocation.hasItem(containerName)) {
                            return "Cannot find container here.";
                        }


                        if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                            return "The " + containerName + " is not a container.";
                        } else {
                            Item moving = myInventory.removeItem(itemName);
                            container.addItem(moving);

                            // Quest checks
                            StringBuilder extra = new StringBuilder();
                            if (containerName.equalsIgnoreCase("Aquarium")) {
                                if (Item.normalizeName(moving.getName()).contains("endangeredsalmon") && !salmonTaskComplete) {
                                    salmonTaskComplete = true;
                                    points += 12;
                                    extra.append("\nThe aquarium hums — salmon rescued! +12 points.");
                                }
                                if (Item.normalizeName(moving.getName()).equals(Item.normalizeName("Snakes")) && !snakeTaskComplete) {
                                    snakeTaskComplete = true;
                                    points += 8;
                                    extra.append("\nSnakes safely contained in the lab. +8 points.");
                                }
                                if (salmonTaskComplete && snakeTaskComplete && !couponsUnlimited) {
                                    unlockCouponsUnlimited();
                                    extra.append("\nBiology team loves you. Dining now honors unlimited coupons!");
                                }
                            }

                            if (containerName.equalsIgnoreCase("MacbookCase") && Item.normalizeName(moving.getName()).equals(Item.normalizeName("Macbook")) && !macbookTaskComplete) {
                                macbookTaskComplete = true;
                                points += 7;
                                extra.append("\nMacBook returned to Admin. +7 points and gratitude from IT.");
                            }

                            if (containerName.equalsIgnoreCase("GuitarCase")) {
                                extra.append(handleMusicCase(container));
                            }

                            // DNA delivery if someone tries putting in display case (allow Julian only)
                            if (Item.normalizeName(moving.getName()).contains("batman's-d") && currLocation.getName().equalsIgnoreCase("Julian") && !dnaTaskComplete) {
                                dnaTaskComplete = true;
                                dnaTaskActive = false;
                                dnaCountdownRunning = false;
                                points += 15;
                                extra.append("\nLab tech seals the DNA properly. Delivery complete! +15 points.");
                            }

                            extra.append(maybeFinaleMessage());

                            return "You put " + itemName + " in " + containerName + extra;
                        }
                    }
                    // If no 'in' keyword was found
                    return "Specify like: put <item> in <container>";
                } else {
                    return "Put what in what? Give me a full sentence";
                }
            }


            case "help" -> {
                if (myInventory.hasItem("Help")) {
                    return "Pull up inventory! You have a Help guide. Type 'examine Help' to read it.";
                } else if (currLocation.hasItem("Help")) {
                    return "You dropped your Help guide here pal! Pick it up and examine it to learn how to play.";
                } else if (helpDropLocation != null) {
                    return "You dropped your Help guide at " + helpDropLocation.getName() + "! Go back there to find it and examine it.";
                } else {
                    return "You dropped your Help guide somewhere! Find it and examine it to see the game instructions.";
                }


            }

            case "hi", "hello" -> {
                return "Hey What's up? Type 'examine help' if you need assistance you have the guide in your inventory";
            }


            default -> {
                wrongCommandCount++;
                if (wrongCommandCount >= 10) {
                    startTypingChallenge("penalty", 5);
                    return "You keep fumbling commands. " + typingPrompt();
                }
                return "I don't know how to do that";
            }
        }

    }

}

