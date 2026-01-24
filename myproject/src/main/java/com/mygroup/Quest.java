package com.mygroup;

/**
 * Quest represents a goal or task for the player.
 */
public class Quest {
    public enum Status { NOT_STARTED, IN_PROGRESS, COMPLETED }

    private final String id;
    private String name;
    private String description;
    private Status status;
    private int rewardPoints;

    public Quest(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NOT_STARTED;
        this.rewardPoints = 0;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
