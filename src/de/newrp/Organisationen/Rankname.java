package de.newrp.Organisationen;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Rankname implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if(!Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(args.length != 3) {
            p.sendMessage(Messages.ERROR + "/rankname [Rang] [Geschlecht (m/w)] [Name]");
            return true;
        }

        Organisation o = Organisation.getOrganisation(p);

        int rank;
        try {
            rank = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Der Rang muss eine Zahl sein.");
            return true;
        }

        if(rank > o.getLevel()) {
            p.sendMessage(Messages.ERROR + "Der Rang darf nicht höher als das Level deiner Organisation sein.");
            return true;
        }

        if(rank < 0) {
            p.sendMessage(Messages.ERROR + "Der Rang darf nicht kleiner als 0 sein.");
            return true;
        }


        if(args[1].equalsIgnoreCase("m")) {
            if(o.getRankName(rank, Gender.MALE).equalsIgnoreCase(args[2])) {
                p.sendMessage(Organisation.PREFIX + "Das ist bereits der Name des Rangs.");
                return true;
            }

            o.setRankName(rank, Gender.MALE, args[2]);
            o.sendLeaderMessage(Organisation.PREFIX + "Der Name des Rangs " + rank + " wurde zu " + args[2] + " geändert.");
            return true;
        } else if(args[1].equalsIgnoreCase("w")) {

            if(o.getRankName(rank, Gender.FEMALE).equalsIgnoreCase(args[2])) {
                p.sendMessage(Organisation.PREFIX + "Das ist bereits der Name des Rangs.");
                return true;
            }

            o.setRankName(rank, Gender.FEMALE, args[2]);
            o.sendLeaderMessage(Organisation.PREFIX + "Der Name des Rangs " + rank + " wurde zu " + args[2] + " geändert.");
            return true;
        }

        return false;
    }
}
