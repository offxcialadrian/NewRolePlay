package de.newrp.API;

import org.bukkit.Location;

public enum HologramList {

    CAFE_SH(1, "§8/§6buy", new Location(Script.WORLD, 626, 67, 1030)),
    BANKENCOMMAND(2, "§8/§6banken", new Location(Script.WORLD, 949, 77, 934.5)),
    AEKI(3, "§8/§6buy", new Location(Script.WORLD, 681, 67, 902)),
    AEKI_CAFE(4, "§8/§6buy",new Location(Script.WORLD, 689, 137, 909)),
    CAFE_AM_X3(5, "§8/§6buy",new Location(Script.WORLD, 754, 71, 924)),
    GUNSHOP(6, "§8/§6buy",new Location(Script.WORLD, 454, 68, 929)),
    EQUIP_COPS(7, "§8/§6equip",new Location(Script.WORLD, 405, 70, 824)),
    DUTY_COPS(8, "§8/§6duty",new Location(Script.WORLD, 408, 70, 824));

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
