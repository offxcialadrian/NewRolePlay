package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRank implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Blacklist.PREFIX + "Du bist in keiner Organisation.");
            return true;
        }

        if(!Organisation.isLeader(p, true)) {
            p.sendMessage(Blacklist.PREFIX + "Du bist kein Leader.");
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Blacklist.PREFIX + "/setrank [Spieler] [Rang]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Organisation.hasOrganisation(tg)) {
            p.sendMessage(Blacklist.PREFIX + "Der Spieler ist in keiner Organisation.");
            return true;
        }

        if(Organisation.getOrganisation(p) != Organisation.getOrganisation(tg)) {
            p.sendMessage(Blacklist.PREFIX + "Der Spieler ist nicht in deiner Organisation.");
            return true;
        }

        Organisation o = Organisation.getOrganisation(p);

        int rank;
        try {
            rank = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(Blacklist.PREFIX + "Der Rang muss eine Zahl sein.");
            return true;
        }

        if(rank > o.getLevel()) {
            p.sendMessage(Blacklist.PREFIX + "Der Rang darf nicht höher als das Level deiner Organisation sein.");
            return true;
        }

        if(rank < 1) {
            p.sendMessage(Blacklist.PREFIX + "Der Rang darf nicht kleiner als 1 sein.");
            return true;
        }

        if(Organisation.getRank(tg) == rank) {
            p.sendMessage(Blacklist.PREFIX + "Der Spieler hat bereits den Rang " + rank + ".");
            return true;
        }

        o.setRank(tg, rank);
        p.sendMessage(Organisation.PREFIX + "Du hast den Rang von " + Script.getName(tg) + " auf Rang " + rank + " gesetzt.");
        tg.sendMessage(Organisation.PREFIX + "Dein Rang wurde von " + Script.getName(p) + " auf Rang " + rank + " gesetzt.");
        o.sendLeaderMessage(Organisation.PREFIX + Script.getName(p) + " hat den Rang von " + Script.getName(tg) + " auf Rang " + rank + " gesetzt.");

        return false;
    }
}
