package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.newrp.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Vote {

    public static final int VOTE_AMOUNT_WEEK = 250;
    public static final int VOTE_AMOUNT_WEEKEND = 350;

    public static int getVotesToday(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(id) AS total FROM vote_cache WHERE id=" + id)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //TODO Auf richtigen Cache ändern (Default läd dann diese funktion)
    public static boolean hasVotedToday(int id) {
        return getVotesToday(id) > 0;
    }

    public static void resetVoteCache() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Script.executeUpdate("DELETE FROM vote_cache WHERE day!=" + day + " ALTER TABLE vote_cache AUTO_INCREMENT = 1");
    }

    public static void startVoteRamble() {
        List<Player> votedPlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            int unicaID = Script.getNRPID(p);
            if (!hasVotedToday(unicaID)) continue;
            votedPlayers.add(p);
        }
        Player p = votedPlayers.get(ThreadLocalRandom.current().nextInt(votedPlayers.size()));
        int r = Script.getRandom(1, 2);
        switch (r) {
            case 1:
                Bukkit.broadcastMessage("§8[§6Event§8]§6 " + Script.getName(p) + " hat 1 Tag Premium gewonnen!");
                Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(1), true);
                break;
            case 2:
                Bukkit.broadcastMessage("§8[§6Event§8]§6 " + Script.getName(p) + " hat 3 Tage Premium gewonnen!");
                Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(3), true);
                break;
        }
    }
}