package de.newrp.Administrator;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class TeamActivity implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 1) {
            try {
                int days = Integer.parseInt(args[0]);
                if(days < 1) {
                    p.sendMessage(Messages.ERROR + "Die Anzahl der Tage muss mindestens 1 sein.");
                    return true;
                }

                HashMap<String, Integer> activity = getActitvity(days);
                p.sendMessage("§8[§c§lTeamActivity§8] §c»");
                for(String name : activity.keySet()) {
                    p.sendMessage("§8- §c" + name + " §8» §c" + activity.get(name) + " Tickets (" + (Script.getPercentage(activity.get(name), getTotalTicket(days)) + "%)" + " §8» §c" + getRating(days, Script.getNRPID(Script.getPlayer(name))) + " Sterne"));
                }
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Ungültige Anzahl an Tagen.");
            }
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/teamactivity");
            return true;
        }

        HashMap<String, Integer> activity = getActitvity();
        p.sendMessage("§8[§c§lTeamActivity§8] §c»");
        for(String name : activity.keySet()) {
            p.sendMessage("§8- §c" + name + " §8» §c" + activity.get(name) + " Tickets (" + (Script.getPercentage(activity.get(name), getTotalTicket()) + "%) " + "§8» §c" + getRating(Script.getNRPID(Script.getPlayer(name))) + " Sterne"));
        }

        return false;
    }

    public static HashMap<String, Integer> getActitvity() {
        HashMap<String, Integer> activity = new HashMap<>();
        for(OfflinePlayer op : Script.getAllNRPTeam()) {
            try (Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM ticket WHERE supporterID=" + Script.getNRPID(op))) {
                if(rs.next()) {
                    activity.put(Script.getNameInDB(op), rs.getInt("total"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //sort by value but
        activity = Script.sortByValue(activity, true);
        return activity;
    }

    public static double getRating(int nrpid) {
        double rating = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AVG(rating) AS total FROM supporter_rating WHERE rating > 0 AND supporterID=" + nrpid)) {
            if (rs.next()) {
                rating = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rating;
    }

    public static double getRating(int days, int nrpid) {
        double rating = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AVG(rating) AS supporter_rating FROM supporter_rating WHERE rating > 0 AND time > " + (System.currentTimeMillis() - ((long) days * 24 * 60 * 60 * 1000)) + " AND supporterID=" + nrpid) ) {
            if (rs.next()) {
                rating = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rating;
    }

    public static HashMap<String, Integer> getActitvity(int days) {
        HashMap<String, Integer> activity = new HashMap<>();
        for(OfflinePlayer op : Script.getAllNRPTeam()) {
            try (Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM ticket WHERE supporterID=" + Script.getNRPID(op) + " AND closed > " + (System.currentTimeMillis() - ((long) days * 24 * 60 * 60 * 1000)))) {
                if(rs.next()) {
                    activity.put(Script.getNameInDB(op), rs.getInt("total"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //sort by value
        activity = Script.sortByValue(activity, true);
        return activity;
    }

    public static int getTotalTicket() {
        int total = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM ticket")) {
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static int getTotalTicket(int days) {
        int total = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM ticket WHERE closed > " + (System.currentTimeMillis() - ((long) days * 24 * 60 * 60 * 1000)))) {
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

}
