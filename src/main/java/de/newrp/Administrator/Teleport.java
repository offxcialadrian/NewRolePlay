package de.newrp.Administrator;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Teleport implements CommandExecutor, TabCompleter {

    public static String PREFIX = "§8[§eTeleport§8] §e" + Messages.ARROW + " ";
    public static HashMap<Player, Location> back = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1 && args.length != 2) {
            p.sendMessage(Messages.ERROR + "/tp [Spieler] {GoTo}");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst teleportieren.");
            return true;
        }

        if(Friedhof.isDead(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist tot.");
            return true;
        }

        if(args.length == 1) {
            back.put(p, p.getLocation());
            p.teleport(tg.getLocation());
            p.sendMessage(PREFIX + "Du hast dich zu " + Script.getName(tg) + " teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu " + Script.getName(tg) + " teleportiert.", true);
            return true;
        }

        GoTo.Points gp = GoTo.Points.getPointByName(args[1]);
        if (gp == null) {
            p.sendMessage(Messages.ERROR + "Punkt nicht gefunden.");
            return true;
        }

        back.put(tg, tg.getLocation());
        tg.teleport(gp.getLocation());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " zu " + gp.getName() + " teleportiert.");
        tg.sendMessage(PREFIX + "Du wurdest zu " + gp.getName() + " teleportiert.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " zu " + gp.getName() + " teleportiert.", true);


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("tp")) {
            if (!SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (GoTo.Points point : GoTo.Points.values()) {
                oneArgList.add(point.getName());
            }



            if (args.length == 1) {
                return null;
            }

            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], oneArgList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }
}
