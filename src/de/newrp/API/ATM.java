package de.newrp.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ATM {

    ATM_1(1, new Location(Script.WORLD, 645, 66, 1056), 0),
    ATM_2(2, new Location(Script.WORLD, 645, 66, 1059), 0);

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
            if (atm.getLocation().distance(p.getLocation()) < 9) {
                a = atm;
                break;
            }
        }
        return a;
    }
}
