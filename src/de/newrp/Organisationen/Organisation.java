package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.Forum.ForumGroup;
import de.newrp.Government.Arbeitslosengeld;
import de.newrp.TeamSpeak.TeamspeakServerGroup;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public enum Organisation {

    GOVERNMENT(1, "Bloods", 2, false, false, false, 0, null, null);

    private final String name;
    int id;
    int level;
    boolean kasse;
    boolean duty;
    boolean equip;
    int channelid;
    TeamspeakServerGroup serverGroup;
    ForumGroup[] forumGroup;

    Organisation(int id, String name, int level, boolean kasse, boolean duty, boolean equip, int channelid, TeamspeakServerGroup serverGroup, ForumGroup[] forumGroup) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.kasse = kasse;
        this.duty = duty;
        this.equip = equip;
        this.channelid = channelid;
        this.serverGroup = serverGroup;
        this.forumGroup = forumGroup;
    }

    public int getLevel() {
        return level;
    }

    public static Organisation getOrganisation(String name) {
        for (Organisation organisation : Organisation.values()) {
            if (organisation.getName().equalsIgnoreCase(name)) {
                return organisation;
            }
        }
        return null;
    }

    public static Organisation getOrganisation(int id) {
        for (Organisation organisation : Organisation.values()) {
            if (organisation.getID() == id) {
                return organisation;
            }
        }
        return null;
    }

    public static Organisation getOrganisation(Player p) {
        return Organisation.getOrganisation(Script.getInt(p, "organisation", "organisationID"));
    }

    public static Organisation getOrganisation(OfflinePlayer p) {
        return Organisation.getOrganisation(Script.getInt(p, "organisation", "organisationID"));
    }

    public static boolean hasOrganisation(Player p) {
        return Script.getInt(p, "organisation", "organisationID") != 0;
    }

    public static boolean hasOrganisation(OfflinePlayer p) {
        return Script.getInt(p, "organisation", "organisationID") != 0;
    }

    public static boolean hasOrganisation(Player p, Organisation organisation) {
        return Script.getInt(p, "organisation", "organisationID") == organisation.getID();
    }

    public static boolean isCoLeader(Player p) {
        return Script.getInt(p, "organisation", "coleader") == 1;
    }

    public static int getSalary(OfflinePlayer p) {
        return Script.getInt(p, "organisation", "salary");
    }

    public static int getSalary(Player p) {
        return Script.getInt(p, "organisation", "salary");
    }

    public static void setLeader(OfflinePlayer p, boolean main) {
        Script.setInt(p, "organisation", "leader", main ? 1 : 0);
        Script.setInt(p, "organisation", "coleader", main ? 0 : 1);
    }

    public static void removeLeader(OfflinePlayer p) {
        Script.setInt(p, "organisation", "leader", 0);
        Script.setInt(p, "organisation", "coleader", 0);
    }

    /*public static boolean isLeader(Player p, boolean coleader) {
        if (Script.getInt(p, "organisation", "leader") == 1) return true;
        if (coleader) {
            return Script.getInt(p, "organisation", "coleader") == 1;
        }
        return false;
    }*/

    public static boolean isLeader(OfflinePlayer p, boolean coleader) {
        if (Script.getInt(p, "organisation", "leader") == 1) return true;
        if (coleader) {
            return Script.getInt(p, "organisation", "coleader") == 1;
        }
        return false;
    }

    public boolean hasBlacklist() {
        return getLevel() >= 2;
    }

    public static List<Player> getPlayersFromOrganisation(Organisation organisation) {
        List<Player> list = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasOrganisation(all, organisation)) {
                list.add(all);
            }
        }
        return list;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasEquip() {
        return equip;
    }

    public int getChannelID() {
        return channelid;
    }

    public boolean hasDuty() {
        return duty;
    }

    public TeamspeakServerGroup getTeamspeakServerGroup() {
        return serverGroup;
    }

    public ForumGroup getForumGroup(boolean leader) {
        return leader ? this.forumGroup[1] : this.forumGroup[0];
    }

    public boolean hasKasse() {
        return kasse;
    }

    public ArrayList<Location> getDoors() {
        ArrayList<Location> locs = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT organisationID, x, y, z FROM Organisationsdoor WHERE organisationID=" + this.id)) {
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                Location loc = new Location(Script.WORLD, x, y, z);
                if (!locs.contains(loc)) locs.add(loc);
            }
            return locs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locs;
    }

    public int getKasse() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_kasse WHERE organisationID='" + this.id + "'")) {
            if (rs.next()) {
                return rs.getInt("kasse");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setKasse(int amount) {
        Script.executeUpdate("UPDATE organisation_kasse SET kasse='" + amount + "' WHERE organisationID='" + this.id + "'");
    }

    public void addKasse(int amount) {
        Script.executeUpdate("UPDATE organisation_kasse SET kasse='" + (getKasse() + amount) + "' WHERE organisationID='" + this.id + "'");
    }

    public void removeKasse(int amount) {
        Script.executeUpdate("UPDATE organisation_kasse SET kasse='" + (getKasse() - amount) + "' WHERE organisationID='" + this.id + "'");
    }

    public static boolean isLeader(Player p, boolean coleader) {
        if (Script.getInt(p, "organisation", "leader") == 1) return true;
        if (coleader) {
            return Script.getInt(p, "organisation", "coleader") == 1;
        }
        return false;
    }

    public boolean isLeader(int id, boolean coleader) {
        OfflinePlayer p = Script.getOfflinePlayer(id);
        if (Script.getInt(p, "organisation", "leader") == 1) return true;
        if (coleader) {
            return Script.getInt(p, "organisation", "coleader") == 1;
        }
        return false;
    }

    public void sendMessage(String message) {
        for (Player all : getMembers()) {
            all.sendMessage(message);
        }
    }

    public void sendLeaderMessage(String message) {
        for (Player all : getLeaders()) {
            all.sendMessage(message);
        }
    }

    public List<Player> getMembers() {
        List<Player> list = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasOrganisation(all, this)) {
                list.add(all);
            }
        }
        return list;
    }

    public List<OfflinePlayer> getAllMembers() {
        List<OfflinePlayer> list = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM organisation WHERE organisationID='" + this.id + "' ORDER BY rank ASC")) {
            if (rs.next()) {
                do {
                    list.add(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Player> getLeaders() {
        List<Player> list = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasOrganisation(all, this)) {
                if (isLeader(all, true)) {
                    list.add(all);
                }
            }
        }
        return list;
    }

    public static int getRank(Player p) {
        return Script.getInt(p, "organisation", "rank");
    }

    public void addMember(Player p, Player leader) {
        Script.executeUpdate("INSERT INTO organisation (nrp_id, organisationID, salary, rank, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §eist dem Organisation beigetreten.");
        }
        sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " in den Organisation eingeladen.");
        Script.sendTeamMessage("§8[§eOrganisationControl§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " in die Organisation " + getName() + " eingeladen.");
    }

    public void addMember(OfflinePlayer p) {
        Script.executeUpdate("INSERT INTO organisation (nrp_id, OrganisationID, salary, rank, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §eist dem Organisation beigetreten.");
        }
        sendLeaderMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " ist nun Teil des Organisations.");
    }

    public void removeMember(Player p, Player leader) {
        if (Duty.isInDuty(p)) Duty.removeDuty(p);
        Equip.removeEquip(p);
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat die Organisation verlassen.");
        }
        sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " aus der Organisation geworfen.");
        Script.sendTeamMessage("§8[§eOrganisationsControl§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " aus der Organisation " + getName() + " geworfen.");
    }

    public void removeMember(OfflinePlayer p, Player leader) {
        if (p.getPlayer() != null) {
            if (Duty.isInDuty(p.getPlayer())) Duty.removeDuty(p.getPlayer());
            Equip.removeEquip(p.getPlayer());
        }
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat die Organisation verlassen.");
        }
        sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + p.getName() + " aus die Organisation geworfen.");
        Script.sendTeamMessage("§8[§eOrganisationsControl§8] §e" + Script.getName(leader) + " §ehat " + p.getName() + " aus die Organisation " + getName() + " geworfen.");
        Script.addOfflineMessage(p, "§8[§eOrganisation§8] §e" + Messages.ARROW + " Du wurdest aus deiner Organisation geworfen.");
    }

    public void removeMember(OfflinePlayer p) {
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat die Organisation verlassen.");
        }
    }
}
