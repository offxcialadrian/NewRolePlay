package de.newrp.API;

import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;

public class Premium {

    public static boolean hasPremium(Player p) {
        if(Script.hasRank(p, Rank.MODERATOR, false)) return true;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getPremiumTime(Player p) {
        if(Script.hasRank(p, Rank.MODERATOR, false)) return 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return rs.getLong("until");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getPremiumTime(OfflinePlayer p) {
        if(Script.hasRank(p, Rank.MODERATOR, false)) return 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return rs.getLong("until");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean hasPremium(OfflinePlayer p) {
        if(Script.hasRank(p, Rank.MODERATOR, false)) return true;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addPremium(Player p, long time) {
        if(hasPremium(p)) {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
                if (rs.next()) {
                    if (rs.getLong("until") > System.currentTimeMillis()) {
                        Script.executeAsyncUpdate("UPDATE premium SET until=" + (rs.getLong("until") + time) + " WHERE nrp_id=" + Script.getNRPID(p));
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Script.executeAsyncUpdate("INSERT INTO premium (nrp_id, until) VALUES (" + Script.getNRPID(p) + ", " + (System.currentTimeMillis() + time) + ")");
        }
    }

    public static void addPremium(OfflinePlayer p, long time) {
        if(hasPremium(p)) {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
                if (rs.next()) {
                    if (rs.getLong("until") > System.currentTimeMillis()) {
                        Script.executeAsyncUpdate("UPDATE premium SET until=" + (rs.getLong("until") + time) + " WHERE nrp_id=" + Script.getNRPID(p));
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Script.executeAsyncUpdate("INSERT INTO premium (nrp_id, until) VALUES (" + Script.getNRPID(p) + ", " + (System.currentTimeMillis() + time) + ")");
        }
    }



}
