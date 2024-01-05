package de.newrp.API;

import de.newrp.main;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public enum Krankheit {
    HUSTEN(0, "Husten", true, true),
    AIDS(1, "AIDS", true, true),
    TRIPPER(2, "Tripper", true, true),
    CHOLERA(3, "Cholera", true, false),
    HERPES(4, "Herpes", false, true),
    GEBROCHENES_BEIN(5, "Gebrochenes Bein", false, false),
    GEBROCHENER_ARM(6, "Gebrochener Arm", false, false);

    private final int id;
    private final String name;
    private final boolean foodIntolerance;
    private final boolean transmittable;

    Krankheit(int id, String name, boolean foodIntolerance, boolean transmittable) {
        this.id = id;
        this.name = name;
        this.foodIntolerance = foodIntolerance;
        this.transmittable = transmittable;
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
