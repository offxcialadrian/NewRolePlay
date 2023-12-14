package de.newrp.API;

import org.bukkit.Bukkit;

public class Debug {

    public static void debug(Object msg) {
        Bukkit.broadcastMessage("§e" + msg);
    }

}
