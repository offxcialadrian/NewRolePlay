package de.newrp.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ATM {

    ATM_1(1, new Location(Script.WORLD, 284, 97, 640), 0),
    ATM_2(2, new Location(Script.WORLD, 337, 76, 1179), 0),
    ATM_3(3, new Location(Script.WORLD, 400, 72, 841), 0),
    ATM_4(4, new Location(Script.WORLD, 428, 78, 1074), 0),
    ATM_5(5, new Location(Script.WORLD, 430, 68, 762), 0),
    ATM_6(6, new Location(Script.WORLD, 576, 70, 968), 0),
    ATM_7(7, new Location(Script.WORLD, 776, 75, 895), 0),
    ATM_8(8, new Location(Script.WORLD, 808, 67, 713), 0),
    ATM_9(9, new Location(Script.WORLD, 929, 77, 920, -181.79962f, 17.700035f), 0),
    ATM_10(10, new Location(Script.WORLD, 933, 77, 920, -94.64969f, 16.05004f), 0),
    ATM_11(11, new Location(Script.WORLD, 936, 77, 920, -62.699703f, 18.750032f), 0),
    ATM_12(12, new Location(Script.WORLD, 802, 64, 1217, -103.067116f, 25.628996f), 0);

    private final int id;
    private final Location loc;
    private int cash;

    ATM(int id, Location loc, int cash) {
        this.id = id;
        this.loc = loc;
        this.cash = cash;
    }

    public static void restore() {
        for (ATM atm : values()) atm.cash = 100000;
    }

    public int getID() {
        return this.id;
    }

    public Location getLocation() {
        return this.loc;
    }

    public int getCash() {
        return this.cash;
    }

    public void addCash(int i) {
        int x = (getCash() + i);
        if (x > 100000) x = 100000;
        this.cash = x;
    }

    public void removeCash(int i) {
        int x = (getCash() - i);
        if (x < 0) x = 0;
        this.cash = x;
    }

    public static ATM getNearATM(Player p) {
        ATM a = null;
        for (ATM atm : ATM.values()) {
            if (atm.getLocation().distance(p.getLocation()) < 3) {
                a = atm;
                break;
            }
        }
        return a;
    }

    public static ATM getNearestATM(Location loc) {
        ATM a = null;
        for (ATM atm : ATM.values()) {
            if (a == null || atm.getLocation().distance(loc) < a.getLocation().distance(loc)) {
                a = atm;
            }
        }
        return a;
    }

}
