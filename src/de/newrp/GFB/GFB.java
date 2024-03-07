package de.newrp.GFB;

import com.github.theholywaffle.teamspeak3.api.wrapper.Message;
import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public enum GFB {

    LAGERARBEITER(1, "Lagerarbeiter", new Location(Script.WORLD, 1, 70, 1)),
    TRANSPORT(2, "Transport", new Location(Script.WORLD, 1, 70, 1)),
    KELLNER(3, "Kellner", new Location(Script.WORLD, 1, 70, 1)),
    EISHALLE(4, "Eishalle", new Location(Script.WORLD, 1, 70, 1)),
    PIZZALIEFERANT(5, "Pizzalieferant", new Location(Script.WORLD, 1, 70, 1)),
    DISHWASHER(6, "Tellerwäscher", new Location(Script.WORLD, 1, 70, 1)),
    BURGERFRYER(7, "Burgerbrater", new Location(Script.WORLD, 1, 70, 1)),
    STRASSENWARTUNG(8, "Straßenwartung", new Location(Script.WORLD, 1, 70, 1)),
    IMKER(9, "Imker", new Location(Script.WORLD, 1, 70, 1));


    private int id;
    private String name;
    private Location location;

    GFB(int id, String name, Location loc) {
        this.id = id;
        this.name = name;
        this.location = loc;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public static GFB getGFBByID(int id) {
        for (GFB gfb : GFB.values()) {
            if (gfb.getID() == id) return gfb;
        }
        return null;
    }

    public static String PREFIX = "§8[§6GFB§8] §6» §7";
    public static HashMap<String, GFB> CURRENT = new HashMap<>();

    public static GFB getGFBByName(String name) {
        for (GFB gfb : GFB.values()) {
            if (gfb.getName().equalsIgnoreCase(name)) return gfb;
        }
        return null;
    }

    public int getExp(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gfb_level WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("exp");
            } else {
                Script.executeAsyncUpdate("INSERT INTO gfb_level (nrp_id, gfb_id, level, exp) VALUES ('" + Script.getNRPID(p) + "', " + this.id + ", 1, 0)");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addExp(Player p, int exp) {
        Achievement.GFB_JOBS.grant(p);
        if(getExp(p) + exp >= getLevelCost(getLevel(p))) {
            if(getLevel(p) >= 10) {
                p.sendMessage(PREFIX + Messages.INFO + "Du hast das maximale Level erreicht!");
                return;
            }
            Script.executeAsyncUpdate("UPDATE gfb_level SET level=" + (getLevel(p) + 1) + " WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
            Script.executeAsyncUpdate("UPDATE gfb_level SET exp=0 WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
            p.sendMessage(PREFIX + "Du bist beim GFB " + this.getName() + " nun Level " + (getLevel(p)) + "!");
            return;
        }
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE gfb_level SET exp=" + (getExp(p) + exp) + " WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        p.sendMessage(PREFIX + "Du hast " + exp + " Exp für den Job " + this.name + " erhalten (" + getExp(p) + "/" + getLevelCost(getLevel(p)) + ")");
    }

    public void removeExp(Player p, int exp) {
        if(getExp(p) - exp < 0) {
            Script.executeAsyncUpdate("UPDATE gfb_level SET level=" + (getLevel(p) - 1) + " WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
            Script.executeAsyncUpdate("UPDATE gfb_level SET exp=" + (getLevelCost(getLevel(p) - 1) - 1) + " WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
            p.sendMessage(PREFIX + "Du bist beim GFB " + this.getName() + " nun Level " + (getLevel(p) - 1) + "!");
            return;
        }
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE gfb_level SET exp=" + (getExp(p) - exp) + " WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        p.sendMessage(PREFIX + "Du hast " + exp + " Exp für den Job " + this.name + " verloren (" + getExp(p) + "/" + getLevelCost(getLevel(p)) + ")");
    }

    public int getLevel(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gfb_level WHERE nrp_id='" + Script.getNRPID(p) + "' AND gfb_id=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("level");
            } else {
                Script.executeAsyncUpdate("INSERT INTO gfb_level (nrp_id, gfb_id, level, exp) VALUES ('" + Script.getNRPID(p) + "', " + this.id + ", 1, 0)");
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int getLevelCost(int level) {
        int level_cost;
        level_cost = 692 + ((level * 2) * 250);
        if (level % 2 == 0) {
            level_cost += 173;
        }
        return level_cost;
    }

}
