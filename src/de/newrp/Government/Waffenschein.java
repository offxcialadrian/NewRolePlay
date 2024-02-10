package de.newrp.Government;

import de.newrp.API.*;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;

public class Waffenschein implements CommandExecutor {

    public static String PREFIX = "§8[§eWaffenschein§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (args.length == 0) {
                if(Beruf.getAbteilung(p) == Abteilung.Abteilungen.JUSTIZMINISTERIUM) {
                    sendApplications(p);
                    return true;
                }
            }


            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("accept")) {
                    p.sendMessage(PREFIX + "Bitte gib eine ID an.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("deny")) {
                    p.sendMessage(PREFIX + "Bitte gib eine ID an.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("list")) {
                    sendApplications(p);
                    return true;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("accept")) {
                    try {
                        int id = Integer.parseInt(args[1]);
                        if (isAccepted(p)) {
                            p.sendMessage(PREFIX + "Du hast diesen Antrag bereits angenommen.");
                            return true;
                        }

                        if (id <= 0) {
                            p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                            return true;
                        }

                        if (!antragExists(id)) {
                            p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                            return true;
                        }

                        if(Script.getMoney(id, PaymentType.BANK) < Script.getLevel(Script.getOfflinePlayer(id)) * 1000) {
                            p.sendMessage(PREFIX + "Der Spieler hat nicht genug Geld.");
                            return true;
                        }

                        acceptApplication(id);
                        p.sendMessage(PREFIX + "Du hast den Antrag angenommen.");
                        OfflinePlayer player = getPlayerByWaffenscheinID(id);
                        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Antrag #" + id + " angenommen.");
                    } catch (Exception e) {
                        p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                        return true;
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("deny")) {
                    try {
                        int id = Integer.parseInt(args[1]);
                        if (id <= 0) {
                            p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                            return true;
                        }

                        if (!antragExists(id)) {
                            p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                            return true;
                        }

                        denyApplication(id);
                        p.sendMessage(PREFIX + "Du hast den Antrag abgelehnt.");
                        OfflinePlayer player = getPlayerByWaffenscheinID(id);
                        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Antrag #" + id + " abgelehnt.");
                    } catch (Exception e) {
                        p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                        return true;
                    }
                    return true;
                }

            }

        }

        if (Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Waffenschein.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 552, 69, 967, -253.474f, 4.7215652f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht an der Stelle zur Beantragung.");
            return true;
        }

        if(Script.getLevel(p) < 5) {
            p.sendMessage(Messages.ERROR + "Du benötigst mindestens Level 5 um einen Waffenschein zu beantragen.");
            return true;
        }

        if(!Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du benötigst einen Personalausweis.");
            return true;
        }


        if (hasApplied(p)) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Waffenschein beantragt. Bitte warte auf eine Antwort.");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast erfolgreich einen Waffenschein beantragt. Bitte warte auf eine Antwort durch die Verwaltung.");
        Script.executeAsyncUpdate("INSERT INTO waffenschein (nrp_id, date, accepted) VALUES ('" + Script.getNRPID(p) + "', '" + System.currentTimeMillis() + "', '0')");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat einen Waffenschein beantragt.");

        return false;
    }

    public static boolean hasApplied(Player p) {
        return Script.getLong(p, "waffenschein", "date") > 0;
    }

    public static void sendApplications(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE accepted='0'")) {
            if (rs.next()) {
                p.sendMessage(PREFIX + "Es gibt " + rs.getRow() + " Anträge auf einen Waffenschein.");
                do {
                    Script.sendClickableMessage(p, PREFIX + "Antrag von §6" + Script.getName(Script.getPlayer(rs.getInt("nrp_id"))) + " §8× §6" + rs.getInt("money") + "€ §8× §6" + Script.dateFormat.format(Script.getDate(rs.getLong("date"))) + " Uhr §8× §6§l#" + rs.getInt("id") + "§7.", "/waffenschein accept " + rs.getInt("id"), "§a§lAnnehmen");
                } while (rs.next());
            } else {
                p.sendMessage(PREFIX + "Es gibt derzeit keine Anträge auf einen Waffenschein.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptApplication(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id='" + id + "'")) {
            if (rs.next()) {
                Script.executeAsyncUpdate("UPDATE waffenschein SET accepted=1 WHERE id='" + id + "'");
                Licenses.WAFFENSCHEIN.grant(rs.getInt("nrp_id"));
                Player p = Script.getPlayer(rs.getInt("nrp_id"));
                Script.removeMoney(p, PaymentType.BANK, (Script.getLevel(Script.getOfflinePlayer(rs.getInt("nrp_id"))) * 1000));
                if (p != null) {
                    p.sendMessage(PREFIX + "Dein Antrag auf einen Waffenschein wurde angenommen.");
                } else {
                    Script.addOfflineMessage(rs.getInt("nrp_id"), PREFIX + "Dein Antrag auf einen Waffenschein wurde angenommen.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void denyApplication(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id='" + id + "'")) {
            if (rs.next()) {
                Script.executeAsyncUpdate("DELETE from waffenschein WHERE id='" + id + "'");
                Player p = Script.getPlayer(rs.getInt("nrp_id"));
                if (p != null) {
                    p.sendMessage(PREFIX + "Dein Antrag auf einen Waffenschein wurde abgelehnt.");
                } else {
                    Script.addOfflineMessage(rs.getInt("nrp_id"), PREFIX + "Dein Antrag auf einen Waffenschein wurde abgelehnt.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Player getPlayerByWaffenscheinID(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id=" + id)) {
            return Script.getPlayer(rs.getInt("nrp_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean antragExists(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id=" + id)) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAccepted(Player p) {
        return Script.getLong(p, "waffenschein", "accepted") == 1;
    }
}
