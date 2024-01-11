package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
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
import java.util.UUID;

public class Beruf {

    public enum Berufe {
        GOVERNMENT(1, "Regierung", false, false, false, 218, TeamspeakServerGroup.GOVERNMENT, new ForumGroup[]{ForumGroup.GOVERNMENT, ForumGroup.GOVERNMENT_LEADER}),
        NEWS(2, "News", true, false, true, 357, TeamspeakServerGroup.NEWS, new ForumGroup[]{ForumGroup.NEWS, ForumGroup.NEWS_LEADER}),
        POLICE(3, "Polizei", false, true, true, 197, TeamspeakServerGroup.POLICE, new ForumGroup[]{ForumGroup.POLICE, ForumGroup.POLICE_LEADER}),
        RETTUNGSDIENST(4, "Rettungsdienst", false, true,true, 232, TeamspeakServerGroup.RETTUNGSDIENST, new ForumGroup[]{ForumGroup.RETTUNGSDIENST, ForumGroup.RETTUNGSDIENST_LEADER});

        int id;
        private final String name;
        boolean kasse;
        boolean duty;
        boolean equip;
        int channelid;
        TeamspeakServerGroup serverGroup;
        ForumGroup[] forumGroup;

        Berufe(int id, String name, boolean kasse, boolean duty, boolean equip, int channelid, TeamspeakServerGroup serverGroup, ForumGroup[] forumGroup) {
            this.id = id;
            this.name = name;
            this.kasse = kasse;
            this.duty = duty;
            this.equip = equip;
            this.channelid = channelid;
            this.serverGroup = serverGroup;
            this.forumGroup = forumGroup;
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

        public static Berufe getBeruf(String name) {
            for (Berufe beruf : Berufe.values()) {
                if (beruf.getName().equalsIgnoreCase(name)) {
                    return beruf;
                }
            }
            return null;
        }

        public boolean hasKasse() {
            return kasse;
        }

        public ArrayList<Location> getDoors() {
            ArrayList<Location> locs = new ArrayList<>();
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT berufID, x, y, z FROM berufsdoor WHERE berufID=" + this.id)) {
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
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe_kasse WHERE berufID='" + this.id + "'")) {
                if (rs.next()) {
                    return rs.getInt("kasse");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void setKasse(int amount) {
            Script.executeUpdate("UPDATE berufe_kasse SET kasse='" + amount + "' WHERE berufID='" + this.id + "'");
        }

        public void addKasse(int amount) {
            Script.executeUpdate("UPDATE berufe_kasse SET kasse='" + (getKasse() + amount) + "' WHERE berufID='" + this.id + "'");
        }

        public void removeKasse(int amount) {
            Script.executeUpdate("UPDATE berufe_kasse SET kasse='" + (getKasse() - amount) + "' WHERE berufID='" + this.id + "'");
        }


        public boolean isLeader(Player p, boolean coleader) {
            if(Script.getInt(p, "berufe", "leader") == 1) return true;
            if(coleader) {
                return Script.getInt(p, "berufe", "coleader") == 1;
            }
            return false;
        }

        public boolean isLeader(int id, boolean coleader) {
            OfflinePlayer p = Script.getOfflinePlayer(id);
            if(Script.getInt(p, "berufe", "leader") == 1) return true;
            if(coleader) {
                return Script.getInt(p, "berufe", "coleader") == 1;
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
                if (hasBeruf(all, this)) {
                    list.add(all);
                }
            }
            return list;
        }

        public List<OfflinePlayer> getAllMembers() {
            List<OfflinePlayer> list = new ArrayList<>();
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe WHERE berufID='" + this.id + "' ORDER BY abteilung DESC")) {
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
                if (hasBeruf(all, this)) {
                    if (isLeader(all, true)) {
                        list.add(all);
                    }
                }
            }
            return list;
        }

        public void addMember(Player p, Player leader) {
            Script.executeUpdate("INSERT INTO berufe (nrp_id, berufID, salary, abteilung, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §eist dem Beruf beigetreten.");
            }
            sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " in den Beruf eingeladen.");
            Script.sendTeamMessage("§8[§eBerufeControl§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " in den Beruf " + getName() + " eingeladen.");
            if (Arbeitslosengeld.hasArbeitslosengeld(p))
                p.sendMessage(Messages.INFO + "Dein Arbeitslosengeld wurde automatisch gekündigt.");
            Arbeitslosengeld.deleteArbeitslosengeld(p);
        }

        public void addMember(OfflinePlayer p) {
            Script.executeUpdate("INSERT INTO berufe (nrp_id, berufID, salary, abteilung, leader, coleader) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0')");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §eist dem Beruf beigetreten.");
            }
            sendLeaderMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " ist nun Teil des Berufs.");
            Arbeitslosengeld.deleteArbeitslosengeld(p);
        }

        public void removeMember(Player p, Player leader) {
            if(Duty.isInDuty(p)) Duty.removeDuty(p);
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat den Beruf verlassen.");
            }
            sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " aus dem Beruf geworfen.");
            Script.sendTeamMessage("§8[§eBerufeControl§8] §e" + Script.getName(leader) + " §ehat " + Script.getName(p) + " aus dem Beruf " + getName() + " geworfen.");
        }

        public void removeMember(OfflinePlayer p, Player leader) {
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat den Beruf verlassen.");
            }
            sendLeaderMessage("§8[§e" + getName() + "§8] §e" + Script.getName(leader) + " §ehat " + p.getName() + " aus dem Beruf geworfen.");
            Script.sendTeamMessage("§8[§eBerufeControl§8] §e" + Script.getName(leader) + " §ehat " + p.getName() + " aus dem Beruf " + getName() + " geworfen.");
        }

        public void removeMember(OfflinePlayer p) {
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§e" + getName() + "§8] §e" + p.getName() + " §ehat den Beruf verlassen.");
            }
        }

        public static Berufe getBeruf(int id) {
            for (Berufe beruf : Berufe.values()) {
                if (beruf.getID() == id) {
                    return beruf;
                }
            }
            return null;
        }


    }




    public static Berufe getBeruf(Player p) {
        return Berufe.getBeruf(Script.getInt(p, "berufe", "berufID"));
    }

    public static Berufe getBeruf(OfflinePlayer p) {
        return Berufe.getBeruf(Script.getInt(p, "berufe", "berufID"));
    }

    public static boolean hasBeruf(Player p) {
        return Script.getInt(p, "berufe", "berufID") != 0;
    }

    public static boolean hasBeruf(OfflinePlayer p) {
        return Script.getInt(p, "berufe", "berufID") != 0;
    }

    public static boolean hasBeruf(Player p, Berufe beruf) {
        return Script.getInt(p, "berufe", "berufID") == beruf.getID();
    }

    public static boolean isCoLeader(Player p) {
        return Script.getInt(p, "berufe", "coleader") == 1;
    }

    public static int getSalary(OfflinePlayer p) {
        return Script.getInt(p, "berufe", "salary");
    }

    public static int getSalary(Player p) {
        return Script.getInt(p, "berufe", "salary");
    }

    public static void setLeader(OfflinePlayer p, boolean main) {
        Script.setInt(p, "berufe", "leader", main ? 1 : 0);
        Script.setInt(p, "berufe", "coleader", main ? 0 : 1);
    }

    public static void removeLeader(OfflinePlayer p) {
        Script.setInt(p, "berufe", "leader", 0);
        Script.setInt(p, "berufe", "coleader", 0);
    }

    public static Abteilung.Abteilungen getAbteilung(Player p) {
        return Abteilung.Abteilungen.getAbteilung(Script.getInt(p, "berufe", "abteilung"), getBeruf(p));
    }

    public static Abteilung.Abteilungen getAbteilung(OfflinePlayer p) {
        return Abteilung.Abteilungen.getAbteilung(Script.getInt(p, "berufe", "abteilung"), getBeruf(p));
    }

    public static boolean isLeader(Player p, boolean coleader) {
        if(Script.getInt(p, "berufe", "leader") == 1) return true;
        if(coleader) {
            return Script.getInt(p, "berufe", "coleader") == 1;
        }
        return false;
    }

    public static boolean isLeader(OfflinePlayer p, boolean coleader) {
        if(Script.getInt(p, "berufe", "leader") == 1) return true;
        if(coleader) {
            return Script.getInt(p, "berufe", "coleader") == 1;
        }
        return false;
    }


    public static List<Player> getPlayersFromBeruf(Berufe beruf) {
        List<Player> list = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasBeruf(all, beruf)) {
                list.add(all);
            }
        }
        return list;
    }


}
