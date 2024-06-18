package de.newrp.API;

import de.newrp.NewRoleplayMain;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
public class Activity {

    public static String PREFIX = "§8[§3Akti§8] §3" + Messages.ARROW + " §3";

    public final int id;
    public final int giver;
    public final String name;
    public final float points;
    public final long time;

    public Activity(int id, int giver, String name, float points, long time) {
        this.id = id;
        this.giver = giver;
        this.name = name;
        this.points = points;
        this.time = time;
    }

    public static List<Activity> getActivities(int id, long time) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM activity WHERE id = " + id + " AND time > " + time)) {
            List<Activity> activities = new ArrayList<>();
            while (rs.next()) {
                activities.add(new Activity(id, rs.getInt("giver"), rs.getString("name"), rs.getFloat("points"), rs.getLong("time")));
            }
            return activities;
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

    public static void grantActivity(int id, Activities activity) {
        addActivity(id, 0, activity.getName(), activity.getPoints());
    }

    public static void addActivity(int id, int giver, String name, float points) {
        Script.executeAsyncUpdate("INSERT INTO activity (id, giver, name, points, time) VALUES (" + id + ", " + giver + ", '" + name + "', " + points + ", " + System.currentTimeMillis() + ");");
        if (giver != 0) {
            if (Objects.requireNonNull(Script.getOfflinePlayer(giver)).isOnline()) {
                Objects.requireNonNull(Script.getPlayer(giver)).sendMessage(PREFIX + "§7Aktivität §3§l" + name + " §8(§3" + points + "∅§8) §7an §3" + Objects.requireNonNull(Script.getOfflinePlayer(id)).getName() + " §7gegeben.");
                if (Objects.requireNonNull(Script.getOfflinePlayer(id)).isOnline()) {
                    Objects.requireNonNull(Script.getPlayer(id)).sendMessage(PREFIX + "§7Aktivität §3§l" + name + " §8(§3" + points + "∅§8) §7von §3" + Objects.requireNonNull(Script.getOfflinePlayer(giver)).getName() + " §7erhalten.");
                }
            }
        } else {
            // Evt. Folgendes auskommentieren, falls zu nervig:
            /* Edit: Jap, ist nervig
            if (Objects.requireNonNull(Script.getOfflinePlayer(id)).isOnline()) {
                Objects.requireNonNull(Script.getPlayer(id)).sendMessage(PREFIX + "§7Aktivität §3§l" + name + " §8(§3" + points + "∅§8) §7erhalten.");
            }
             */
        }
    }

    public static void deleteActivity(int id, int giver, String name) {
        Script.executeAsyncUpdate("DELETE FROM activity WHERE time = (SELECT time FROM activity WHERE name = '" + name + "' AND id = " + id + " ORDER BY time DESC LIMIT 1);");
        if (giver != 0) {
            if (Objects.requireNonNull(Script.getOfflinePlayer(giver)).isOnline()) {
                Objects.requireNonNull(Script.getPlayer(giver)).sendMessage(PREFIX + "§7Aktivität §3§l" + name + " §7von §3" + Objects.requireNonNull(Script.getOfflinePlayer(id)).getName() + " §7entfernt.");
                if (Objects.requireNonNull(Script.getOfflinePlayer(id)).isOnline()) {
                    Objects.requireNonNull(Script.getPlayer(id)).sendMessage(PREFIX + "§7Aktivität §3§l" + name + " §7durch §3" + Objects.requireNonNull(Script.getOfflinePlayer(giver)).getName() + " §7entfernt.");
                }
            }
        }
    }

    public static void deleteActivities(int id) {
        for (Activity activity : Objects.requireNonNull(getActivities(id, 0))) {
            deleteActivity(id, 0, activity.getName());
        }
    }

    public static long getResetDate(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM resetdate WHERE id = " + id)) {
            if (rs.next()) return rs.getLong("time");
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }

    public static void setResetDate(int id, long time) {
        Script.executeAsyncUpdate("UPDATE resetdate SET time = " + time + " WHERE id = " + id + ";");
    }


    public static String formatTime(long time) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(time));
    }
}
