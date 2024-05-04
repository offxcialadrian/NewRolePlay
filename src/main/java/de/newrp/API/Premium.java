package de.newrp.API;

import de.newrp.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class Premium {

    public static String PREFIX = "§8[§b§lPremium§8] §b» §7";

    public static boolean hasPremium(Player p) {
        if(Script.hasRank(p, Rank.MODERATOR, false)) return true;
        try (Statement stmt = Main.getConnection().createStatement();
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

    public static boolean hasPremium(Integer id) {
        if(Script.hasRank(id, Rank.MODERATOR, false)) return true;
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium WHERE nrp_id=" + id + " ORDER BY id DESC LIMIT 1;")) {
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
        try (Statement stmt = Main.getConnection().createStatement();
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
        try (Statement stmt = Main.getConnection().createStatement();
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
        try (Statement stmt = Main.getConnection().createStatement();
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
        p.sendMessage(PREFIX + "Du hast §b" + TimeUnit.MILLISECONDS.toDays(time) + " Tage §7Premium erhalten.");
        if(hasPremium(p)) {
            try (Statement stmt = Main.getConnection().createStatement();
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
            try (Statement stmt = Main.getConnection().createStatement();
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

    public static void addPremiumStorage(Player p, long time, boolean expires) {
        p.sendMessage(PREFIX + "Du hast §b" + TimeUnit.MILLISECONDS.toDays(time) + " Tage §7Premium erhalten.");
        if(expires) p.sendMessage(Messages.INFO + "Du hast nun §b7 Tage §rZeit, um dein Premium zu aktivieren. Nutze dazu §8/§6premium");
        else p.sendMessage(Messages.INFO + "Nutze §8/§6premium §rum dein Premium zu aktivieren.");
        long expireDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
        Script.executeUpdate("INSERT INTO premium_storage (nrp_id, duration, expires) VALUES (" + Script.getNRPID(p) + ", " + TimeUnit.MILLISECONDS.toDays(time) + ", " + (expires? expireDate : "NULL") + ")");
    }

    public static void addPremiumStorage(Player p, int days) {
        p.sendMessage(PREFIX + "Du hast §b§l" + days + " Tage §7Premium erhalten.");
        p.sendMessage(Messages.INFO + "Nutze §8/§6premium §rum dein Premium zu aktivieren.");
        if(days>=30) p.sendMessage(Messages.INFO + "Nutze §8/§6premium feedback §rund teile uns mit, warum du dich für den Premiumkauf entschieden hast. Du erhältst dafür 3 weitere Tage Premium.");
        Script.executeUpdate("INSERT INTO premium_storage (nrp_id, duration, expires) VALUES (" + Script.getNRPID(p) + ", " + days + ", " + "NULL" + ")");
    }

    public static void addPremiumStorage(OfflinePlayer p, int days) {
        Script.executeUpdate("INSERT INTO premium_storage (nrp_id, duration, expires) VALUES (" + Script.getNRPID(p) + ", " + days + ", " + "NULL" + ")");
    }



}
