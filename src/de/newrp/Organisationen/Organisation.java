package de.newrp.Organisationen;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.Forum.ForumGroup;
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

    FALCONE(1, "Falcone-Famiglia",  true, false, false, 158, new Location(Script.WORLD, 761, 119, 847, 258.50473f, 17.865908f), TeamspeakServerGroup.FALCONE,  new ForumGroup[]{ForumGroup.FALCONE, ForumGroup.FALCONE_LEADER}, OrgSpray.FraktionSpray.FALCONE),
    KARTELL(2, "puertoricanisches-Kartell",  true, false, false, 119, new Location(Script.WORLD, 230, 68, 1110, 1.5193672f, 16.320272f), TeamspeakServerGroup.KARTELL,  new ForumGroup[]{ForumGroup.KARTELL, ForumGroup.KARTELL_LEADER}, OrgSpray.FraktionSpray.KARTELL),
    BRATERSTWO(3, "Braterstwo",  true, false, false, 143, new Location(Script.WORLD, 556, 77, 1268, -263.88275f, 10.649914f), TeamspeakServerGroup.BRATERSTWO,  new ForumGroup[]{ForumGroup.BRATERSTWO, ForumGroup.BRATERSTWO_LEADER}, OrgSpray.FraktionSpray.BRATERSTWO),
    CORLEONE(4, "Corleone-Familie",  true, false, false, 131, new Location(Script.WORLD, 183, 104, 479, -268.55347f, 14.808363f), TeamspeakServerGroup.CORLEONE,  new ForumGroup[]{ForumGroup.CORLEONE, ForumGroup.CORLEONE_LEADER}, OrgSpray.FraktionSpray.CORLEONE),
    GROVE(5, "Grove-Street",  true, false, false, 170, new Location(Script.WORLD, 752, 54, 1266, 193.52417f, 13.093105f), TeamspeakServerGroup.GROVE,  new ForumGroup[]{ForumGroup.GROVE, ForumGroup.GROVE_LEADER}, OrgSpray.FraktionSpray.GROVE);

    private final String name;
    int id;
    boolean kasse;
    boolean duty;
    boolean equip;
    int channelid;
    Location dbank;
    TeamspeakServerGroup serverGroup;
    ForumGroup[] forumGroup;
    OrgSpray.FraktionSpray fraktionSpray;

    Organisation(int id, String name, boolean kasse, boolean duty, boolean equip, int channelid, Location dbank, TeamspeakServerGroup serverGroup, ForumGroup[] forumGroup, OrgSpray.FraktionSpray fraktionSpray) {
        this.id = id;
        this.name = name;
        this.kasse = kasse;
        this.duty = duty;
        this.equip = equip;
        this.channelid = channelid;
        this.dbank = dbank;
        this.serverGroup = serverGroup;
        this.forumGroup = forumGroup;
        this.fraktionSpray = fraktionSpray;
    }

    public static String PREFIX = "§8[§eOrganisation§8] §e" + Messages.ARROW + " §7";

    public int getLevel() {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_level WHERE organisationID='" + this.id + "'")) {
            if(rs.next()) {
                return rs.getInt("level");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Location getDbank() {
        return dbank;
    }

    public void setLevel(int level) {
        Script.executeUpdate("UPDATE organisation_level SET level='" + level + "' WHERE organisationID='" + this.id + "'");
    }

    public void addLevel(int amount) {
        Script.executeUpdate("UPDATE organisation_level SET level='" + (getLevel() + amount) + "' WHERE organisationID='" + this.id + "'");
    }

    public void removeLevel(int amount) {
        Script.executeUpdate("UPDATE organisation_level SET level='" + (getLevel() - amount) + "' WHERE organisationID='" + this.id + "'");
    }

    public void setRank(Player p, int rank) {
        Script.executeUpdate("UPDATE organisation SET rank='" + rank + "' WHERE nrp_id='" + Script.getNRPID(p) + "'");
    }

    public String getMOTD() {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_motd WHERE organisationID='" + this.id + "'")) {
            if(rs.next()) {
                return rs.getString("motd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMOTD(String motd) {
        if(getMOTD() == null) {
            Script.executeUpdate("INSERT INTO organisation_motd (organisationID, motd) VALUES ('" + this.id + "', '" + motd + "')");
            return;
        }
        Script.executeUpdate("UPDATE organisation_motd SET motd = '" + motd + "' WHERE organisationID = " + this.id);
    }

    public int getExp() {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_level WHERE organisationID='" + this.id + "'")) {
            if(rs.next()) {
                return rs.getInt("exp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addExp(int amount) {
        if(this.getLevel()==10) {
            for(Player all : getMembers()) {
                Script.sendActionBar(all, PREFIX + "Deine Organisation hat das maximale Level erreicht.");
            }
            return;
        }
        if(getExp() + amount >= getLevelCost()) {
            sendMessage(PREFIX + "Deine Organisation hat Level " + (getLevel() + 1) + " erreicht.");
            Script.executeUpdate("UPDATE organisation_level SET level='" + (getLevel() + 1) + "', exp='" + 0 + "' WHERE organisationID='" + this.id + "'");
        } else {
            Script.executeUpdate("UPDATE organisation_level SET exp='" + (getExp() + amount) + "' WHERE organisationID='" + this.id + "'");
            sendMessage(PREFIX + "Deine Organisation hat " + amount + " Erfahrungspunkte erhalten §8(§e" + getExp() + "§7/§e" + getLevelCost() + "§8)§7.");
        }
    }

    public int getLevelCost() {
        int level_cost;
        level_cost = 692 + ((getLevel() * 2) * 3628);
        if (getLevel() % 2 == 0) {
            level_cost += 173;
        }
        return level_cost;
    }

    public ArrayList<Location> getDoors() {
        ArrayList<Location> locs = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT orgID, x, y, z FROM orgadoor WHERE orgID=" + this.id)) {
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

    public OrgSpray.FraktionSpray getFraktionSpray() {
        return fraktionSpray;
    }

    public void removeExp(int amount) {
        if(getExp() - amount < 0) {
            sendMessage(PREFIX + "Deine Organisation hat Level " + (getLevel() - 1) + " erreicht.");
            Script.executeUpdate("UPDATE organisation_level SET level='" + (getLevel() - 1) + "' WHERE organisationID='" + this.id + "'");
            Script.executeUpdate("UPDATE organisation_level SET exp='" + (getLevelCost() - (getLevelCost() - amount)) + "' WHERE organisationID='" + this.id + "'");
        } else {
            Script.executeUpdate("UPDATE organisation_level SET exp='" + (getExp() - amount) + "' WHERE organisationID='" + this.id + "'");
            sendMessage(PREFIX + "Deine Organisation hat " + amount + " Erfahrungspunkte verloren §8(§e" + getExp() + "§7/§e" + getLevelCost() + "§8)§7.");
        }
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
        return getLevel() >= 1;
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
        for (OfflinePlayer all : this.getAllMembers()) {
            if(all.isOnline()) list.add(all.getPlayer());
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

        // sort list by last disconnect the longest time ago first
        list.sort((o1, o2) -> {
            long l1 = Script.getLastDisconnect(o1);
            long l2 = Script.getLastDisconnect(o2);
            return Long.compare(l2, l1);
        });

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

    public List<OfflinePlayer> getAllLeaders() {
        List<OfflinePlayer> list = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM organisation WHERE organisationID='" + this.id + "' AND leader=1")) {
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

    public static int getRank(Player p) {
        return Script.getInt(p, "organisation", "rank");
    }

    public static int getRank(OfflinePlayer p) {
        return Script.getInt(p, "organisation", "rank");
    }


    public void addMember(Player p, Player leader) {
        Script.executeUpdate("INSERT INTO organisation (nrp_id, organisationID, salary, rank, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage(PREFIX + Script.getName(p) + "  ist der Organisation beigetreten.");
        }
        sendLeaderMessage(PREFIX + Script.getName(leader) + " hat " + Script.getName(p) + " in die Organisation eingeladen.");
        Script.sendTeamMessage("§8[§6OC§8] §6" + Messages.ARROW + " §7" + Script.getName(leader) + " §7hat " + Script.getName(p) + " in die Organisation " + getName() + " eingeladen.");
    }

    public void addMember(OfflinePlayer p) {
        Script.executeUpdate("INSERT INTO organisation (nrp_id, OrganisationID, salary, rank, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage(PREFIX + p.getName() + " ist der Organisation beigetreten.");
        }
        sendLeaderMessage(PREFIX + p.getName() + " ist nun Teil der Organisation.");
    }

    public void removeMember(Player p, Player leader) {
        if (Duty.isInDuty(p)) Duty.removeDuty(p);
        Equip.removeEquip(p);
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage(PREFIX + Script.getName(p) + " §7hat die Organisation verlassen.");
        }
        sendLeaderMessage(PREFIX + Script.getName(leader) + " §7hat " + Script.getName(p) + " aus der Organisation geworfen.");
        Script.sendTeamMessage("§8[§6OC§8] §6" + Messages.ARROW + " §7" + Script.getName(leader) + " §7hat " + Script.getName(p) + " aus der Organisation " + getName() + " geworfen.");
    }

    public void removeMember(OfflinePlayer p, Player leader) {
        if (p.getPlayer() != null) {
            if (Duty.isInDuty(p.getPlayer())) Duty.removeDuty(p.getPlayer());
            Equip.removeEquip(p.getPlayer());
        }
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage(PREFIX + p.getName() + " §7hat die Organisation verlassen.");
        }
        sendLeaderMessage(PREFIX + Script.getName(leader) + " §7hat " + p.getName() + " aus die Organisation geworfen.");
        Script.sendTeamMessage("§8[§6OC§8] §6" + Messages.ARROW + " §7" + Script.getName(leader) + " §7hat " + p.getName() + " aus der Organisation " + getName() + " geworfen.");
        Script.addOfflineMessage(p, "§8[§6OC§8] §6" + Messages.ARROW + " §7Du wurdest aus deiner Organisation geworfen.");
    }

    public void removeMember(OfflinePlayer p) {
        Script.executeUpdate("DELETE FROM organisation WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        for (Player members : getPlayersFromOrganisation(this)) {
            members.sendMessage(PREFIX + p.getName() + " §7hat die Organisation verlassen.");
        }
    }

    public static String getRankName(Player p) {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_rankname WHERE organisationID='" + Organisation.getOrganisation(p).getID() + "' AND rank='" + Organisation.getRank(p) + "' AND gender='" + Script.getGender(p).getName().charAt(0)+"'")) {
            if(rs.next()) {
                return rs.getString("name").replace("-"," ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Kein Rangname vorhanden";
    }

    public static String getRankName(OfflinePlayer p) {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_rankname WHERE organisationID='" + Organisation.getOrganisation(p).getID() + "' AND rank='" + Organisation.getRank(p) + "' AND gender='" + Script.getGender(p).getName().charAt(0) + "'")) {
            if(rs.next()) {
                return rs.getString("name").replace("-"," ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Kein Rangname vorhanden";
    }

    public String getRankName(int rank, Gender g) {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisation_rankname WHERE organisationID='" + this.id + "' AND rank='" + rank + "' AND gender='" + g.getName().charAt(0) + "'")) {
            if(rs.next()) {
                return rs.getString("name").replace("-"," ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Kein Rangname vorhanden";
    }

    public void setRankName(int rank, Gender gender, String arg) {
        if(getRankName(rank, gender).equalsIgnoreCase("Kein Rangname vorhanden")) {
            Script.executeAsyncUpdate("INSERT INTO organisation_rankname (organisationID, rank, gender, name) VALUES (" + this.id + ", '" + rank + "', '" + gender.getName().charAt(0) + "', '" + arg + "')");
            return;
        }
        Script.executeAsyncUpdate("UPDATE organisation_rankname SET name = '" + arg + "' WHERE organisationID = " + this.id + " AND rank = " + rank + " AND gender = '" + gender.getName().charAt(0) + "'");
    }
}
