package de.newrp.API;

import org.bukkit.Location;

public enum HologramList {

    CAFE_SH(1, "§8/§6buy", new Location(Script.WORLD, 626, 67, 1030));

    private final int id;
    private final String name;
    private final Location loc;

    HologramList(int id, String name, Location loc) {
        this.id = id;
        this.name = name;
        this.loc = loc;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }
}
