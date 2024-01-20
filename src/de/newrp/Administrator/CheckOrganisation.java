package de.newrp.Administrator;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckOrganisation implements CommandExecutor {

    public static String PREFIX = "§8[§eCheckOrganisation§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(Organisation.hasOrganisation(p) && !SDuty.isSDuty(p)) {
            Organisation o = Organisation.getOrganisation(p);
            if(o == null) {
                p.sendMessage(Messages.ERROR + "Die Organisation wurde nicht gefunden.");
                return true;
            }

            p.sendMessage(PREFIX + "§8=== §6" + o.getName() + " §8===");
            p.sendMessage(PREFIX + "§8» §7Name: §e" + o.getName());
            p.sendMessage(PREFIX + "§8» §7Level: §e" + o.getLevel());
            p.sendMessage(PREFIX + "§8» §7Exp: §e" + o.getExp() + " / " + o.getLevelCost() + " (" + Script.getPercentage(o.getExp(), o.getLevelCost()) + "%)");
            p.sendMessage(PREFIX + "§8» §7Blacklist: §e" + (o.hasBlacklist() ? "Ja" : "Nein"));
            p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + o.getAllMembers().size());
            StringBuilder leaders = new StringBuilder();
            for(OfflinePlayer all : o.getLeaders()) {
                if(leaders.length() > 0) {
                    leaders.append(", ");
                }
                leaders.append("§7").append(all.getName()).append(" §8[§e").append(Organisation.getRank(all)).append("§8]");
            }
            p.sendMessage(PREFIX + "§8» §7Leader: §e" + leaders.toString());
            StringBuilder members = new StringBuilder();
            for(OfflinePlayer all : o.getAllMembers()) {
                if(members.length() > 0) {
                    members.append(", ");
                }
                members.append("§e").append(all.getName()).append(" §8[§e").append(Organisation.getRank(all)).append("§8]");
            }
            p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + members.toString());
            p.sendMessage(PREFIX + "§8» §7Rangnamen:");
            for(int i = 0; i <= o.getLevel(); i++) {
                p.sendMessage(PREFIX + "§8» §7Rang " + i + ": §e" + o.getRankName(i, Gender.MALE) + " §8| §e" + o.getRankName(i, Gender.FEMALE));
            }
        }

        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkorganisation [Organisation]");
            return true;
        }

        Organisation o = Organisation.getOrganisation(args[0]);
        if(o == null) {
            p.sendMessage(Messages.ERROR + "Die Organisation wurde nicht gefunden.");
            return true;
        }

        p.sendMessage(PREFIX + "§8=== §6" + o.getName() + " §8===");
        p.sendMessage(PREFIX + "§8» §7Name: §e" + o.getName());
        p.sendMessage(PREFIX + "§8» §7Level: §e" + o.getLevel());
        p.sendMessage(PREFIX + "§8» §7Exp: §e" + o.getExp() + " / " + o.getLevelCost() + " (" + Script.getPercentage(o.getExp(), o.getLevelCost()) + "%)");
        p.sendMessage(PREFIX + "§8» §7Kontostand: §e" + o.getKasse() + "€");
        p.sendMessage(PREFIX + "§8» §7Blacklist: §e" + (o.hasBlacklist() ? "Ja" : "Nein"));
        p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + o.getAllMembers().size());
        StringBuilder leaders = new StringBuilder();
        for(OfflinePlayer all : o.getLeaders()) {
            if(leaders.length() > 0) {
                leaders.append(", ");
            }
            leaders.append(all.getName());
        }
        p.sendMessage(PREFIX + "§8» §7Leader: §e" + leaders.toString());
        StringBuilder members = new StringBuilder();
        for(OfflinePlayer all : o.getAllMembers()) {
            if(members.length() > 0) {
                members.append(", ");
            }
            members.append("§e" + all.getName() + " §8[§e" + Organisation.getRank(all) + "§8]");
        }
        p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + members.toString());
        p.sendMessage(PREFIX + "§8» §7Rangnamen:");
        for(int i = 0; i <= o.getLevel(); i++) {
            p.sendMessage(PREFIX + "§8» §7Rang " + i + ": §e" + o.getRankName(i, Gender.MALE) + " §8| §e" + o.getRankName(i, Gender.FEMALE));
        }


        return false;
    }
}
