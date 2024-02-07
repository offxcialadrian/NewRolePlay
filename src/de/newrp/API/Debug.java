package de.newrp.API;

public class Debug {

    public static void debug(Object msg) {
        if(Script.isInTestMode()) Script.sendTeamMessage("§e" + msg);
    }

}
