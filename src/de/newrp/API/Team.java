package de.newrp.API;

import de.newrp.Forum.Forum;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.TeamSpeak.TeamspeakServerGroup;
import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Team {


    public enum Teams {
        SOCIALMEDIA(1, "Social-Media-Team", TeamspeakServerGroup.SOCIALMEDIA, 16, "§b"),
        EVENT(2, "Event-Team", TeamspeakServerGroup.EVENTTEAM, 18, "§7"),
        BAU(3, "Bau-Team", TeamspeakServerGroup.BAUTEAM, 17, "§e"),
        ENTWICKLUNG(4, "Entwicklungs-Team", TeamspeakServerGroup.ENTWICKLUNG, 19, "§b");

        int id;
        String name;
        TeamspeakServerGroup group;
        int channelID;
        String color;

        Teams(int id, String name, TeamspeakServerGroup group, int channelID, String color) {
            this.name = name;
            this.id = id;
            this.group = group;
            this.channelID = channelID;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getID() {
            return id;
        }

        public TeamspeakServerGroup getTeamspeakServerGroup() {
            return group;
        }

        public int getChannelID() {
            return channelID;
        }

        public String getColor() {
            return color;
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

        public ArrayList<OfflinePlayer> getAllMembers() {
            ArrayList<OfflinePlayer> members = new ArrayList<>();
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE team_id=" + this.getID())) {
                while (rs.next()) {
                    members.add(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return members;
        }

        public ArrayList<Player> getAllOnlineMembers() {
            ArrayList<Player> members = new ArrayList<>();
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE team_id=" + this.getID())) {
                while (rs.next()) {
                    members.add(Script.getPlayer(rs.getInt("nrp_id")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return members;
        }

        public void addMember(Player p) {
            Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + this.getID() + ", 0)");
            Forum.syncPermission(p);
            TeamSpeak.sync(Script.getNRPID(p));
        }

        public void removeMember(Player p) {
            setTeamLeader(p, false);
            Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
        }

        public void removeMember(OfflinePlayer p) {
            setTeamLeader(p, false);
            Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
            Forum.syncPermission(p);
            TeamSpeak.sync(Script.getNRPID(p));
        }
    }

    public static boolean isTeamLeader(Player p) {
        return Script.getInt(p, "teams", "leader") == 1;
    }

    public static boolean isTeamLeader(OfflinePlayer p) {
        return Script.getInt(p, "teams", "leader") == 1;
    }

    public static void setTeamLeader(Player p, boolean leader) {
        Script.executeUpdate("UPDATE teams SET leader=" + (leader ? 1 : 0) + " WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void setTeamLeader(OfflinePlayer p, boolean leader) {
        Script.executeUpdate("UPDATE teams SET leader=" + (leader ? 1 : 0) + " WHERE nrp_id=" + Script.getNRPID(p));
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
        setTeamLeader(p, false);
        Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void removeTeam(OfflinePlayer p) {
        setTeamLeader(p, false);
        Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void setTeam(Player p, Teams team, boolean leader) {
        Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + team.getID() + ", " + (leader ? 1 : 0) + ")");
    }
}
