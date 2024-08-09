package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Blacklist {

    public static final String PREFIX = "§8[§6Blacklist§8] §6" + Messages.ARROW + " §7";
    public static HashMap<Organisation, List<Blacklist>> BLACKLIST = new HashMap<>();
    private final int userID;
    private final String username;
    private final Organisation organisation;
    private final String reason;
    private final long time;
    private int kills;
    private int price;

    public Blacklist(int userID, String username, Organisation organisation, String reason, long time, int kills, int price) {
        this.userID = userID;
        this.username = username;
        this.organisation = organisation;
        this.reason = reason;
        this.time = time;
        this.kills = kills;
        this.price = price;
    }

    public static List<Blacklist> getBlacklist(Organisation f) {
        if (BLACKLIST.containsKey(f)) {
            return BLACKLIST.get(f);
        }
        List<Blacklist> bl = new ArrayList<>();
        BLACKLIST.put(f, bl);
        return bl;
    }

    public static boolean isOnBlacklist(Player p, Organisation f) {
        if (!BLACKLIST.containsKey(f)) return false;
        String name = p.getName();
        for (Blacklist bl : BLACKLIST.get(f)) {
            if (bl.getUserName().equals(name)) return true;
        }
        return false;
    }

    public static boolean isOnBlacklist(OfflinePlayer p, Organisation f) {
        if (!BLACKLIST.containsKey(f)) return false;
        String name = p.getName();
        for (Blacklist bl : BLACKLIST.get(f)) {
            if (bl.getUserName().equals(name)) return true;
        }
        return false;
    }

    public static Blacklist getBlacklistObject(int id, Organisation f) {
        List<Blacklist> blacklist = getBlacklist(f);
        for (Blacklist bl : blacklist) {
            if (bl.getUserID() == id) return bl;
        }
        return null;
    }

    public static void load() {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            HashMap<Organisation, List<Blacklist>> blacklist = new HashMap<>();
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT blacklist.organisationID, blacklist.userID, nrp_id.name, blacklist.reason, blacklist.time, blacklist.kills, blacklist.price FROM blacklist LEFT JOIN nrp_id ON nrp_id.id = blacklist.userID")) {
                while (rs.next()) {
                    Organisation f = Organisation.getOrganisation(rs.getInt("organisationID"));
                    if(f == null) continue;
                    int userID = rs.getInt("userID");
                    String name = rs.getString("name");
                    String reason = rs.getString("reason");
                    long time = rs.getLong("time");
                    int kills = rs.getInt("kills");
                    int price = rs.getInt("price");
                    if(!blacklist.containsKey(f)) {
                        Bukkit.getLogger().info("Added bl for " + f.getName());
                        blacklist.put(f, new ArrayList<>());
                    }
                    blacklist.get(f).add(new Blacklist(userID, name, f, reason, time, kills, price));
                }
                BLACKLIST = blacklist;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void add(Player p, Organisation f, String reason, int kills, int price) {
        Blacklist bl = new Blacklist(Script.getNRPID(p), p.getName(), f, reason, System.currentTimeMillis(), kills, price);
        BLACKLIST.get(f).add(bl);
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO blacklist (userID, organisationID, reason, time, kills, price) VALUES (?, ?, ?, ?, ?, ?);")) {
                statement.setInt(1, Script.getNRPID(p));
                statement.setInt(2, f.getID());
                statement.setString(3, reason);
                statement.setLong(4, System.currentTimeMillis());
                statement.setInt(5, kills);
                statement.setInt(6, price);

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void remove(Player p, Organisation f) {
        int id = Script.getNRPID(p);
        Iterator<Blacklist> it = BLACKLIST.get(f).iterator();
        while (it.hasNext()) {
            Blacklist bl = it.next();
            if (bl.getUserID() == id) {
                it.remove();
                break;
            }
        }
        Script.executeAsyncUpdate("DELETE FROM blacklist WHERE userID=" + id + " AND organisationID=" + f.getID());
    }

    public static void remove(int id, Organisation f) {
        Iterator<Blacklist> it = BLACKLIST.get(f).iterator();
        while (it.hasNext()) {
            Blacklist bl = it.next();
            if (bl.getUserID() == id) {
                it.remove();
                break;
            }
        }
        Script.executeAsyncUpdate("DELETE FROM blacklist WHERE userID=" + id + " AND organisationID=" + f.getID());
    }

    public String toString() {
        return "{userID: " + userID + ", username: " + username + ", organisation: " + organisation + ", reason: " + reason + ", time: " + time + ", kills: " + kills + ", price: " + price + "}";
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUserName() {
        return this.username;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public String getReason() {
        return this.reason;
    }

    public long getTime() {
        return this.time;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        Script.executeAsyncUpdate("UPDATE blacklist SET kills=" + kills + " WHERE userID=" + this.userID + " AND organisationID=" + this.organisation.getID());
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
        Script.executeAsyncUpdate("UPDATE blacklist SET price=" + price + " WHERE userID=" + this.userID + " AND organisationID=" + this.organisation.getID());
    }
}
