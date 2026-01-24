package com.mygroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NPC represents a non-player character that can hand out quests or dialogue.
 */
public class NPC {
    private final String id;
    private String name;
    private String locationName;
    private final List<String> dialogueLines;
    private String questId; // optional quest the NPC is related to

    public NPC(String id, String name, String locationName) {
        this.id = id;
        this.name = name;
        this.locationName = locationName;
        this.dialogueLines = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<String> getDialogueLines() {
        return Collections.unmodifiableList(dialogueLines);
    }

    public void addDialogueLine(String line) {
        if (line != null && !line.isEmpty()) {
            dialogueLines.add(line);
        }
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }
}
