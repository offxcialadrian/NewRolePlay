package de.newrp.Administrator;

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
import java.util.List;

public class GoTo implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§eTeleport§8] §e" + Messages.ARROW + " ";

    public enum Points {

        STADTHALLE(1, "Stadthalle", new String[]{"SH"}, new Location(Script.WORLD, 544, 69, 975)),
        POLIZEIREVIER(2, "Polizeirevier", new String[]{"PR"}, new Location(Script.WORLD, 544, 69, 975));

        int id;
        String name;
        String[] alt_names;
        Location loc;

        Points(int id, String name, String[] alt_names, Location loc) {
            this.id = id;
            this.name = name;
            this.alt_names = alt_names;
            this.loc = loc;
        }

        public String getName() {
            return name;
        }

        public String[] getAltNames() {
            return alt_names;
        }

        public Location getLocation() {
            return loc;
        }

        public static Points getPointByName(String name) {
            for (Points p : Points.values()) {
                if (name.equalsIgnoreCase(p.getName())) {
                    return p;
                } else {
                    for (String s : p.getAltNames()) {
                        if (s.equalsIgnoreCase(name)) return p;
                    }
                }
            }
            return null;
        }

    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        Points gp;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/goto [Punkt]");
            return true;
        }

        gp = Points.getPointByName(args[0]);

        if (gp == null) {
            p.sendMessage(Messages.ERROR + "Punkt nicht gefunden.");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast dich zu " + gp.getName() + " teleportiert.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu " + gp.getName() + " teleportiert.", true);
        p.teleport(gp.getLocation());

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("goto")) {
            if (!SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Points point : Points.values()) {
                oneArgList.add(point.getName());
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
