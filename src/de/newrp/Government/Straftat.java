package de.newrp.Government;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Straftat implements CommandExecutor {

    public static String PREFIX = "§8[§eFahndung§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            p.sendMessage(PREFIX + "Alle Straftaten:");
            sendAllReason(p);
            return true;
        }

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Regierung.");
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Regierung.");
            return true;
        }

        if(Beruf.getAbteilung(p) != Abteilung.Abteilungen.JUSTIZMINISTERIUM) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Justizministerium.");
            return true;
        }


        if(args.length < 2) {
            p.sendMessage(Messages.ERROR + "/straftat [WantedPunkte] [Grund]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "Die WantedPunkte müssen eine Zahl sein.");
            return true;
        }

        int wantedPunkte = Integer.parseInt(args[0]);

        String grund = "";
        for(int i = 1; i < args.length; i++) {
            grund += args[i] + " ";
        }

        grund = grund.trim();
        if(straftatExists(grund)) {
            p.sendMessage(Messages.ERROR + "Diese Straftat existiert bereits.");
            return true;
        }

        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO wanted_reason (reason, amount) VALUES ('" + grund + "', " + wantedPunkte + ")");
            p.sendMessage(PREFIX + "Der Fahndungsgrund wurde erfolgreich hinzugefügt.");
            Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat die Straftat §6" + grund + " §7mit §6" + wantedPunkte + " WantedPunkten §7hinzugefügt.");
            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Die Straftat §6" + grund + " §7mit §6" + wantedPunkte + " WantedPunkten §7wurde hinzugefügt.");
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "Die Straftat §6" + grund + " §7mit §6" + wantedPunkte + " WantedPunkten §7wurde hinzugefügt.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Debug.debug(e.getMessage());
        }


        return false;
    }

    public static void sendAllReason(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason")) {
            if (rs.next()) {
                do {
                    p.sendMessage("§8» " + (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT && Beruf.getAbteilung(p) == Abteilung.Abteilungen.JUSTIZMINISTERIUM ? "§6§l#" + rs.getInt("id") + "§8: ":"") + "§6" + rs.getString("reason") + " §8(§6" + rs.getInt("amount") + " WantedPunkte§8)");
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllReason() {
        List<String> list = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason")) {
            if (rs.next()) {
                do {
                    list.add(rs.getString("reason").replace(" ", "-"));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean straftatExists(String reason) {
        reason = reason.replace("-", " ");
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE reason = '" + reason + "'")) {
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isStraftat(int id) {
        try (Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE id=" + id)) {
            if(rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getWanteds(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE id = '" + id + "'")) {
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void delete(int id) {
        Script.executeAsyncUpdate("DELETE FROM wanted_reason WHERE id=" + id);
    }

    public static String getReason(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE id = '" + id + "'")) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getReason(String s) {
        s = s.replace("-", " ");
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE reason = '" + s + "'")) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getReasonID(String s) {
        s = s.replace("-", " ");
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted_reason WHERE reason = '" + s + "'")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
