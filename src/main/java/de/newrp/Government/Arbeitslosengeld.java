package de.newrp.Government;

import de.newrp.API.*;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.data.UnemploymentBenefitData;
import de.newrp.Player.Banken;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arbeitslosengeld implements CommandExecutor {

    public static String PREFIX = "§8[§eArbeitslosengeld§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (Beruf.getAbteilung(p, true) == Abteilung.Abteilungen.FINANZAMT || Beruf.isLeader(p, true)) {
                if (args.length == 0) {
                    p.sendMessage(PREFIX + "Es gibt " + getArbeitslosengeldApplicationAmount() + " Anträge.");
                    sendApplications(p);
                    return true;

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

                            if (id <= 0) {
                                p.sendMessage(PREFIX + "Bitte gib eine gültige ID an.");
                                return true;
                            }

                            if (!arbeitslosengeldExists(id)) {
                                p.sendMessage(PREFIX + "Dieser Antrag existiert nicht.");
                                return true;
                            }

                            OfflinePlayer offlinePlayer = getPlayerByArbeitslosengeldID(id);
                            if (isAccepted(offlinePlayer)) {
                                p.sendMessage(PREFIX + "Dieser Antrag wurde bereits angenommen.");
                                return true;
                            }

                            acceptApplication(id);
                            p.sendMessage(PREFIX + "Du hast den Antrag angenommen.");
                            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Antrag #" + id + " angenommen.");
                            Activity.grantActivity(Script.getNRPID(p), Activities.ARBEITSLOSENGELD);
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
                        if (!Beruf.isLeader(p, true)) {
                            p.sendMessage(Messages.ERROR + "Nur der Bundeskanzler kann die Höhe des Arbeitslosengeldes ändern.");
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

        }

        if (hasArbeitslosengeld(p)) {
            long time = Script.getLong(p, "arbeitslosengeld", "until") - System.currentTimeMillis();
            int days = (int) (time / 1000 / 60 / 60 / 24);
            int hours = (int) (time / 1000 / 60 / 60 % 24);
            p.sendMessage(PREFIX + "Dein Arbeitslosengeld läuft in " + days + " Tagen und " + hours + " Stunden ab.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 552, 70, 966, 266.23996f, 65.03321f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht am Arbeitsamt.");
            return true;
        }

        if (!Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du benötigst einen Personalausweis.");
            return true;
        }

        if (!Banken.hasBank(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst ein Bankkonto.");
            return true;
        }

        if (Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast einen Beruf.");
            return true;
        }

        if (hasApplied(p) && !isAccepted(p)) {
            p.sendMessage(Messages.ERROR + "Du hast bereits Arbeitslosengeld beantragt. Bitte warte auf eine Antwort.");
            return true;
        }

        if (hasApplied(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst nur alle zwei Wochen Arbeitslosengeld beantragen.");
            return true;
        }

        if(Script.getLevel(p) <= 3) {
            Script.executeAsyncUpdate("INSERT INTO arbeitslosengeld (nrp_id, money, date, accepted, until) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getMoney(p, PaymentType.BANK) + "', '" + System.currentTimeMillis() + "', '1', " + (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7) + ")");
            p.sendMessage(PREFIX + "Du hast erfolgreich Arbeitslosengeld genommen");
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat Arbeitslosengeld genommen (Spieler <= Level 3)");
            return false;
        }
        p.sendMessage(PREFIX + "Du hast erfolgreich Arbeitslosengeld beantragt. Bitte warte auf eine Antwort durch die Verwaltung.");
        Script.executeAsyncUpdate("INSERT INTO arbeitslosengeld (nrp_id, money, date, accepted) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getMoney(p, PaymentType.BANK) + "', '" + System.currentTimeMillis() + "', '0')");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat Arbeitslosengeld beantragt.");

        return false;
    }

    public static boolean hasArbeitslosengeld(Player p) {
        return Script.getLong(p, "arbeitslosengeld", "until") > System.currentTimeMillis();
    }

    public static boolean hasArbeitslosengeld(OfflinePlayer p) {
        return Script.getLong(p, "arbeitslosengeld", "until") > System.currentTimeMillis();
    }

    public static boolean hasApplied(Player p) {
        return Script.getLong(p, "arbeitslosengeld", "date") > 0;
    }

    public static boolean isAccepted(Player p) {
        return Script.getInt(p, "arbeitslosengeld", "accepted") == 1;
    }

    public static boolean isAccepted(OfflinePlayer p) {
        return Script.getInt(p, "arbeitslosengeld", "accepted") == 1;
    }

    public static void sendApplications(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE accepted='0'")) {
            while (rs.next()) {

                Script.sendClickableMessage(p, PREFIX + "Antrag von §6" + Script.getNameInDB(rs.getInt("nrp_id")) + " §8× §6" + rs.getInt("money") + "€ §8× §6" + Script.dateFormat.format(Script.getDate(rs.getLong("date"))) + " Uhr §8× §6§l#" + rs.getInt("id") + "§7.", "/arbeitslosengeld accept " + rs.getInt("id"), "§a§lAnnehmen");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getArbeitslosengeldApplicationAmount() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE accepted='0'")) {
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


    public static void acceptApplication(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM arbeitslosengeld WHERE id=" + id)) {
            if(rs.next()) {
                return Script.getPlayer(rs.getInt("nrp_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getNRPIDByArbeitslosengeldID(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nrp_id FROM arbeitslosengeld WHERE id=" + id)) {
            return rs.getInt("nrp_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean arbeitslosengeldExists(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM arbeitslosengeld WHERE id=" + id)) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteArbeitslosengeld(Player p) {
        if (hasArbeitslosengeld(p)) {
            Script.executeAsyncUpdate("DELETE FROM arbeitslosengeld WHERE nrp_id='" + Script.getNRPID(p) + "'");
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + "s Arbeitslosengeld wurde gekündigt.");
        }
    }

    public static void deleteArbeitslosengeld(OfflinePlayer p) {
        if (hasArbeitslosengeld(p)) {
            Script.executeAsyncUpdate("DELETE FROM arbeitslosengeld WHERE nrp_id='" + Script.getNRPID(p) + "'");
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + p.getName() + "s Arbeitslosengeld wurde gekündigt.");
        }
    }

    public static List<UnemploymentBenefitData> getAllActiveUnemploymentBenefits() {
        final List<UnemploymentBenefitData> list = new ArrayList<>();
        try (final Statement statement = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = statement.executeQuery("SELECT alg.id, alg.nrp_id, nid.uuid, nid.name FROM arbeitslosengeld alg LEFT JOIN nrp_id nid ON nid.id=alg.nrp_id;")) {
            while(rs.next()) {
                list.add(new UnemploymentBenefitData(rs.getInt(1), UUID.fromString(rs.getString(3)), rs.getString(4), rs.getInt(2)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
