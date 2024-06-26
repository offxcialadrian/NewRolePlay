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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckOrganisation implements CommandExecutor, TabCompleter {

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
            for(OfflinePlayer all : o.getAllLeaders()) {
                if(leaders.length() > 0) {
                    leaders.append(", ");
                }
                leaders.append("§e").append(all.getName()).append(" §8[§e").append(Organisation.getRank(all)).append("§8]");
            }
            p.sendMessage(PREFIX + "§8» §7Leader: §e" + leaders);
            StringBuilder members = new StringBuilder();
            for(OfflinePlayer all : o.getAllMembers()) {
                if(members.length() > 0) {
                    members.append(", ");
                }
                members.append("§e").append(all.getName()).append(" §8[§e").append(Organisation.getRank(all)).append("§8]");
            }
            p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + members);
            p.sendMessage(PREFIX + "§8» §7Rangnamen:");
            for(int i = 0; i <= 6; i++) {
                p.sendMessage(PREFIX + "§8» §7Rang " + i + ": §e" + o.getRankName(i, Gender.MALE) + " §8| §e" + o.getRankName(i, Gender.FEMALE));
            }
            return true;
        }

        if(!Script.hasRank(p, Rank.DEVELOPER, false)) {
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
        for(OfflinePlayer all : o.getAllLeaders()) {
            if(leaders.length() > 0) {
                leaders.append(", ");
            }
            leaders.append(all.getName());
        }
        p.sendMessage(PREFIX + "§8» §7Leader: §e" + leaders);
        StringBuilder members = new StringBuilder();
        for(OfflinePlayer all : o.getAllMembers()) {
            if(members.length() > 0) {
                members.append(", ");
            }
            members.append("§e" + all.getName() + " §8[§e" + Organisation.getRank(all) + "§8]");
        }
        p.sendMessage(PREFIX + "§8» §7Mitglieder: §e" + members);
        p.sendMessage(PREFIX + "§8» §7Rangnamen:");
        for(int i = 0; i <= 6; i++) {
            p.sendMessage(PREFIX + "§8» §7Rang " + i + ": §e" + o.getRankName(i, Gender.MALE) + " §8| §e" + o.getRankName(i, Gender.FEMALE));
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("checkorg") || cmd.getName().equalsIgnoreCase("checkorganisation")) {
            if (!SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for(Organisation org : Organisation.values()) {
                oneArgList.add(org.getName());
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

}
