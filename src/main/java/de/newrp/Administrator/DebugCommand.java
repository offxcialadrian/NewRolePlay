package de.newrp.Administrator;

import de.newrp.API.Cache;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebugCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/debug [Spieler] [Debug]");
            return true;
        }

        Player tg = Bukkit.getPlayer(args[0]);

        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (args[1].equalsIgnoreCase("scoreboard")) {
            p.sendMessage("§e" + Messages.ARROW + " Du hast das Scoreboard von " + Script.getName(tg) + " zurückgesetzt.");
            tg.sendMessage("§e" + Messages.ARROW + " " + Messages.RANK_PREFIX(p) + " hat dein Scoreboard zurückgesetzt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat das Scoreboard von " + Script.getName(tg) + " zurückgesetzt.", true);
            //tg.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Cache.resetScoreboard(tg);
        } else if (args[1].equalsIgnoreCase("inventar")) {
            p.sendMessage("§e" + Messages.ARROW + " Du hast das Inventar von " + Script.getName(tg) + " zurückgesetzt.");
            tg.sendMessage("§e" + Messages.ARROW + " " + Messages.RANK_PREFIX(p) + " hat dein Inventar zurückgesetzt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat das Inventar von " + Script.getName(tg) + " zurückgesetzt.", true);
            Cache.loadInventory(tg);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("debug")) {
            if (!SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();

            // Adds all custom items to the list
            oneArgList.add("Scoreboard");
            oneArgList.add("Inventar");

            if (args.length != 2) {
                return null;
            }


            StringUtil.copyPartialMatches(args[1], oneArgList, completions);


            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

}
