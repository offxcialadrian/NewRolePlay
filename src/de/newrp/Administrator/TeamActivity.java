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

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/teamactivity");
            return true;
        }

        //print activity
        HashMap<String, Integer> activity = getActitvity();
        p.sendMessage("§8[§c§lTeamActivity§8] §c»");
        for(String name : activity.keySet()) {
            p.sendMessage("§8- §c" + name + " §8» §c" + activity.get(name) + " Tickets (" + (Script.getPercentage(activity.get(name), getTotalTicket()) + "%)"));
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

        //sort by value
        activity = Script.sortByValue(activity);
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

}
