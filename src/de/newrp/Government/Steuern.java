package de.newrp.Government;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Steuern implements CommandExecutor, TabCompleter {


    private static final long TIMEOUT = TimeUnit.HOURS.toMillis(6);
    private static long lastTime;

    private static final String PREFIX = "§8[§eSteuern§8] §e» ";

    private static HashMap<Steuer, Long> steuern = new HashMap<>();

    public enum Steuer {
        EINKOMMENSSTEUER(1, "Einkommenssteuer", "einkommenssteuer", "Die Einkommenssteuer wird auf dein Profit am PayDay erhoben."),
        ARBEITSLOSENVERSICHERUNG(2, "Arbeitslosenversicherung", "arbeitslosenversicherung", "Die Arbeitslosenversicherung wird auf dein Gehalt am PayDay erhoben."),
        LOHNSTEUER(3, "Lohnsteuer", "lohnsteuer", "Die Lohnsteuer wird auf dein Gehalt am PayDay erhoben."),
        GFB_LOHNSTEUER(4, "Lohnsteuer für geringfügig Beschäftigte", "gfb_lohnsteuer", "Die Lohnsteuer für geringfügig Beschäftigte wird auf dein Gehalt am PayDay erhoben."),
        GEWERBESTEUER(5, "Gewerbesteuer", "gewerbesteuer", "Die Gewerbesteuer wird jeden PayDay auf jeden Shop erhoben."),
        MEHRWERTSTEUER(6, "Mehrwertsteuer", "mehrwertsteuer", "Die Mehrwertsteuer wird auf jeden Kauf in Shops erhoben."),
        SHOP_VERKAUFSSTEUER(7, "Shop Verkaufssteuer", "shop_verkaufssteuer", "Die Shop Verkaufssteuer wird auf jeden Verkauf eines Shops erhoben."),
        GRUNDSTEUER(8, "Grundsteuer", "grundsteuer", "Die Grundsteuer wird jeden PayDay auf jedes Haus erhoben."),
        HAUSVERKAUFSSTEUER(9, "Haus Verkaufssteuer", "haus_verkaufssteuer", "Die Haus Verkaufssteuer wird auf jeden Verkauf eines Hauses erhoben."),
        KRANKENVERSICHERUNG(10, "Krankenversicherung", "krankenversicherung", "Die Krankenversicherung wird auf dein Gehalt am PayDay erhoben.");

        private final int id;
        private final String name;
        private final String dbname;
        private final String description;

        Steuer(int id, String name, String dbname, String description) {
            this.id = id;
            this.name = name;
            this.dbname = dbname;
            this.description = description;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDBName() {
            return dbname;
        }

        public String getDescription() {
            return description;
        }

        public double getPercentage() {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM city")) {
                if (rs.next()) {
                    return rs.getDouble(this.dbname);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (args.length == 0) {
            p.sendMessage("§8[§eSteuern & Sozialversicherungen§8] §e ");
            for (Steuer steuer : Steuer.values()) {
                if(steuer == Steuer.GEWERBESTEUER || steuer == Steuer.GRUNDSTEUER) {
                    p.sendMessage("§8" + Messages.ARROW + " §6" + steuer.getName() + " §8× §6" + steuer.getPercentage() + "€");
                    continue;
                }
                p.sendMessage("§8" + Messages.ARROW + " §6" + steuer.getName() + " §8× §6" + steuer.getPercentage() + "%");
            }
            return true;
        }

        if (args.length == 1) {
            for (Steuer steuer : Steuer.values()) {
                if (steuer.getName().equalsIgnoreCase(args[0]) || steuer.getDBName().equalsIgnoreCase(args[0])) {
                    if(steuer == Steuer.GEWERBESTEUER || steuer == Steuer.GRUNDSTEUER) {
                        p.sendMessage("§8[§eSteuern§8] §e» §7Die " + steuer.getName() + " beträgt §6" + steuer.getPercentage() + "€");
                        p.sendMessage("§8[§eSteuern§8] §e» §7" + steuer.getDescription());
                        continue;
                    }
                    p.sendMessage("§8[§eSteuern§8] §e» §7Die " + steuer.getName() + " beträgt §6" + steuer.getPercentage() + "%.");
                    p.sendMessage("§8[§eSteuern§8] §e» §7" + steuer.getDescription());
                    return true;
                } else {
                    p.sendMessage(Messages.ERROR + "Die Steuer wurde nicht gefunden.");
                    return true;
                }
            }
        }

        if (args.length == 3) {

            if (Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT) {
                p.sendMessage(Messages.ERROR + "Du bist kein Regierungsmitglied.");
                return true;
            }

            if (!Beruf.isLeader(p, false)) {
                p.sendMessage(Messages.ERROR + "Nur das Staatsoberhaupt kann die Steuern ändern.");
                return true;
            }

            if (!args[0].equalsIgnoreCase("set")) {
                p.sendMessage(Messages.ERROR + "/steuern set [Steuer] [Prozentzahl]");
                return true;
            }


            for (Steuer steuer : Steuer.values()) {
                if (steuer.getDBName().equalsIgnoreCase(args[1])) {
                    try {
                        double percentage = Double.parseDouble(args[2]);
                        if (percentage < 0 || percentage > 100) {
                            if (steuer != Steuer.GEWERBESTEUER && steuer != Steuer.GRUNDSTEUER) {
                                p.sendMessage(Messages.ERROR + "Die Prozentzahl muss zwischen 0 und 100 liegen.");
                                return true;
                            }
                        }

                        if((steuer == Steuer.GEWERBESTEUER || steuer==Steuer.GRUNDSTEUER) && percentage < 0) {
                            p.sendMessage(Messages.ERROR + "Die Steuer kann kein negativer Betrag sein.");
                            return true;
                        }

                        if (steuer.getPercentage() == percentage) {
                            p.sendMessage(Messages.ERROR + "Die " + steuer.getName() + " beträgt bereits " + percentage + "%.");
                            return true;
                        }

                        if(steuern.containsKey(steuer)) {
                            long leftTime = TIMEOUT - (System.currentTimeMillis() - steuern.get(steuer));
                            if (leftTime > 0) {
                                p.sendMessage(Messages.ERROR + "Du kannst die Steuern erst in " + TimeUnit.MILLISECONDS.toMinutes(leftTime) + " Minuten wieder ändern.");
                                return true;
                            }
                        }

                        steuern.put(steuer, System.currentTimeMillis());
                        Log.WARNING.write(p, "hat die " + steuer.getName() + " auf " + percentage + "% gesetzt.");
                        if(steuer == Steuer.GEWERBESTEUER || steuer == Steuer.GRUNDSTEUER) {
                            p.sendMessage(PREFIX + "Du hast die " + steuer.getName() + " von " + steuer.getPercentage() + "€ auf §6" + percentage + "€ §e gesetzt.");
                            Bukkit.broadcastMessage("§8[§6NEWS§8] §6" + Messages.ARROW + " Die Regierung hat beschlossen die " + steuer.getName() + " von " + steuer.getPercentage() + "€ auf §6" + percentage + "€ zu setzen.");
                            return true;
                        }
                        p.sendMessage(PREFIX + "Du hast die " + steuer.getName() + " von " + steuer.getPercentage() + "% auf §6" + percentage + "% §egesetzt.");
                        Bukkit.broadcastMessage("§8[§6NEWS§8] §6" + Messages.ARROW + " Die Regierung hat beschlossen die " + steuer.getName() + " von " + steuer.getPercentage() + "% auf §6" + percentage + "% zu setzen.");
                        Script.executeAsyncUpdate("UPDATE city SET " + steuer.getDBName() + " = " + percentage);
                        return true;
                    } catch (Exception e) {
                        p.sendMessage(Messages.ERROR + "Bitte gib eine gültige Prozentzahl an.");
                        return true;
                    }
                }
            }
            p.sendMessage(Messages.ERROR + "Die Steuer wurde nicht gefunden.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("steuern")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Steuer steuer : Steuern.Steuer.values()) {
                oneArgList.add(steuer.getDBName());
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], oneArgList, completions);
            }

            if (args.length == 3) {
                return null;
            }


            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }
}
