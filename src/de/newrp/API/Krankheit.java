package de.newrp.API;

import de.newrp.main;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public enum Krankheit {
    HUSTEN(0, "Husten", true, true, true),
    CHOLERA(3, "Cholera", true, false, false),
    GEBROCHENES_BEIN(5, "Gebrochenes Bein", false, false, false),
    GEBROCHENER_ARM(6, "Gebrochener Arm", false, false, false),
    ENTZUENDUNG(7, "Entzündung", false, false, false);

    private final int id;
    private final String name;
    private final boolean foodIntolerance;
    private final boolean transmittable;
    private final boolean isImpfable;

    Krankheit(int id, String name, boolean foodIntolerance, boolean transmittable, boolean isImpfable) {
        this.id = id;
        this.name = name;
        this.foodIntolerance = foodIntolerance;
        this.transmittable = transmittable;
        this.isImpfable = isImpfable;
    }

    public static Krankheit getKrankheitByName(String name) {
        for (Krankheit k : Krankheit.values()) {
            if (k.getName().equalsIgnoreCase(name)) return k;
        }
        return null;
    }

    public static Krankheit getKrankheitByID(int id) {
        for (Krankheit k : Krankheit.values()) {
            if (k.getID() == id) return k;
        }
        return null;
    }

    public static ArrayList<Krankheit> getAllKrankheiten(int id) {
        ArrayList<Krankheit> all = new ArrayList<>();
        HashMap<Krankheit, Boolean> cache = getKrankheiten(id);

        if (cache != null) {
            for (Krankheit k : values()) {
                if (cache.get(k)) {
                    all.add(k);
                }
            }
        }
        return all;
    }

    public static boolean fastCheckFoodIntolerance(Player p) {
        HashMap<Krankheit, Boolean> cache = getKrankheiten(Script.getNRPID(p));

        if (cache != null) {
            for (Krankheit k : values()) {
                if (k.isFoodIntolerance()) {
                    if (cache.get(k)) return true;
                }
            }
        }
        return false;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isFoodIntolerance() {
        return this.foodIntolerance;
    }

    public boolean isTransmittable() {
        return this.transmittable;
    }

    public void add(int id) {
        Script.executeAsyncUpdate("INSERT INTO krankheit (userID, krankheitID, time) VALUES (" + id + ", " + this.getID() + ", " + System.currentTimeMillis() + ");");
    }

    public void remove(int id) {
        Script.executeAsyncUpdate("DELETE FROM krankheit WHERE userID = " + id + " AND krankheitID = " + this.getID());
    }

    public boolean isImpfed(int id) {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM impfung WHERE nrp_id = " + id + " AND krankheitID = " + this.getID())) {
            if(rs.next()) {
                return rs.getLong("until") > System.currentTimeMillis();
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isImpfable() {
        return this.isImpfable;
    }

    public void setImpfed(int id, long until) {
        Script.executeAsyncUpdate("DELETE FROM impfung WHERE nrp_id = " + id + " AND krankheitID = " + this.getID());
        Script.executeAsyncUpdate("INSERT INTO impfung (nrp_id, krankheitID, until) VALUES (" + id + ", " + this.getID() + ", " + until + ")");
    }

    public boolean isInfected(int id) {
        HashMap<Krankheit, Boolean> cache = getKrankheiten(id);
        if (cache != null) {
            return cache.get(this);
        }
        return false;
    }

    public static HashMap<Krankheit, Boolean> getKrankheiten(Integer id) {
        HashMap<Krankheit, Boolean> map = new HashMap<>();
        for (Krankheit k : Krankheit.values()) map.put(k, false);
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT krankheitID FROM krankheit WHERE userID = " + id)) {
            while (rs.next()) {
                Krankheit k = Krankheit.getKrankheitByID(rs.getInt("krankheitID"));
                if (k != null) map.put(k, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
