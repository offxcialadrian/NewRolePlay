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
    ATM_9(9, new Location(Script.WORLD, 929, 77, 920), 0),
    ATM_10(10, new Location(Script.WORLD, 933, 77, 920), 0),
    ATM_11(11, new Location(Script.WORLD, 936, 77, 920), 0),
    ATM_12(12, new Location(Script.WORLD, 802, 64, 1217), 0),
    ATM_13(13, new Location(Script.WORLD, 553, 65, 1307), 0),
    ATM_14(14, new Location(Script.WORLD, 260.5, 66, 924), 0),
    ATM_15(15, new Location(Script.WORLD, 621, 66, 693), 0),
    ATM_16(16, new Location(Script.WORLD, 80.5, 66, 685.5), 0),
    ATM_17(17, new Location(Script.WORLD, 815.5, 67, 1328.5), 0),
    ATM_18(18, new Location(Script.WORLD, 625.5, 65, 1207.5), 0);

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

    public static ATM getATMByID(int id) {
        for (ATM atm : ATM.values()) {
            if (atm.getID() == id) return atm;
        }
        return null;
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
