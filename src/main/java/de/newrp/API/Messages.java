package de.newrp.API;

import org.bukkit.entity.Player;

public class Messages {

    public static String ERROR = "§8[§c§l!§8] §c» ";
    public static String NO_PERMISSION = ERROR + "Du hast nicht die benötigten Rechte, um diesen Befehl auszuführen.";
    public static String PLAYER_NOT_FOUND = ERROR + "Spieler nicht gefunden.";
    public static String NO_SDUTY = ERROR + "Du bist nicht im Supporter-Dienst.";
    public static String INFO = "  §bInfo§8: §r";
    public static String PLAYER_FAR = ERROR + "Der Spieler ist zu weit entfernt.";
    public static String ARROW = "»";
    public static String X = "×";

    public static String RANK_PREFIX(Player p) {
        return Script.getName(p);
    }

}
