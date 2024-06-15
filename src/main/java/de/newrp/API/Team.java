package de.newrp.API;

import de.newrp.Forum.Forum;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.TeamSpeak.TeamspeakServerGroup;
import de.newrp.NewRoleplayMain;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {


    public enum Teams {
        SOCIALMEDIA(1, "Marketing-Team", TeamspeakServerGroup.SOCIALMEDIA, 16, "§b", 0),
        EVENT(2, "Event-Team", TeamspeakServerGroup.EVENTTEAM, 18, "§e", 0),
        BAU(3, "Bau-Team", TeamspeakServerGroup.BAUTEAM, 17, "§e", 75),
        ENTWICKLUNG(4, "Entwicklungs-Team", TeamspeakServerGroup.ENTWICKLUNG, 19, "§b", 100);

        int id;
        String name;
        TeamspeakServerGroup group;
        int channelID;
        String color;
        List<UUID> members;
        final int salary;

        Teams(int id, String name, TeamspeakServerGroup group, int channelID, String color, int salary) {
            this.name = name;
            this.id = id;
            this.group = group;
            this.channelID = channelID;
            this.color = color;
            this.members = new ArrayList<>();
            this.salary = salary;
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

        public int getSalary() {
            return salary;
        }

        public List<UUID> getMembers() {
            return members;
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
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE team_id=" + this.getID())) {
                while (rs.next()) {
                    members.add(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                }
            } catch (SQLException e) {
                Debug.debug("SQLException -> " + e.getMessage());
                e.printStackTrace();
            }
            return members;
        }

        public void addMember(Player p) {
            this.members.add(p.getUniqueId());
            Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + this.getID() + ", 0)");
            Forum.syncPermission(p);
            TeamSpeak.sync(Script.getNRPID(p));
        }

        public void removeMember(Player p) {
            this.members.remove(p.getUniqueId());
            setTeamLeader(p, false);
            Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
        }

        public void removeMember(OfflinePlayer p) {
            this.members.remove(p.getUniqueId());
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
        for (Teams team : Teams.values()) {
            if(team.members.contains(p.getUniqueId())) {
                return team;
            }
        }

        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                final Teams team = Teams.getTeam(rs.getInt("team_id"));
                team.members.add(p.getUniqueId());
                return team;
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static Teams getTeam(OfflinePlayer p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return Teams.getTeam(rs.getInt("team_id"));
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static Teams getTeam(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE nrp_id=" + id)) {
            if (rs.next()) {
                return Teams.getTeam(rs.getInt("team_id"));
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void removeTeam(Player p) {
        final Teams teams = getTeam(p);
        if(teams != null) {
            teams.members.remove(p.getUniqueId());
        }

        setTeamLeader(p, false);
        Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void removeTeam(OfflinePlayer p) {
        setTeamLeader(p, false);
        Script.executeUpdate("DELETE FROM teams WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void setTeam(Player p, Teams team, boolean leader) {
        team.members.add(p.getUniqueId());
        Script.executeUpdate("INSERT INTO teams (nrp_id, team_id, leader) VALUES (" + Script.getNRPID(p) + ", " + team.getID() + ", " + (leader ? 1 : 0) + ")");
    }
}
