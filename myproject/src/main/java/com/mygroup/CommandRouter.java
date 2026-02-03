package com.mygroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommandRouter routes and processes player commands.
 * Handles command parsing and delegates to subsystems for execution.
 */
public class CommandRouter {
    private final GameState state;
    private final FoodSystem foodSystem;
    private final TypingChallengeSystem typingChallengeSystem;
    private final DNASystem dnaSystem;
    private final QuestSystem questSystem;

    public CommandRouter(GameState state, FoodSystem foodSystem, TypingChallengeSystem typingChallengeSystem,
            DNASystem dnaSystem, QuestSystem questSystem) {
        this.state = state;
        this.foodSystem = foodSystem;
        this.typingChallengeSystem = typingChallengeSystem;
        this.dnaSystem = dnaSystem;
        this.questSystem = questSystem;
    }

    /**
     * Route a player command to the appropriate handler
     */
    public String routeCommand(String userInput) {
        String[] words = userInput.toLowerCase().trim().split(" ");

        if (userInput.trim().isEmpty()) {
            return "Enter a command...";
        }

        // Typing challenge gate
        if (state.isTypingChallengeActive()) {
            if (userInput.equalsIgnoreCase("skip")) {
                state.endTypingChallenge();
                if ("treadmill".equals(state.getTypingContext())) {
                    return "You step off the treadmill. No bonus, but you can keep exploring.";
                }
                return "Fine, skipping the typing drill. Back to adventuring.";
            }
            return typingChallengeSystem.evaluateAttempt(userInput);
        }

        String command = words[0];

        switch (command) {
            case "look" -> {
                StringBuilder lkOutput = new StringBuilder();
                lkOutput.append(state.getCurrLocation().getName())
                        .append(" - ")
                        .append(state.getCurrLocation().getDescription());
                if (state.getCurrLocation().getName().equalsIgnoreCase("Roy Library")) {
                    lkOutput.append(
                            "\n(Oh Oh the Librarian is saying something you might want to listen, * If you ever carry BatMan's DNA from Olin, rush it to Julian within three moves or it spoils *)");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("CDI")) {
                    lkOutput.append(
                            "\n(You gotta give Sasha a call here with the phone she's got some good tea for you.)");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("Lilly Building")) {
                    lkOutput.append(
                            "\n(OH! The guy Sasha was talking about is here and surprisingly hitting the strings pretty well! But blah blah you gotta take it and step");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("GCPA")) {
                    lkOutput.append(
                            "\n(A stage manager mutters: 'We still need a guitar, a mic, and the complete sheet before the show.')");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("Administration Building")) {
                    lkOutput.append("\n(A secretary asks: 'Did you bring back the President's MacBook?')");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("Olin")) {
                    lkOutput.append(
                            "\n(A lab poster: 'Return endangered animals to the Aquarium. Keep DNA cold and hurry to Julian!')");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("The Fluttering Duck")) {
                    lkOutput.append(
                            "\n(A chef warns: 'One meal source only—Duck or Hoover. Coupons required unless you're the bio hero.')");
                }
                if (state.getCurrLocation().getName().equalsIgnoreCase("Hoover")) {
                    lkOutput.append(
                            "\n(A cashier smiles: 'Stick with Hoover or you'll pay a penalty. Coupons please—unless biology owes you.')");
                }
                ArrayList<String> locationItems = state.getCurrLocation().getItemNames();
                for (String itemName : locationItems) {
                    lkOutput.append("\n+ ").append(itemName);
                }
                return lkOutput.toString();
            }

            case "go" -> {
                if (words.length < 2) {
                    return "Go where?";
                }
                String direction = words[1].toLowerCase();
                Location previousLocation = state.getCurrLocation();
                Location nextLocation = state.getCurrLocation().getConnection(direction);
                if (nextLocation != null) {
                    state.setCurrLocation(nextLocation);
                    state.addVisitedLocation(nextLocation);
                    state.incrementMoveCount();

                    StringBuilder output = new StringBuilder();
                    output.append("You are now at ")
                            .append(state.getCurrLocation().getName())
                            .append(". ")
                            .append(state.getCurrLocation().getDescription());

                    dnaSystem.startCountdownIfNeeded(previousLocation);
                    output.append(dnaSystem.tickCountdown());
                    output.append(foodSystem.applyHungerAfterMove());
                    output.append(foodSystem.checkDiningLocationPenalty());

                    return output.toString();
                } else {
                    return "You can't go that way";
                }
            }

            case "items" -> {
                ArrayList<String> itemNames = state.getCurrLocation().getItemNames();
                if (itemNames.isEmpty()) {
                    return "No items here";
                } else {
                    return "Items here: " + String.join(", ", itemNames);
                }
            }

            case "connections" -> {
                ArrayList<String> connectionNames = state.getCurrLocation().getConnectionNames();
                if (connectionNames.isEmpty()) {
                    return "No exits from here";
                } else {
                    return "Exits: " + String.join(", ", connectionNames);
                }
            }

            case "inventory" -> {
                ArrayList<String> inventoryNames = state.getInventory().getItemNames();
                if (inventoryNames.isEmpty()) {
                    return "Your inventory is empty";
                } else {
                    return "You have: " + String.join(", ", inventoryNames);
                }
            }

            case "examine" -> {
                if (words.length < 2) {
                    return "Examine what?";
                }
                String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));

                // Special Help guide content (verbatim from original)
                if (Item.normalizeName(itemName).equals(Item.normalizeName("Help"))) {
                    if (state.getInventory().hasItem(itemName) || state.getCurrLocation().hasItem(itemName)) {
                        return """
                                ╔═══════════════════════════════════════════════════════════════╗
                                 ║                    ZORK v2 - GAME GUIDE                     ║
                                ╚═══════════════════════════════════════════════════════════════╝

                                🎮 OBJECTIVE: Explore the campus, collect valuable items, and manage your moves wisely.
                                Track your moves and points with the 'status' command. Goal: maximize points! or for others least moves🙃.

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
                                                                • You can visit the FIRST dining location freely (no penalty).
                                                                • VISITING the SECOND dining location triggers the penalty: +10 MOVES + 25 POINTS lost.
                                                                • You can only eat at ONE place. The second location visit costs you, no matter what.
                                                                • Food pickups require coupons unless you've earned unlimited coupons (by rescuing both wildlife).
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

                                                            🎭 QUEST TASKS (Complete All or Don't DC...):

                                                                🎸 ★ MAIN TASK — THE MUSIC SHOW:
                                                                Call Sasha with the Phone. She'll tell you where to find a glowing guitar at Lilly.
                                                                Collect the Microphone and combine Yin + Yang into a CompleteSheet.
                                                                Assemble all three into GuitarCase at GCPA to save the show!
                                                                Reward: +20 points

                                                                🧬 DNA DELIVERY (URGENT — 3 MOVE LIMIT):
                                                                Grab the DNA sample from Olin's PCR Machine. Rush it to Julian within 3 moves or it spoils!
                                                                Success: +15 pts | If spoiled: -10 pts

                                                                🐠 ENDANGERED SALMON RESCUE:
                                                                The Salmon at Hoover is dying. Move it to the Aquarium at Olin to save it.
                                                                Reward: +12 points

                                                                🐍 SNAKES (SEALED BOX — DO NOT OPEN):
                                                                Dangerous venomous Snakes are sealed in TreasureBox at Duck.
                                                                Carry the sealed TreasureBox to Olin, then place it INSIDE the SafeBox container.
                                                                This secures them safely without endangering anyone.
                                                                Reward: +10 points

                                                                💻 MACBOOK RECOVERY:
                                                                Find the President's MacBook and return it to MacbookCase at Administration.
                                                                Reward: +7 points

                                                                🏃 TREADMILL TYPING SPRINT:
                                                                Use the treadmill at Lilly and ace the typing challenge.
                                                                Reward: +15 points

                                ─────────────────────────────────────────────────────────────────
                                Good luck, explorer! Make every move count and collect wisely!
                                """;
                    }
                }

                if (state.getCurrLocation().hasItem(itemName)) {
                    return state.getCurrLocation().getItem(itemName).getDescription();
                } else if (state.getInventory().hasItem(itemName)) {
                    return state.getInventory().getItem(itemName).getDescription();
                } else {
                    return "You do not see " + itemName + " here or in your inventory";
                }
            }

            case "take" -> {
                if (words.length < 2) {
                    return "Take what?";
                } else {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                    // Special-case: allow picking up the sealed TreasureBox to carry it safely to
                    // Olin
                    if (state.getCurrLocation().hasContainerItem(itemName)) {
                        if (Item.normalizeName(itemName).equals(Item.normalizeName("TreasureBox"))) {
                            Item box = state.getCurrLocation().getItem(itemName);
                            state.getInventory().addItem(box);
                            state.getCurrLocation().removeItem(itemName);
                            return "You carefully pick up the sealed TreasureBox. Handle with care and take it to Olin.";
                        }
                        // Support syntax: take <item> from <container>
                        int fromIndex = -1;
                        for (int i = 1; i < words.length; i++) {
                            if (words[i].equals("from")) {
                                fromIndex = i;
                                break;
                            }
                        }
                        if (fromIndex > 1 && fromIndex < words.length - 1) {
                            String thingName = String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
                            String containerName = String.join(" ",
                                    Arrays.copyOfRange(words, fromIndex + 1, words.length));
                            if (!state.getCurrLocation().hasContainerItem(containerName)) {
                                return "There's no container called " + containerName + " here.";
                            }
                            ContainerItem container = (ContainerItem) state.getCurrLocation().getItem(containerName);
                            if (!container.hasItem(thingName)) {
                                return "The " + containerName + " doesn't contain " + thingName + ".";
                            }
                            // Dangerous action: trying to remove snakes from the box costs points and is
                            // disallowed
                            if (Item.normalizeName(containerName).equals(Item.normalizeName("TreasureBox")) &&
                                    Item.normalizeName(thingName).equals(Item.normalizeName("Snakes"))) {
                                state.subtractPoints(10);
                                return "Danger! Do NOT remove snakes from the box. You lose 10 points for endangering everyone. Carry the TreasureBox to Olin instead.";
                            }
                            Item takenFrom = container.removeItem(thingName);
                            state.getInventory().addItem(takenFrom);
                            questSystem.combineSheetsInInventory();
                            return "You took " + thingName + " from " + containerName + ".";
                        }
                        return "That's a container. Try 'open " + itemName + "' first or 'take <item> from " + itemName
                                + "'";
                    }

                    if (state.getCurrLocation().hasItem(itemName)) {
                        Item taken = state.getCurrLocation().removeItem(itemName);
                        state.getInventory().addItem(taken);

                        // Check if they're getting food from the second dining location (triggers
                        // dangerous bonus potential)
                        if (foodSystem.isFoodItem(taken)) {
                            foodSystem.markFoodFromSecondDining();
                        }

                        // Phone triggers Sasha call
                        if (Item.normalizeName(taken.getName()).equals(Item.normalizeName("Phone"))
                                && !state.isSashaCalled()) {
                            state.callSasha();
                            questSystem.spawnGuitarAtLilly();
                            return "You picked up the phone. Sasha calls: 'Listen—her retirement relics slumbered on campus for years, but the school let them scatter like whispers. Dorm rumors hum, Hoover misplaced a tune at lunch, and somewhere around Lilly a footballer cradles a glowing guitar. GCPA and the library guard shadows of a case. Find it when your heart says go—we need it to wake the stage.'";
                        }
                        // DNA pickup starts task
                        if (Item.normalizeName(taken.getName()).contains("batmansd")) {
                            state.startDNATask(3);
                        }
                        questSystem.combineSheetsInInventory();
                        return "You picked up  " + itemName;
                    } else {
                        return "That item is not here";
                    }
                }
            }

            case "drop" -> {
                if (words.length < 2) {
                    return "Drop what?";
                }
                String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                if (!state.getInventory().hasItem(itemName)) {
                    return "Cannot find that item in your inventory";
                }

                Item dropped = state.getInventory().removeItem(itemName);
                state.getCurrLocation().addItem(dropped);

                if (Item.normalizeName(itemName).equals(Item.normalizeName("Help"))) {
                    state.setHelpDropLocation(state.getCurrLocation());
                }

                // DNA delivery success only at Julian
                if (Item.normalizeName(itemName).contains("batmansd")
                        && state.getCurrLocation().getName().equalsIgnoreCase("Julian") && !state.isDnaTaskComplete()) {
                    state.completeDNATask();
                    state.addPoints(15);
                    return "You dropped " + itemName
                            + ". The lab tech secures the sample. DNA delivery complete! +15 points."
                            + questSystem.maybeFinaleMessage();
                }

                return "You dropped " + itemName;
            }

            case "status" -> {
                StringBuilder statusOutput = new StringBuilder();
                statusOutput.append("=== Player Status ===\n");
                statusOutput.append("Current Location: ").append(state.getCurrLocation().getName()).append("\n");
                statusOutput.append("Total Moves: ").append(state.getMoveCount()).append("\n");
                statusOutput.append("Points: ").append(state.getPoints()).append("\n");
                statusOutput.append("Food in inventory: ").append(foodSystem.getFoodCount()).append("\n");
                statusOutput.append("Coupons left: ").append(foodSystem.getCouponCount()).append("\n");
                statusOutput.append("Inventory Size: ").append(state.getInventory().getItemNames().size()).append("\n");
                return statusOutput.toString();
            }

            case "open" -> {
                if (words.length < 2) {
                    return "Open what?";
                } else {
                    String containerName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                    if (state.getCurrLocation().hasContainerItem(containerName)) {
                        ContainerItem container = (ContainerItem) state.getCurrLocation().getItem(containerName);
                        ArrayList<String> contents = container.getContainerItemNames();
                        if (contents.isEmpty()) {
                            return "The " + containerName + " is empty.";
                        } else {
                            return "The " + containerName + " contains: " + String.join(", ", contents);
                        }
                    } else {
                        return "There's no container called " + containerName + " here.";
                    }
                }
            }

            case "put" -> {
                // Expected format: put <item> in <container>
                int inIndex = -1;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].equals("in") && i > 0 && i < words.length - 1) {
                        inIndex = i;
                        break;
                    }
                }

                if (inIndex == -1) {
                    return "Specify like: put <item> in <container>";
                }

                String itemName = String.join(" ", Arrays.copyOfRange(words, 1, inIndex));
                String containerName = String.join(" ", Arrays.copyOfRange(words, inIndex + 1, words.length));

                if (itemName.isBlank() || containerName.isBlank()) {
                    return "Put what in what? Give me a full sentence";
                }
                if (!state.getInventory().hasItem(itemName)) {
                    return "You don't have " + itemName + " in your inventory";
                }
                if (!state.getCurrLocation().hasContainerItem(containerName)) {
                    return "There's no container called " + containerName + " here";
                }

                ContainerItem container = (ContainerItem) state.getCurrLocation().getItem(containerName);
                if (container == null) {
                    return "The " + containerName + " is not a container.";
                }

                Item moving = state.getInventory().removeItem(itemName);
                container.addItem(moving);

                StringBuilder extra = new StringBuilder();

                if (containerName.equalsIgnoreCase("Aquarium")
                        && Item.normalizeName(moving.getName()).equals(Item.normalizeName("EndangeredSalmon"))
                        && !state.isSalmonTaskComplete()) {
                    state.completeSalmonTask();
                    state.addPoints(12);
                    extra.append("\nSalmon safe! Protected species secured! +12 points.");
                    // Check if both wildlife tasks are now complete
                    if (state.isSnakeTaskComplete()) {
                        state.markBothDiningVisited(); // Triggers dangerous bonus potential
                    }
                }

                if (containerName.equalsIgnoreCase("SafeBox")
                        && Item.normalizeName(moving.getName()).equals(Item.normalizeName("TreasureBox"))
                        && !state.isSnakeTaskComplete()) {
                    state.completeSnakeTask();
                    state.addPoints(10);
                    extra.append("\nDangerous Snakes secured in SafeBox! Protected everyone! +10 points.");
                    // Check if both wildlife tasks are now complete
                    if (state.isSalmonTaskComplete()) {
                        state.markBothDiningVisited(); // Triggers dangerous bonus potential
                    }
                }

                if (containerName.equalsIgnoreCase("MacbookCase")
                        && Item.normalizeName(moving.getName()).equals(Item.normalizeName("Macbook"))
                        && !state.isMacbookTaskComplete()) {
                    state.completeMacbookTask();
                    state.addPoints(7);
                    extra.append("\nMacBook returned to Admin. +7 points and gratitude from IT.");
                }

                if (containerName.equalsIgnoreCase("GuitarCase")) {
                    extra.append(questSystem.handleMusicCase(container));
                }

                if (Item.normalizeName(moving.getName()).contains("batmansd")
                        && state.getCurrLocation().getName().equalsIgnoreCase("Julian") && !state.isDnaTaskComplete()) {
                    state.completeDNATask();
                    state.addPoints(15);
                    extra.append("\nLab tech seals the DNA properly. Delivery complete! +15 points.");
                }

                extra.append(questSystem.maybeFinaleMessage());
                return "You put " + itemName + " in " + containerName + extra;
            }

            case "use" -> {
                if (words.length < 2) {
                    return "Use what?";
                } else {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                    if (itemName.toLowerCase().contains("treadmill")) {
                        if (!state.getCurrLocation().hasItem("Treadmill")) {
                            return "No treadmill here.";
                        }
                        if (state.isTreadmillUsed()) {
                            return "You've already used the treadmill.";
                        }
                        typingChallengeSystem.startChallenge("treadmill", 5);
                        return typingChallengeSystem.getPrompt();
                    } else if (state.getInventory().hasItem(itemName)) {
                        Item item = state.getInventory().getItem(itemName);
                        if (foodSystem.isFoodItem(item)) {
                            state.getInventory().removeItem(itemName);
                            state.resetHungerCounter();
                            state.incrementMoveCount();
                            return "You ate " + item.getName() + ". Hunger reset! +1 move for eating.";
                        }
                        return "You can't use that item.";
                    } else {
                        return "You can't use that item.";
                    }
                }
            }

            case "talk" -> {
                if (words.length < 2) {
                    return "Talk to whom?";
                }
                String npcName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                return questSystem.talkToNpc(npcName);
            }

            case "accept" -> {
                if (words.length < 2) {
                    return "Accept what quest?";
                }
                String questId = words[1];
                return questSystem.acceptQuest(questId);
            }

            case "quests" -> {
                StringBuilder sb = new StringBuilder("=== Quest Status ===\n");
                questSystem.getQuestStatusLine("music").ifPresent(line -> sb.append(line).append("\n"));
                questSystem.getQuestStatusLine("macbook").ifPresent(line -> sb.append(line).append("\n"));
                questSystem.getQuestStatusLine("dna").ifPresent(line -> sb.append(line).append("\n"));
                questSystem.getQuestStatusLine("salmon").ifPresent(line -> sb.append(line).append("\n"));
                questSystem.getQuestStatusLine("snake").ifPresent(line -> sb.append(line).append("\n"));
                String result = sb.toString().trim();
                return result.isEmpty() ? "No quests tracked yet." : result;
            }

            case "help" -> {
                if (state.getInventory().hasItem("Help")) {
                    return "Pull up inventory! You have a Help guide. Type 'examine Help' to read it.";
                } else if (state.getCurrLocation().hasItem("Help")) {
                    return "You dropped your Help guide here pal! Pick it up and examine it to learn how to play.";
                } else if (state.getHelpDropLocation() != null) {
                    return "You dropped your Help guide at " + state.getHelpDropLocation().getName()
                            + "! Go back there to find it and examine it.";
                } else {
                    return "You dropped your Help guide somewhere! Find it and examine it to see the game instructions.";
                }
            }

            case "hi", "hello", "hey", "yo", "sup", "wassup", "greetings", "howdy" -> {
                return "Hey there! Welcome to campus! 👋 Ready to explore? If you need guidance, just type 'examine help' from your inventory.";
            }

            case "quit" -> {
                return questSystem.getEndGameSummary();
            }

            default -> {
                state.incrementWrongCommandCount();
                if (state.getWrongCommandCount() >= 10) {
                    typingChallengeSystem.startChallenge("penalty", 5);
                    return "You keep fumbling commands. " + typingChallengeSystem.getPrompt();
                }
                return "Hmm, I'm not sure what you mean by that. Try 'examine help' for a list of commands!";
            }
        }
    }
}
