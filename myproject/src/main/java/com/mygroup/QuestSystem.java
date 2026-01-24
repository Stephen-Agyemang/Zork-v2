package com.mygroup;

import java.util.Optional;

/**
 * QuestSystem coordinates NPC interactions and quest lifecycle.
 * This is intentionally lightweight to avoid changing existing story logic; it provides
 * safe placeholders so commands can hook in later without breaking the game.
 */
public class QuestSystem {
    private final GameState state;

    public QuestSystem(GameState state) {
        this.state = state;
    }

    public String talkToNpc(String npcName) {
        if (npcName == null || npcName.isEmpty()) {
            return "Talk to whom?";
        }
        NPC npc = state.findNpcByName(npcName);
        if (npc == null) {
            return "No one named " + npcName + " is nearby.";
        }
        StringBuilder sb = new StringBuilder();
        npc.getDialogueLines().forEach(line -> sb.append(line).append("\n"));
        String questId = npc.getQuestId();
        if (questId != null) {
            Quest quest = state.getQuest(questId);
            if (quest != null && quest.getStatus() == Quest.Status.NOT_STARTED) {
                sb.append("They seem to have a task for you. Type 'accept ").append(questId).append("' to begin.");
            }
        }
        return sb.isEmpty() ? "They have nothing to say right now." : sb.toString().trim();
    }

    public String acceptQuest(String questId) {
        if (questId == null || questId.isEmpty()) {
            return "Accept which quest?";
        }
        Quest quest = state.getQuest(questId);
        if (quest == null) {
            return "No quest with id '" + questId + "' exists.";
        }
        if (quest.getStatus() == Quest.Status.COMPLETED) {
            return "You've already completed that quest.";
        }
        if (quest.getStatus() == Quest.Status.IN_PROGRESS) {
            return "You're already working on that quest.";
        }
        quest.setStatus(Quest.Status.IN_PROGRESS);
        return "Quest accepted: " + quest.getName();
    }

    public String completeQuest(String questId) {
        Quest quest = state.getQuest(questId);
        if (quest == null) {
            return "No quest with id '" + questId + "' exists.";
        }
        if (quest.getStatus() != Quest.Status.IN_PROGRESS) {
            return "You haven't started that quest.";
        }
        quest.setStatus(Quest.Status.COMPLETED);
        state.addPoints(quest.getRewardPoints());
        return "Quest completed: " + quest.getName() + " (+" + quest.getRewardPoints() + " points)";
    }

    public Optional<String> getQuestStatusLine(String questId) {
        Quest quest = state.getQuest(questId);
        if (quest == null) return Optional.empty();
        return Optional.of(quest.getId() + ": " + quest.getStatus());
    }

    public void spawnGuitarAtLilly() {
        Location lilly = state.getLillyLocation();
        if (lilly != null) {
            Item guitar = state.getPendingGuitar();
            if (guitar != null && !lilly.hasItem(guitar.getName())) {
                lilly.addItem(new Item("GlazedGuitar", "Music", "A four string guitar with a glowing green string, waiting for its true player."));
            }
        }
    }

    public void combineSheetsInInventory() {
        ContainerItem inventory = state.getInventory();
        if (inventory.hasItem("Yin") && inventory.hasItem("Yang")) {
            inventory.removeItem("Yin");
            inventory.removeItem("Yang");
            inventory.addItem(new Item("CompleteSheet", "Music", "Yin and Yang fused into one complete sheet."));
        }
    }

    public String maybeFinaleMessage() {
        // Finale banner that appears once after all core quests are complete
        if (!state.allCoreQuestsDone() || state.isFinaleShown()) {
            return "";
        }

        state.showFinale();

        StringBuilder summary = new StringBuilder();
        summary.append("\n\n========================================\n");
        summary.append("        CONGRATS — ADVENTURE COMPLETE!\n");
        summary.append("========================================\n");
        summary.append("\n");
        summary.append("  You restored the macbook, delivered DNA,\n");
        summary.append("  saved the salmon and snakes, rocked the music,\n");
        summary.append("  and finished the treadmill grind.\n\n");
        summary.append("  Total Points : ").append(state.getPoints()).append("\n");
        summary.append("  Total Moves  : ").append(state.getMoveCount()).append("\n");
        summary.append("\n");
        summary.append("  Thanks for playing ZORK v2!\n");
        summary.append("\n");
        summary.append("  (Type 'status' anytime to revisit stats)\n");
        summary.append("\n========================================\n");

        return summary.toString();
    }

    /**
     * Full end-game summary (verbatim from original GameEngine design).
     */
    public String getEndGameSummary() {
        int moveCount = state.getMoveCount();
        int points = state.getPoints();

        boolean allCoreDone = state.isMusicTaskComplete()
                && state.isDnaTaskComplete()
                && state.isSalmonTaskComplete()
                && state.isSnakeTaskComplete()
                && state.isMacbookTaskComplete()
                && state.isTreadmillUsed();

        // Apply efficiency bonus if earned
        boolean efficiencyBonus = false;
        if (moveCount < 70 && allCoreDone) {
            state.addPoints(10);
            points = state.getPoints();
            efficiencyBonus = true;
        }

        // Apply dangerous bonus: visited both dining locations AND completed all quests
        boolean dangerousBonus = false;
        if (state.isBothDiningVisited() && allCoreDone && !state.isDangerousBonusAwarded()) {
            state.addPoints(50);
            state.awardDangerousBonus();
            points = state.getPoints();
            dangerousBonus = true;
        }

        // Determine ranking (updated for 124-point max)
        String ranking;
        if (points >= 110) {
            ranking = "🏆 CAMPUS LEGEND";
        } else if (points >= 90) {
            ranking = "⭐ TRUE EXPLORER";
        } else if (points >= 70) {
            ranking = "📚 DEDICATED ADVENTURER";
        } else if (points >= 40) {
            ranking = "🎒 CASUAL WANDERER";
        } else {
            ranking = "🚶 NOVICE EXPLORER";
        }

        // Check secret achievement
        boolean secretWin = state.isVisitedHoover() && state.isVisitedDuck();

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
        summary.append("   DNA Delivery: ").append(state.isDnaTaskComplete() ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Music Quest: ").append(state.isMusicTaskComplete() ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Wildlife Rescue (Salmon): ").append(state.isSalmonTaskComplete() ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Wildlife Rescue (Snakes): ").append(state.isSnakeTaskComplete() ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   MacBook Return: ").append(state.isMacbookTaskComplete() ? "✓ Complete" : "✗ Incomplete").append("\n");
        summary.append("   Treadmill Challenge: ").append(state.isTreadmillUsed() ? "✓ Complete" : "✗ Incomplete").append("\n\n");

        if (efficiencyBonus) {
            summary.append("🌟 EFFICIENCY BONUS: Completed all quests in under 70 moves! (+10 points)\n\n");
        }

        if (dangerousBonus) {
            summary.append("⚡ DANGEROUS RISK BONUS: You visited BOTH dining locations and completed all quests! (+50 points)\n");
            summary.append("   Only the most skilled adventurers master the impossible!\n\n");
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

    public String handleMusicCase(ContainerItem guitarCase) {
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

        if (hasGuitar && hasMic && hasSheet && !state.isMusicTaskComplete()) {
            state.completeMusicTask();
            state.addPoints(20);
            return "\nThe guitarist and singer cheer—the show is saved! Music quest complete (+20 points)." + maybeFinaleMessage();
        }
        return "";
    }
}
