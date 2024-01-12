package de.newrp.API;

import org.bukkit.Location;

public enum Navi {
    STADTHALLE("Stadthalle", new Location(Script.WORLD, 555, 70, 991, 88.77539f, 2.5500057f));


    private final String name;
    private final Location loc;

    Navi(String name, Location loc) {
        this.name = name;
        this.loc = loc;
    }

    public static String PREFIX = "§8[§6Navi§8]§6 " + Messages.ARROW + " ";

    public static Navi getNextNaviLocation(Location loc) {
        Navi n = Navi.STADTHALLE;
        double distance = 1000000D;
        if (loc != null) {
            for (Navi navi : values()) {
                if (navi.getLocation() != null) {
                    double d = loc.distanceSquared(navi.getLocation());
                    if (d <= distance) {
                        n = navi;
                        distance = d;
                    }
                }
            }
        }
        return n;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }
}
