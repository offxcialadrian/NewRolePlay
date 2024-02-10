package de.newrp.API;

import de.newrp.Administrator.Notifications;

public class Debug {

    public static void debug(Object msg) {
        if(Script.isInTestMode()) Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§e" + msg);
    }

}
