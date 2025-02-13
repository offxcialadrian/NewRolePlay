package de.newrp.Berufe;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Forum.ForumGroup;
import de.newrp.Government.Arbeitslosengeld;
import de.newrp.Organisationen.Organisation;
import de.newrp.TeamSpeak.TeamspeakServerGroup;
import de.newrp.NewRoleplayMain;
import de.newrp.Vehicle.CarType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Beruf {

    public static String PREFIX = "§8[§eBeruf§8] §e" + Messages.ARROW + " §7";

    public enum Berufe {
        GOVERNMENT(1, "Regierung", new Location(Script.WORLD, 562, 88, 992, 270.80725f, 18.372393f),false, true, true, 56, TeamspeakServerGroup.GOVERNMENT, new ForumGroup[]{ForumGroup.GOVERNMENT, ForumGroup.GOVERNMENT_LEADER}, CarType.MERCADAS, new Location(Script.WORLD,  466, 69, 981, 180, 0), 19, new Location(Script.WORLD, 540, 88, 981)),
        NEWS(2, "News", new Location(Script.WORLD, 302, 67, 777, -24.381052f, 8.922394f), true, true, true, 95, TeamspeakServerGroup.NEWS, new ForumGroup[]{ForumGroup.NEWS, ForumGroup.NEWS_LEADER}, CarType.NMW, new Location(Script.WORLD, 283, 68, 741, -90, 0), 21, new Location(Script.WORLD, 289, 67, 788)),
        POLICE(3, "Polizei", new Location(Script.WORLD, 420, 71, 819, 185.04346f, 15.860464f),false, true, true, 70, TeamspeakServerGroup.POLICE, new ForumGroup[]{ForumGroup.POLICE, ForumGroup.POLICE_LEADER}, CarType.AWDI, new Location(Script.WORLD, 431, 68, 859, 0, 0), 11, new Location(Script.WORLD, 405, 71, 824)),
        RETTUNGSDIENST(4, "Rettungsdienst", new Location(Script.WORLD, 263, 75, 1254, 189.6543f, 26.346867f),false, true,true, 83, TeamspeakServerGroup.RETTUNGSDIENST, new ForumGroup[]{ForumGroup.RETTUNGSDIENST, ForumGroup.RETTUNGSDIENST_LEADER}, CarType.VOLTSWAGEN, new Location(Script.WORLD, 353, 75, 1253, 90, 0), 25, new Location(Script.WORLD, 267, 75, 1253));

        int id;
        private final String name;
        Location loc;
        boolean kasse;
        boolean duty;
        boolean equip;
        int channelid;
        TeamspeakServerGroup serverGroup;
        ForumGroup[] forumGroup;
        CarType carType;
        Location garage;
        int width;
        @Getter
        final Location equipLoc;

        Berufe(int id, String name, Location loc, boolean kasse, boolean duty, boolean equip, int channelid, TeamspeakServerGroup serverGroup, ForumGroup[] forumGroup, CarType carType, Location garage, int width, Location equipLoc) {
            this.id = id;
            this.name = name;
            this.loc = loc;
            this.kasse = kasse;
            this.duty = duty;
            this.equip = equip;
            this.channelid = channelid;
            this.serverGroup = serverGroup;
            this.forumGroup = forumGroup;
            this.carType = carType;
            this.garage = garage;
            this.width = width;
            this.equipLoc = equipLoc;
        }

        public static Map<Berufe, HashMap<UUID, Boolean>> BERUF_MEMBER = new ConcurrentHashMap<>();

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

        public Location getLoc() {
            return loc;
        }

        public CarType getCarType() {
            return carType;
        }

        public Location getGarage() {
            Location loc = new Location(Script.WORLD, this.garage.getX(), this.garage.getY(), this.garage.getZ(), this.garage.getYaw(), this.garage.getPitch());
            return loc.add(new Random().nextInt(this.getGarageWidth()), 0 , 0);
        }

        public int getGarageWidth() {
            return this.width;
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

        private static final HashMap<Location, Berufe> DOORS = new HashMap<>();

        public static void loadDoors() {
            setDoors();
        }

        public void addDoor(Location loc) {
            DOORS.put(loc, this);
        }

        public ArrayList<Location> getDoors() {
            ArrayList<Location> doors = new ArrayList<>();
            for (Location loc : DOORS.keySet())
                if (DOORS.get(loc) == this) doors.add(loc);
            return doors;
        }

        public static void setDoors() {
            ArrayList<Location> locs = new ArrayList<>();
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT berufID, x, y, z FROM berufsdoor")) {
                while (rs.next()) {
                    int id = rs.getInt("berufID");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    Location loc = new Location(Script.WORLD, x, y, z);
                    if (!DOORS.containsKey(loc)) DOORS.put(loc, getBeruf(id));
                }
            } catch (SQLException e) {
                Debug.debug("SQLException -> " + e.getMessage());
                e.printStackTrace();
            }
        }

        public int getKasse() {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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

        public String getMOTD() {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe_motd WHERE berufeID='" + this.id + "'")) {
                if (rs.next()) {
                    return rs.getString("motd");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setMOTD(String motd) {
            if (getMOTD() == null) {
                Script.executeUpdate("INSERT INTO berufe_motd (berufeID, motd) VALUES ('" + this.id + "', '" + motd + "')");
            } else {
                Script.executeUpdate("UPDATE berufe_motd SET motd='" + motd + "' WHERE berufeID='" + this.id + "'");
            }
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
            for (UUID all : getBeruf().keySet()) {
                Objects.requireNonNull(Bukkit.getPlayer(all)).sendMessage(message);
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
                if (all.isOnline()) list.add(all.getPlayer());
            }
            return list;
        }

        public List<UUID> getMember() {
            return new ArrayList<>(getBeruf().keySet());
        }

        public HashMap<UUID, Boolean> getBeruf() {
            if (!BERUF_MEMBER.containsKey(this)) {
                BERUF_MEMBER.put(this, new HashMap<>());
            }
            return BERUF_MEMBER.get(this);
        }

        public void changeDuty(Player player, Boolean duty) {
            getBeruf().put(player.getUniqueId(), duty);
        }

        public Boolean isDuty(Player player) {
            final HashMap<UUID, Boolean> result = getBeruf();
            if (result == null) {
                return false;
            }
            return result.getOrDefault(player.getUniqueId(), false);
        }

        public Boolean isDuty(UUID player) {
            final HashMap<UUID, Boolean> result = getBeruf();
            if (result == null) {
                return false;
            }
            return result.getOrDefault(player, false);
        }

        public void setMember(Player player) {
            getBeruf().put(player.getUniqueId(), false);
        }

        public void deleteMember(Player player) {
            getBeruf().remove(player.getUniqueId());
        }

        public Boolean isMember(Player player) {
            return Objects.requireNonNull(getBeruf()).containsKey(player.getUniqueId());
        }

        public List<OfflinePlayer> getAllMembers() {
            List<OfflinePlayer> list = new ArrayList<>();
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe WHERE berufID='" + this.id + "' ORDER BY abteilung DESC")) {
                if (rs.next()) {
                    do {
                        list.add(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                    } while (rs.next());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                if (hasBeruf(all, this)) {
                    if (isLeader(all, true)) {
                        list.add(all);
                    }
                }
            }
            return list;
        }

        public static boolean hasAbteilung(Player p, Abteilung abteilung) {
            if(Beruf.isLeader(p, true)) return true;
            if(getAbteilung(p, true) == null) return false;
            return (getAbteilung(p, true).equals(abteilung));
        }

        public void addMember(Player p, Player leader) {
            Script.executeUpdate("INSERT INTO berufe (nrp_id, berufID, salary, abteilung, leader, coleader, invite) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0', " + System.currentTimeMillis() + ")");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " ist dem Beruf beigetreten.");
            }
            sendLeaderMessage("§8[§6" + getName() + "§8] §6» §7" + Script.getName(leader) + " hat " + Script.getName(p) + " in den Beruf eingeladen.");
            Script.sendTeamMessage("§8[§6BC§8] §6» §7" + Script.getName(leader) + " hat " + Script.getName(p) + " in den Beruf " + getName() + " eingeladen.");
            if (Arbeitslosengeld.hasArbeitslosengeld(p))
                p.sendMessage(Messages.INFO + "Dein Arbeitslosengeld wurde automatisch gekündigt.");
            Arbeitslosengeld.deleteArbeitslosengeld(p);
        }

        public void addMember(OfflinePlayer p) {
            Script.executeUpdate("INSERT INTO berufe (nrp_id, berufID, salary, abteilung, leader, coleader, invite) VALUES ('" + Script.getNRPID(p) + "', '" + getID() + "', '0', '0', '0', '0', " + System.currentTimeMillis() + ")");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " ist dem Beruf beigetreten.");
            }
            sendLeaderMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " ist nun Teil des Berufs.");
            Arbeitslosengeld.deleteArbeitslosengeld(p);
        }

        public void removeMember(Player p, Player leader) {
            if (Duty.isInDuty(p)) Duty.removeDuty(p);
            Equip.removeEquip(p);
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " hat den Beruf verlassen.");
            }
            sendLeaderMessage("§8[§6" + getName() + "§8] §6» §7" + Script.getName(leader) + " §7hat " + Script.getName(p) + " aus dem Beruf geworfen.");
            Script.sendTeamMessage("§8[§6BC§8] §6» §7" + Script.getName(leader) + " §7hat " + Script.getName(p) + " aus dem Beruf " + getName() + " geworfen.");
        }

        public void removeMember(OfflinePlayer p, Player leader) {
            if(p.getPlayer() != null) {
                if(Duty.isInDuty(p.getPlayer())) Duty.removeDuty(p.getPlayer());
                Equip.removeEquip(p.getPlayer());
            }
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " hat den Beruf verlassen.");
            }
            sendLeaderMessage("§8[§6" + getName() + "§8] §6» §7" + Script.getName(leader) + " hat " + p.getName() + " aus dem Beruf geworfen.");
            Script.sendTeamMessage("§8[§6BC§8] §6» §7" + Script.getName(leader) + " hat " + p.getName() + " aus dem Beruf " + getName() + " geworfen.");
        }

        public void removeMember(OfflinePlayer p) {
            Script.executeUpdate("DELETE FROM berufe WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            for (Player members : getPlayersFromBeruf(this)) {
                members.sendMessage("§8[§6" + getName() + "§8] §6» §7" + p.getName() + " hat den Beruf verlassen.");
            }
        }

        public int getCarAmount() {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe_cars WHERE beruf_id='" + this.getID() + "'")) {
                if (rs.next()) {
                    return rs.getInt("amount");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void setCarAmount(int a) {
            Script.executeUpdate("UPDATE berufe_cars SET amount='" + a + "' WHERE beruf_id='"+ this.getID() + "'");
        }

        public int getLeasedAmount() {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM berufe_cars WHERE beruf_id='" + this.getID() + "'")) {
                if (rs.next()) {
                    return rs.getInt("leased");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void setLeasedAmount(int l) {
            Script.executeUpdate("UPDATE berufe_cars SET leased='" + l + "' WHERE beruf_id='"+ this.getID() + "'");
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

    public static Abteilung.Abteilungen getAbteilung(Player p, boolean getLower) {
        Abteilung.Abteilungen abteilung = Abteilung.Abteilungen.getAbteilung(Script.getInt(p, "berufe", "abteilung"), getBeruf(p));
        if (getLower) {
            if (abteilung != null) {
                switch (abteilung) {
                    case FINANZMINISTER:
                        return Abteilung.Abteilungen.FINANZAMT;
                    case JUSTIZMINISTER:
                        return Abteilung.Abteilungen.JUSTIZMINISTERIUM;
                    default:
                        return abteilung;
                }
            }
        }
        return abteilung;
    }

    public static Abteilung.Abteilungen getAbteilung(OfflinePlayer p, boolean getLower) {
        Abteilung.Abteilungen abteilung = Abteilung.Abteilungen.getAbteilung(Script.getInt(p, "berufe", "abteilung"), getBeruf(p));
        if (getLower) {
            if (abteilung != null) {
                switch (abteilung) {
                    case FINANZMINISTER:
                        return Abteilung.Abteilungen.FINANZAMT;
                    case JUSTIZMINISTER:
                        return Abteilung.Abteilungen.JUSTIZMINISTERIUM;
                    default:
                        return abteilung;
                }
            }
        }
        return abteilung;
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

    public static boolean hasAbteilung(Player p, Abteilung.Abteilungen... abteilungen) {
        if(isLeader(p, true)) return true;
        if(getAbteilung(p, true) == null) return false;

        for (Abteilung.Abteilungen abteilung : abteilungen) {
            if(getAbteilung(p, true).equals(abteilung)) return true;
        }
        return false;
    }

    public static boolean hasBeruf(Player p, Berufe berufe) {
        if(Beruf.getBeruf(p) == null) return false;
        return Beruf.getBeruf(p).equals(berufe);
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

    public static long getInvite(OfflinePlayer player) {
        return Script.getLong(player, "berufe", "invite");
    }
}
