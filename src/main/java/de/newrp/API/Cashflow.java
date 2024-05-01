package de.newrp.API;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Cashflow {

    public static void addEntry(Player p, int amount, String reason) {
        Script.executeAsyncUpdate("INSERT INTO cashflow (nrp_id, money, reason, time, after) VALUES ('" + Script.getNRPID(p) + "', '" + amount + "', '" + reason + "', '" + System.currentTimeMillis() + "', '" + Script.getMoney(p, PaymentType.BANK) + "')");
    }

    public static void addEntry(OfflinePlayer p, int amount, String reason) {
        Script.executeAsyncUpdate("INSERT INTO cashflow (nrp_id, money, reason, time, after) VALUES ('" + Script.getNRPID(p) + "', '" + amount + "', '" + reason + "', '" + System.currentTimeMillis() + "', '" + Script.getMoney(p, PaymentType.BANK) + "')");
    }
}
