package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class SetRank implements CommandExecutor {

    public static String PREFIX = "§8[§eSetRank§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(PREFIX + "Du bist in keiner Organisation.");
            return true;
        }

        if(!Organisation.isLeader(p, true)) {
            p.sendMessage(PREFIX + "Du bist kein Leader.");
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(PREFIX + "/setrank [Spieler] [Rang]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Organisation.hasOrganisation(tg)) {
            p.sendMessage(PREFIX + "Der Spieler ist in keiner Organisation.");
            return true;
        }

        if(Organisation.getOrganisation(p) != Organisation.getOrganisation(tg)) {
            p.sendMessage(PREFIX + "Der Spieler ist nicht in deiner Organisation.");
            return true;
        }

        Organisation o = Organisation.getOrganisation(p);

        int rank;
        try {
            rank = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(PREFIX + "Der Rang muss eine Zahl sein.");
            return true;
        }

        if(rank > 6) {
            p.sendMessage(PREFIX + "Organisationen haben nicht mehr als 6 Ränge.");
            return true;
        }

        if(rank < 0) {
            p.sendMessage(PREFIX + "Der Rang darf nicht kleiner als 0 sein.");
            return true;
        }

        if(Organisation.getRank(tg) == rank) {
            p.sendMessage(PREFIX + "Der Spieler hat bereits den Rang " + rank + ".");
            return true;
        }

        if (rank > 1 && rank >= Organisation.getRank(tg)) {
            if (!Script.hasRank(p, Rank.FRAKTIONSMANAGER, false)) {
                if (System.currentTimeMillis() - Organisation.getInvite(tg) < TimeUnit.DAYS.toMillis(7 * (rank - 1))) {
                    p.sendMessage(PREFIX + "Der Spieler kann erst in " + TimeUnit.MILLISECONDS.toDays(TimeUnit.DAYS.toMillis(7 * rank) - (System.currentTimeMillis() - Organisation.getInvite(tg))) + " Tagen Rang-" + rank + " werden.");
                    return true;
                }
            } else {
                if (!SDuty.isSDuty(p)) {
                    if (System.currentTimeMillis() - Organisation.getInvite(tg) < TimeUnit.DAYS.toMillis(7 * (rank - 1))) {
                        p.sendMessage(PREFIX + "Der Spieler kann erst in " + TimeUnit.MILLISECONDS.toDays(TimeUnit.DAYS.toMillis(7 * rank) - (System.currentTimeMillis() - Organisation.getInvite(tg))) + " Tagen Rang-" + rank + " werden.");
                        return true;
                    }
                }
            }
        }

        o.setRank(tg, rank);
        p.sendMessage(Organisation.PREFIX + "Du hast den Rang von " + Script.getName(tg) + " auf Rang " + rank + " gesetzt.");
        if(tg.getPlayer() !=null) tg.getPlayer().sendMessage(Organisation.PREFIX + "Dein Rang wurde von " + Script.getName(p) + " auf Rang " + rank + " gesetzt.");
        o.sendLeaderMessage(Organisation.PREFIX + Script.getName(p) + " hat den Rang von " + Script.getName(tg) + " auf Rang " + rank + " gesetzt.");

        return false;
    }
}
