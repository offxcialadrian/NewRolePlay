package de.newrp.API;

import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Team {


    public enum Teams {
        SOCIALMEDIA(1, "Social-Media-Team"),
        EVENT(2, "Event-Team"),
        BAU(3, "Bau-Team");

        int id;
        String name;

        Teams(int id, String name) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getID() {
            return id;
        }

        public static Teams getTeam(String name) {
            for (Teams t : Teams.values()) {
                if (t.getName().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        }

        public static Teams getTeam(int id) {
            for (Teams t : Teams.values()) {
                if (t.getID() == id) {
                    return t;
                }
            }
            return null;
        }

        public void addMember(Player p) {
            Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + this.getID() + ", 0)");
        }

        public void removeMember(Player p) {
            Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
        }
    }

    public static boolean isTeamLeader(Player p) {
        return Script.getBoolean(p, "teams", "leader");
    }

    public static Teams getTeam(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return Teams.getTeam(rs.getInt("team_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Teams getTeam(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return Teams.getTeam(rs.getInt("team_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeTeam(Player p) {
        Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void setTeam(Player p, Teams team, boolean leader) {
        Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + team.getID() + ", " + (leader ? 1 : 0) + ")");
    }
}
