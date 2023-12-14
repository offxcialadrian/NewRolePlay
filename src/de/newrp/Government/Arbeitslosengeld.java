package de.newrp.Government;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;

public class Arbeitslosengeld implements CommandExecutor {

    public static String PREFIX = "§8[§eArbeitslosengeld§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (args.length == 0) {
                if(Beruf.getAbteilung(p) == Abteilung.Abteilungen.ARBEITSAMT) {
                    sendApplications(p);
                    p.sendMessage(Messages.ERROR + "Du bist nicht im Arbeitsamt.");
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

                        if (!arbeitslosengeldExists(id)) {
                            p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                            return true;
                        }

                        acceptApplication(id);
                        p.sendMessage(PREFIX + "Du hast den Antrag angenommen.");
                        OfflinePlayer player = getPlayerByArbeitslosengeldID(id);
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

                        if (!arbeitslosengeldExists(id)) {
                            p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                            return true;
                        }

                        denyApplication(id);
                        p.sendMessage(PREFIX + "Du hast den Antrag abgelehnt.");
                        OfflinePlayer player = getPlayerByArbeitslosengeldID(id);
                        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Antrag #" + id + " abgelehnt.");
                    } catch (Exception e) {
                        p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                        return true;
                    }
                    return true;
                }

                if (args[0].equals("set")) {
                    if (!Beruf.isLeader(p)) {
                        p.sendMessage(Messages.ERROR + "Nur das Staatsoberhaupt kann die Höhe des Arbeitslosengeldes ändern.");
                        return true;
                    }

                    try {
                        int money = Integer.parseInt(args[1]);
                        if (money < 0) {
                            p.sendMessage(Messages.ERROR + "Bitte gib eine gültige Zahl an.");
                            return true;
                        }
                        Script.executeAsyncUpdate("UPDATE city SET arbeitslosengeld='" + money + "'");
                        p.sendMessage(PREFIX + "Du hast das Arbeitslosengeld auf §6" + money + "€ §egesetzt.");
                        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat das Arbeitslosengeld auf §6" + money + "€ §egesetzt.");
                        Bukkit.broadcastMessage("§8[§6NEWS§8] §6" + Messages.ARROW + " Die Regierung hat beschlossen das Arbeitslosengeld auf §6" + money + "€ §6zu setzen.");
                        Log.WARNING.write(p, "hat das Arbeitslosengeld auf " + money + "€ gesetzt.");
                        return true;
                    } catch (Exception e) {
                        p.sendMessage(Messages.ERROR + "Bitte gib eine gültige Zahl an.");
                        return true;
                    }
                }

            }

        }

        if (hasArbeitslosengeld(p)) {
            long time = Script.getLong(p, "arbeitslosengeld", "until") - System.currentTimeMillis();
            int days = (int) (time / 1000 / 60 / 60 / 24);
            int hours = (int) (time / 1000 / 60 / 60 % 24);
            p.sendMessage(PREFIX + "Dein Arbeitslosengeld läuft in " + days + " Tagen und " + hours + " Stunden ab.");
            return true;
        }

        if (Beruf.hasBeruf(p)) {
            p.sendMessage(PREFIX + "Du hast einen Beruf.");
            return true;
        }

        if (hasApplied(p)) {
            p.sendMessage(PREFIX + "Du hast bereits Arbeitslosengeld beantragt. Bitte warte auf eine Antwort.");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast erfolgreich Arbeitslosengeld beantragt. Bitte warte auf eine Antwort durch die Verwaltung.");
        Script.executeAsyncUpdate("INSERT INTO arbeitslosengeld (nrp_id, money, date, accepted) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getMoney(p, PaymentType.BANK) + "', '" + System.currentTimeMillis() + "', '0')");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat Arbeitslosengeld beantragt.");

        return false;
    }

    public static boolean hasArbeitslosengeld(Player p) {
        return Script.getLong(p, "arbeitslosengeld", "until") > System.currentTimeMillis();
    }

    public static boolean hasApplied(Player p) {
        return Script.getLong(p, "arbeitslosengeld", "date") > 0;
    }

    public static boolean isAccepted(Player p) {
        return Script.getInt(p, "arbeitslosengeld", "accepted") == 1;
    }

    public static void sendApplications(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE accepted='0'")) {
            if (rs.next()) {
                p.sendMessage(PREFIX + "Es gibt " + rs.getRow() + " Anträge auf Arbeitslosengeld.");
                do {
                    Script.sendClickableMessage(p, PREFIX + "Antrag von §6" + Script.getName(Script.getPlayer(rs.getInt("nrp_id"))) + " §8× §6" + rs.getInt("money") + "€ §8× §6" + Script.dateFormat.format(Script.getDate(rs.getLong("date"))) + " Uhr §8× §6§l#" + rs.getInt("id") + "§7.", "/arbeitslosengeld accept " + rs.getInt("id"), "§a§lAnnehmen");
                } while (rs.next());
            } else {
                p.sendMessage(PREFIX + "Es gibt derzeit keine Anträge auf Arbeitslosengeld.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptApplication(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id='" + id + "'")) {
            if (rs.next()) {
                Script.executeAsyncUpdate("UPDATE arbeitslosengeld SET accepted=1 WHERE id='" + id + "'");
                Script.executeAsyncUpdate("UPDATE arbeitslosengeld SET until='" + (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7) + "' WHERE id='" + id + "'");
                Player p = Script.getPlayer(rs.getInt("nrp_id"));
                if (p != null) {
                    p.sendMessage(PREFIX + "Dein Antrag auf Arbeitslosengeld wurde angenommen.");
                } else {
                    Script.addOfflineMessage(rs.getInt("nrp_id"), PREFIX + "Dein Antrag auf Arbeitslosengeld wurde angenommen.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void denyApplication(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id='" + id + "'")) {
            if (rs.next()) {
                Script.executeAsyncUpdate("DELETE from arbeitslosengeld WHERE id='" + id + "'");
                Player p = Script.getPlayer(rs.getInt("nrp_id"));
                if (p != null) {
                    p.sendMessage(PREFIX + "Dein Antrag auf Arbeitslosengeld wurde abgelehnt.");
                } else {
                    Script.addOfflineMessage(rs.getInt("nrp_id"), PREFIX + "Dein Antrag auf Arbeitslosengeld wurde abgelehnt.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Player getPlayerByArbeitslosengeldID(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id=" + id)) {
            return Script.getPlayer(rs.getInt("nrp_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getNRPIDByArbeitslosengeldID(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id=" + id)) {
            return rs.getInt("nrp_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean arbeitslosengeldExists(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id=" + id)) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteArbeitslosengeld(Player p) {
        Script.executeAsyncUpdate("DELETE FROM arbeitslosengeld WHERE nrp_id='" + Script.getNRPID(p) + "'");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + "s Arbeitslosengeld wurde gekündigt.");
    }


}
