package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SteuerNotification implements CommandExecutor {

    public static final String PREFIX = "§8[§eSteuern§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (Beruf.getAbteilung(p, true) != Abteilung.Abteilungen.FINANZAMT && !Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (activated(p)) {
            Script.executeAsyncUpdate("DELETE from steuerNotification WHERE nrp_id = " + Script.getNRPID(p));
            p.sendMessage(PREFIX + "Die Steuerbenachrichtigung wurde deaktiviert.");
        } else {
            Script.executeAsyncUpdate("INSERT INTO steuerNotification (nrp_id, active) VALUES (" + Script.getNRPID(p) + ", TRUE)");
            p.sendMessage(PREFIX + "Die Steuerbenachrichtigung wurde aktiviert.");
        }

        return false;
    }

    public static boolean activated(Player p) {
        return Script.getBoolean(p, "steuerNotification", "active");
    }

    public static void sendNotification(String msg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Beruf.hasBeruf(player)) continue;
            if (activated(player) && Beruf.getBeruf(player).equals(Beruf.Berufe.GOVERNMENT) && (Beruf.getAbteilung(player, true) == Abteilung.Abteilungen.FINANZAMT || Beruf.isLeader(player, true))) {
                player.sendMessage(PREFIX + msg);
            }
        }
    }

}
