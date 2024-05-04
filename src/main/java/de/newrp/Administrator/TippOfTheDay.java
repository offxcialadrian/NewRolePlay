package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.newrp.NewRoleplayMain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TippOfTheDay implements CommandExecutor {

    public static String PREFIX = "§8[§eTipp§8] §e" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.MODERATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/tipp [Tipp]");
            return true;
        }

        if(args.length >= 2 && args[0].equalsIgnoreCase("remove")) {
            String tipp = "";
            for(int i = 1; i < args.length; i++) {
                tipp += args[i] + " ";
            }
            if(!exists(tipp)) {
                p.sendMessage(Messages.ERROR + "Der Tipp existiert nicht.");
                return true;
            }
            remove(tipp);
            p.sendMessage(PREFIX + "Du hast den Tipp §6" + tipp + " §7entfernt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den Tipp §6" + tipp + " §eentfernt.", true);
            return true;
        }

        if(args.length >= 2 && args[0].equalsIgnoreCase("delete")) {
            String tipp = "";
            for(int i = 1; i < args.length; i++) {
                tipp += args[i] + " ";
            }
            if(!exists(tipp)) {
                p.sendMessage(Messages.ERROR + "Der Tipp existiert nicht.");
                return true;
            }
            remove(tipp);
            p.sendMessage(PREFIX + "Du hast den Tipp §6" + tipp + " §7entfernt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den Tipp §6" + tipp + " §eentfernt.", true);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            assert getIDs() != null;
            if(getIDs().isEmpty()) {
                p.sendMessage(Messages.ERROR + "Es gibt keine Tipps.");
                return true;
            }
            p.sendMessage(PREFIX + "§8=== §6Tipp Übersicht §8===");
            for(int i = 0; i < getIDs().size(); i++) {
                p.sendMessage(PREFIX + "§7- §6" + getTipp(getIDs().get(i)));
            }
            p.sendMessage(PREFIX + "§8=========");
            return true;
        }

        String tipp = "";
        for(int i = 0; i < args.length; i++) {
            tipp += args[i] + " ";
        }

        if(exists(tipp)) {
            p.sendMessage(Messages.ERROR + "Der Tipp existiert bereits.");
            return true;
        }

        add(tipp);
        p.sendMessage(PREFIX + "Du hast den Tipp §6" + tipp + " §7hinzugefügt.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den Tipp §6" + tipp + " §ehinzugefügt.", true);


        return false;
    }

    public static boolean exists(String tipp) {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM tippoftheday WHERE tipp = '" + tipp + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void add(String tipp) {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement()) {
            statement.executeUpdate("INSERT INTO tippoftheday (tipp) VALUES ('" + tipp + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void remove(String tipp) {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement()) {
            statement.executeUpdate("DELETE FROM tippoftheday WHERE tipp = '" + tipp + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getIDs() {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT id FROM tippoftheday");
            List<Integer> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(rs.getInt("id"));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTipp(int id) {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM tippoftheday WHERE id = '" + id + "'");
            if(rs.next()) {
                return rs.getString("tipp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRandomID() {
        List<Integer> ids = getIDs();
        return ids.get((int) (Math.random() * ids.size()));
    }

    public static String getRandomTipp() {
        return getTipp(getRandomID());
    }

}
