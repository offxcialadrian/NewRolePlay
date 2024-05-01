package de.newrp.Votifier;

public enum NotificationType {

    FULL(0, 0),
    TEXT(1, 4),
    CHAT(2, 8);

    private final int id;
    private final int min;

    NotificationType(int id, int min) {
        this.id = id;
        this.min = min;
    }

    public int getID() {
        return this.id;
    }

    public int getMinDays() {
        return this.min;
    }
}
