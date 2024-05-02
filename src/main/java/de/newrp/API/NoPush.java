package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NoPush {
    public static void addPlayer(Player p) {
        org.bukkit.scoreboard.ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard board = sm.getNewScoreboard();
        String rand = ("" + Math.random()).substring(0, 9999);
        System.out.println(rand);
        org.bukkit.scoreboard.Team team = board.registerNewTeam("no_collision" + rand);
        team.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(p.getName());
        p.setScoreboard(board);
    }

    public static void removePlayer(Player p) {
        if (p.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) return;
        if (p.getScoreboard().getTeam("no_collision") != null)
            p.getScoreboard().getTeam("no_collision").removeEntry(p.getName());
    }

    public static boolean containsPlayer(Player p) {
        return p.getScoreboard().getTeam("no_collision") != null && p.getScoreboard().getTeam("no_collision").hasEntry(p.getName());
    }
}
