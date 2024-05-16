package de.newrp.API;

import de.newrp.Administrator.Notifications;
import org.bukkit.Bukkit;

public class Debug {

    public static void debug(Object msg) {
        Bukkit.getLogger().info("Debug: " + msg);
       Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§e" + msg);
    }

}
