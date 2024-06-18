package de.newrp.Government;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
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
                if (Beruf.getAbteilung(p) == Abteilung.Abteilungen.JUSTIZMINISTERIUM || Beruf.isLeader(p, true)) {
                    if (getApplicationAmount() == 0) {
                        p.sendMessage(PREFIX + "Es gibt keine Anträge.");
                        return true;
                    }

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
                        OfflinePlayer tg =  getPlayerByWaffenscheinID(id);

                        if (id <= 0) {
                            p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                            return true;
                        }

                        if (!antragExists(id)) {
                            p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                            return true;
                        }

                        if (Script.getMoney(tg, PaymentType.BANK) < Math.min(10000, (Script.getLevel(tg) * 500) + 3000)) {
                            p.sendMessage(PREFIX + "Der Spieler hat nur " + Script.getMoney(tg, PaymentType.BANK) + "€ von " + Math.min(10000, (Script.getLevel(tg) * 500) + 3000) + "€.");
                            return true;
                        }


                        if (isAccepted(tg)) {
                            p.sendMessage(PREFIX + "Du hast diesen Antrag bereits angenommen.");
                            return true;
                        }

                        acceptApplication(id);
                        p.sendMessage(PREFIX + "Du hast den Antrag angenommen.");
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

        if (Licenses.WAFFENSCHEIN.isLocked(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Dein Waffenschein wurde gesperrt.");
            return true;
        }

        if (Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Waffenschein.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 552, 69, 967, -253.474f, 4.7215652f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht an der Stelle zur Beantragung.");
            return true;
        }

        if (Script.getLevel(p) < 3) {
            p.sendMessage(Messages.ERROR + "Du benötigst mindestens Level 3 um einen Waffenschein zu beantragen.");
            return true;
        }

        if (!Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du benötigst einen Personalausweis.");
            return true;
        }

        if (hasApplied(p)) {
            Script.executeUpdate("DELETE FROM waffenschein WHERE nrp_id=" + Script.getNRPID(p));
            p.sendMessage(PREFIX + "Du hast deinen Antrag zurückgezogen.");
            return true;
        }

        if (Script.getMoney(p, PaymentType.BANK) < Math.min(10000, (Script.getLevel(p) * 500) + 3000)) {
            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
            p.sendMessage(Messages.INFO + "Du benötigst " + Math.min(10000, (Script.getLevel(p) * 500) + 3000) + "€.");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast erfolgreich einen Waffenschein beantragt. Bitte warte auf eine Antwort durch die Verwaltung.");
        p.sendMessage(Messages.INFO + "Die Ausstellung des Waffenscheins kostet dich " + Math.min(10000, (Script.getLevel(p) * 500) + 3000) + "€. Falls du nicht bereit bist, so viel zu zahlen, kannst du den Antrag zurückziehen.");
        Script.executeAsyncUpdate("INSERT INTO waffenschein (nrp_id, date, accepted) VALUES ('" + Script.getNRPID(p) + "', '" + System.currentTimeMillis() + "', '0')");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat einen Waffenschein beantragt.");

        return false;
    }

    public static boolean hasApplied(Player p) {
        return Script.getLong(p, "waffenschein", "date") > 0;
    }

    public static int getApplicationAmount() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE accepted='0'")) {
            int i = 0;
            while (rs.next()) {
                i++;
            }
            return i;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void sendApplications(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE accepted='0'")) {
            p.sendMessage(PREFIX + "Anträge gefunden:");
            while (rs.next()) {
                Script.sendClickableMessage(p, PREFIX + "Antrag von §6" + Script.getOfflinePlayer(rs.getInt("nrp_id")).getName() + " §8× §6" + Script.dateFormat.format(rs.getLong("date")) + " Uhr §8× §6§l#" + rs.getInt("id") + "§7.", "/waffenschein accept " + rs.getInt("id"), "§a§lAnnehmen");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void acceptApplication(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id='" + id + "'")) {
            if (rs.next()) {
                Script.executeAsyncUpdate("UPDATE waffenschein SET accepted=1 WHERE id='" + id + "'");
                Licenses.WAFFENSCHEIN.grant(rs.getInt("nrp_id"));
                OfflinePlayer p = Script.getOfflinePlayer(rs.getInt("nrp_id"));
                Script.removeMoney(p, PaymentType.BANK, Math.min(10000, (Script.getLevel(Script.getOfflinePlayer(rs.getInt("nrp_id"))) * 500) + 3000));
                Stadtkasse.addStadtkasse(Math.min(10000, (Script.getLevel(Script.getOfflinePlayer(rs.getInt("nrp_id"))) * 500) + 3000), "Waffenschein von " + p.getName() + ".", null);
                if (p.getPlayer() != null) {
                    p.getPlayer().sendMessage(PREFIX + "Dein Antrag auf einen Waffenschein wurde angenommen.");
                } else {
                    Script.addOfflineMessage(rs.getInt("nrp_id"), PREFIX + "Dein Antrag auf einen Waffenschein wurde angenommen.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void denyApplication(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id=" + id)) {
            return Script.getPlayer(rs.getInt("nrp_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean antragExists(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM waffenschein WHERE id=" + id)) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAccepted(OfflinePlayer p) {
        return Script.getInt(p, "waffenschein", "accepted") == 1;
    }
}
