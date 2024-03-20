package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.House.House;
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

        STADTHALLE(1, "Stadthalle", new String[]{"SH", "Regierung"},new Location(Script.WORLD, 581, 69, 992, -269.48206f, -3.5868206f)),
        POLIZEIREVIER(2, "Polizeirevier", new String[]{"PR", "Polizeirevier", "Police", "Cop", "Cops", "police"}, new Location(Script.WORLD, 405, 71, 852, 177.84886f, 5.210484f)),
        KRANKENHAUS  (3, "Krankenhaus", new String[]{"Krankenhaus", "KH", "Hospitalm", "Medic"}, new Location(Script.WORLD, 320, 75, 1232, 90.95497f, -1.2530943f)),
        MALL (4, "Mall", new String[]{"Einkaufszentrum"}, new Location(Script.WORLD, 827, 74, 935)),
        NEWS (5, "News", new String[]{"News", "Nachrichten", "Redaktion"}, new Location(Script.WORLD, 326, 67, 763)),
        X3 (6, "X3", new String[]{"X3-Tower"}, new Location(Script.WORLD, 679, 160, 991, -272.33267f, 4.513469f)),
        GERICHT   (7, "Gericht", new String[]{"Court"}, new Location(Script.WORLD, 763, 77, 950)),
        HAUSADDON   (8, "AEKI", new String[]{"Hausaddon", "Houseaddon"}, new Location(Script.WORLD, 674, 68, 897)),
        HAFEN (9, "Hafen", new String[]{"Harbor"}, new Location(Script.WORLD, 971, 66, 1104, -91.79942f, -0.2999976f)),
        STAATSBANK (10, "Staatsbank", new String[]{"SB", "Bank"}, new Location(Script.WORLD, 924, 77, 934, -90.74999f, 3.3000422f)),
        FLUGHAFEN (11, "Flughafen", new String[]{"Airport"}, new Location(Script.WORLD, 872, 66, 1166)),
        SCHULE (12, "Schule", new String[]{"Berufsschule, BS"}, new Location(Script.WORLD, 701, 67, 763, -90.90038f, -3.2999737f)),
        TAXI (13, "Taxi", new String[]{"Taxihq"}, new Location(Script.WORLD, 689, 69, 1071, 359.399f, 7.2000093f)),
        ARCADE (14, "Arcade", new String[]{"Arcadehalle"}, new Location(Script.WORLD, 431, 67, 772, 91.49992f, -0.3000614f)),
        WAFFENLADEN(15, "Waffenladen", new String[]{"Gunshop"}, new Location(Script.WORLD, 452, 69, 928, -51.94748f, 15.066549f)),
        FREIZEITPARK (15, "Freizeitpark", new String[]{"Park"}, new Location(Script.WORLD, 817, 66, 723)),
        MOTEL (16, "Motel", new String[]{"Motel99"}, new Location(Script.WORLD, 795, 64, 1222)),
        KNAST (17, "Gefängnis", new String[]{"Knast, Jail, JVA"}, new Location(Script.WORLD, 1018, 68, 549, 180.84424f, -9.02183f)),
        CASINO (18, "Casino", new String[]{"Spielhalle"}, new Location(Script.WORLD, 780, 109, 856, 0.22738647f, 5.2779894f)),
        SELFSTORAGE(19, "Selfstorage", new String[]{"Selfstorage, MyPlace, Lagerhalle"},new Location(Script.WORLD, 1001, 68, 1201, 274.2714f, 3.0181432f)),
        TODO(20, "TODO", new String[]{"TODO"}, new Location(Script.WORLD, 583, 122, 1023, -180.83813f, -6.3143415f)),
        SUPERMARKT(21, "Supermarkt", new String[]{"Supermarkt"}, new Location(Script.WORLD, 625, 68, 859, 270.81003f, 1.8723769f)),
        LAGERARBEITER(22, "Lagerarbeiter", new String[]{"Lagerhalle"},new Location(Script.WORLD, 997, 69, 1255, 0.1505936f, 7.0500054f)),
        BAU_TEAM(23, "Bau-Team", new String[]{"Bau-Team", "Grabbelkiste", "Bau"}, new Location(Script.WORLD, 572, 138, 2223, 216.6613f, -2.7775428f)),
        EISHALLE(24, "Eishalle", new String[]{"Eishalle"}, new Location(Script.WORLD, 385, 67, 764, 90.90038f, 0.0f)),
        PIZZALIEFERANT(25, "Pizzalieferant", new String[]{"Pizza"}, new Location(Script.WORLD, 637, 69, 884, 66.48648f, 9.386628f)),
        TRANSPORT(26, "Transport", new String[]{"Transport"}, new Location(Script.WORLD, 935, 66, 1079, 90.90038f, 0.0f)),
        KELLNER(27, "Kellner", new String[]{"Kellner"}, new Location(Script.WORLD, 425, 69, 934, 90.90038f, 0.0f)),
        DISHWASHER(28, "Tellerwäscher", new String[]{"Tellerwäscher"}, new Location(Script.WORLD, 589, 67, 729, -358.82983f, 3.6454659f)),
        BURGERBRATER(29, "Burgerbrater", new String[]{"Burgerbrater"},new Location(Script.WORLD, 463, 67, 785, 180.7009f, 2.8104913f)),
        STRASSENWARTUNG(30, "Straßenwartung", new String[]{"Straßenwartung"}, new Location(Script.WORLD, 470, 66, 1310, 2.0073476f, 4.0150375f)),
        IMKER(31, "Imker", new String[]{"Imker"}, new Location(Script.WORLD, 216, 66, 773, 201.95145f, 0.35791647f)),
        HOTEL(32, "Hotel", new String[]{"Hotel"}, new Location(Script.WORLD, 243, 66, 971, -87.70117f, -0.74990237f)),
        GYM(33, "Gym", new String[]{"Fitness", "Fitnessstudio"}, new Location(Script.WORLD, 461, 67, 742, -324.78662f, 2.9995012f));

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
        Navi gp;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            if(!Script.isInTestMode() || !BuildMode.isInBuildMode(p)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
        }

        if (!SDuty.isSDuty(p)) {
            if(!Script.isInTestMode() || !BuildMode.isInBuildMode(p)) {
                p.sendMessage(Messages.NO_SDUTY);
                return true;
            }
        }

        if(args.length == 2) {
            if(!args[0].equalsIgnoreCase("house") && !args[0].equalsIgnoreCase("h") && !args[0].equalsIgnoreCase("haus")) {
                p.sendMessage(Messages.ERROR + "/goto [Punkt]");
                return true;
            }

            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl ein.");
                return true;
            }

            int id = Integer.parseInt(args[1]);
            House house = House.getHouseByID(id);
            if(house == null) {
                p.sendMessage(Messages.ERROR + "Haus nicht gefunden.");
                return true;
            }

            p.sendMessage(PREFIX + "Du hast dich zu Haus " + house.getID() + " teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu Haus " + house.getID() + " teleportiert.", true);
            Teleport.back.put(p, p.getLocation());
            p.teleport(house.getSignLocation());
            Log.NORMAL.write(p, "teleportierte sich zu Haus " + house.getID() + ".");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/goto [Punkt]");
            return true;
        }

        if(args[0].startsWith("house:") || args[0].startsWith("h:") || args[0].startsWith("haus:")) {
            String[] split = args[0].split(":");
            if(split.length != 2) {
                p.sendMessage(Messages.ERROR + "/goto [Punkt]");
                return true;
            }

            if(!Script.isInt(split[1])) {
                p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl ein.");
                return true;
            }

            int id = Integer.parseInt(split[1]);
            House house = House.getHouseByID(id);
            if(house == null) {
                p.sendMessage(Messages.ERROR + "Haus nicht gefunden.");
                return true;
            }

            p.sendMessage(PREFIX + "Du hast dich zu Haus " + house.getID() + " teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu Haus " + house.getID() + " teleportiert.", true);
            Teleport.back.put(p, p.getLocation());
            p.teleport(house.getSignLocation());
            Log.NORMAL.write(p, "teleportierte sich zu Haus " + house.getID() + ".");
            return true;
        }

        gp = Navi.getNaviByName(args[0].replace("-", " "));

        if (gp == null) {
            GoTo.Points point = GoTo.Points.getPointByName(args[0]);
            if (point == null) {
                p.sendMessage(Messages.ERROR + "Punkt nicht gefunden.");
                return true;
            }

            p.sendMessage(PREFIX + "Du hast dich zu " + point.getName() + " teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu " + point.getName() + " teleportiert.", true);
            Teleport.back.put(p, p.getLocation());
            p.teleport(point.getLocation());
            Log.NORMAL.write(p, "teleportierte sich zu " + point.getName() + ".");
            return true;

        }

        p.sendMessage(PREFIX + "Du hast dich zu " + gp.getName() + " teleportiert.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu " + gp.getName() + " teleportiert.", true);
        Teleport.back.put(p, p.getLocation());
        p.teleport(gp.getLocation());
        Log.NORMAL.write(p, "teleportierte sich zu " + gp.getName() + ".");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("goto")) {
            if (!SDuty.isSDuty(p) && !BuildMode.isInBuildMode(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Navi point : Navi.values()) {
                oneArgList.add(point.getName().replace(" ", "-"));
            }

            for(GoTo.Points gp : GoTo.Points.values()) {
                if(!oneArgList.contains(gp.getName())) {
                    oneArgList.add(gp.getName());
                }
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
