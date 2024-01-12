package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.House.House;
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

        if(!Mobile.hasPhone(p) && !p.getInventory().contains(Material.MAP) && !p.getInventory().contains(Material.FILLED_MAP) && Script.getLevel(p) >= 3) {
            p.sendMessage(Messages.ERROR + "Du benötigst ein Handy oder eine Stadtkarte um das Navi zu benutzen.");
            return true;
        }

        if(!Mobile.mobileIsOn(p) && !p.getInventory().contains(Material.MAP) && !p.getInventory().contains(Material.FILLED_MAP) && Script.getLevel(p) >= 3) {
            p.sendMessage(Messages.ERROR + "Du musst dein Handy einschalten um das Navi zu benutzen.");
            return true;
        }

        if(!Mobile.hasPhone(p) && !p.getInventory().contains(Material.MAP) && !p.getInventory().contains(Material.FILLED_MAP) && Script.getLevel(p) < 3) {
            p.sendMessage(Messages.INFO + "Bitte beachte, dass du ab Level 3 ein Handy oder eine Stadtkarte benötigst um das Navi zu benutzen.");
        }

        if (args.length == 0) {
            openDefault(p);
            return true;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("haus") || args[0].equalsIgnoreCase("house")) {
                if(Script.isInt(args[1])) {
                    int id = Integer.parseInt(args[1]);
                    House house = House.getHouseByID(id);
                    if(house == null) {
                        p.sendMessage(Messages.ERROR + "Das Haus wurde nicht gefunden.");
                        return true;
                    }

                    Location sign = house.getSignLocation();
                    if(sign == null) {
                        p.sendMessage(Messages.ERROR + "Das Haus wurde nicht gefunden.");
                        return true;
                    }

                    new Route(p.getName(), Script.getNRPID(p), p.getLocation(), sign).start();
                    p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zum Haus §6§l" + id + "§r§6 angezeigt.");
                    return true;
                }
            }
        }

        String arguments = String.join(" ", args);
        if (arguments.toLowerCase().contains("haus:") || arguments.toLowerCase().contains("house:") || arguments.toLowerCase().contains("wohnung:")) {
            if (arguments.split(":").length >= 2) {
                String s = arguments.split(":")[1];
                if (!Script.isInt(s)) {
                    p.sendMessage(Messages.ERROR + "Hausnummer wurde nicht gefunden.");
                    return true;
                }

                int index = Integer.parseInt(s);
                House haus = House.getHouseByID(index);

                if (haus == null) {
                    p.sendMessage(Messages.ERROR + "Hausnummer wurde nicht gefunden.");
                    return true;
                }

                Location sign = haus.getSignLocation();

                if (sign == null) {
                    p.sendMessage(Messages.ERROR + "Hausnummer wurde nicht gefunden.");
                    return true;
                }

                new Route(p.getName(), Script.getNRPID(p), p.getLocation(), sign).start();
                p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zum Haus §6§l" + index + "§r§6 angezeigt.");
            } else {
                p.sendMessage(Messages.ERROR + "Hausnummer wurde nicht gefunden.");
            }
        } else if (arguments.toLowerCase().contains("/")) {
            String[] input = arguments.split("/");
            if (input.length == 3) {
                if (Script.isInt(input[0]) && Script.isInt(input[1]) && Script.isInt(input[2])) {
                    int x = Integer.parseInt(input[0]);
                    int y = Integer.parseInt(input[1]);
                    int z = Integer.parseInt(input[2]);
                    p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zu den Koordinaten §6§l" + x + "/" + y + "/" + z + "§r§6 angezeigt.");
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

