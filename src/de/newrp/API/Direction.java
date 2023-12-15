package de.newrp.API;

public enum Direction {
    NORTH(-180.0F, "north"),
    EAST(-90.0F, "east"),
    SOUTH(0.0F, "south"),
    WEST(-270.0F, "west");

    private final float pitch;
    private final String name1;

    Direction(float direction, String name) {
        this.pitch = direction;
        this.name1 = name;
    }

    public float getYaw() {
        return this.pitch;
    }

    public String getName() {
        return this.name1;
    }
}
