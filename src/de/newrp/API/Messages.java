package de.newrp.API;

import org.bukkit.entity.Player;

public class Messages {

    public static String NO_PERMISSION = "§c» Du hast nicht die benötigten Rechte um diesen Befehl auszuführen";
    public static String ERROR = "§c» Fehler: ";
    public static String PLAYER_NOT_FOUND = "§c» Fehler: Spieler nicht gefunden.";
    public static String NO_SDUTY = "§c» Fehler: Du bist nicht im Supporter-Dienst.";
    public static String INFO = "  §bInfo§8: §r";
    public static String WRONG_PLAYER = ERROR + "Du kannst das nicht mit dir selbst.";
    public static String ARROW = "»";

    public static String RANK_PREFIX(Player p) {
        return Script.getRank(p).getName(p) + " " + Script.getName(p);
    }

}
