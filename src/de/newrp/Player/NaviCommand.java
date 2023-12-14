package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Route;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaviCommand implements CommandExecutor, TabCompleter {


    public static void openDefault(Player p) {
        Inventory inv = p.getServer().createInventory(null, 9, "§e§lNavi");
        inv.setItem(0, Script.setName(Material.PAPER, "§6Allgemein"));
        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (Route.getRoute(p) != null) {
            p.sendMessage(Navi.PREFIX + "Du hast deine Route gelöscht.");
            Route.getRoute(p).invalidate();
            return true;
        }

        if (args.length == 0) {
            openDefault(p);
            return true;
        }

        String arguments = String.join(" ", args);
        if (arguments.toLowerCase().contains("/")) {
            String[] input = arguments.split("/");
            if (input.length == 3) {
                if (Script.isInt(input[0]) && Script.isInt(input[1]) && Script.isInt(input[2])) {
                    int x = Integer.parseInt(input[0]);
                    int y = Integer.parseInt(input[1]);
                    int z = Integer.parseInt(input[2]);

                    new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new Location(p.getWorld(), x, y, z)).start();
                } else {
                    p.sendMessage(Messages.ERROR + "Ungültige Koordinate.");
                }
            } else {
                p.sendMessage(Messages.ERROR + "Ungültige Koordinate.");
            }
        } else {
            Navi n = null;
            for (Navi navi : Navi.values()) {
                if (navi.getName().equalsIgnoreCase(arguments)) {
                    n = navi;
                    break;
                }
            }

            if (n == null) {
                openDefault(p);
                return true;
            }

            p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zum Punkt §6§l" + n.getName() + "§r§6 angezeigt.");
            p.sendMessage(Messages.INFO + "Mit /navistop oder erneut /navi wird die Route gelöscht.");
            new Route(p.getName(), Script.getNRPID(p), p.getLocation(), n.getLocation()).start();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("navi")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Navi navi : Navi.values()) {
                oneArgList.add(navi.getName());
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

