package de.newrp.API;

import de.newrp.main;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public enum Licenses {

    PERSONALAUSWEIS(0, "personalausweis"),
    FUEHRERSCHEIN(1, "fuehrerschein"),
    WAFFENSCHEIN(2, "waffenschein"),
    ANGELSCHEIN(3, "angelschein"),
    JADDLIZENZ(4, "jagdlizenz");

    private final int id;
    private final String dbName;

    Licenses(int id, String dbName) {
        this.id = id;
        this.dbName = dbName;
    }

    public int getID() {
        return this.id;
    }

    public String getDBName() {
        return this.dbName;
    }

    public boolean hasLicense(int id) {
        Map<Licenses, Boolean> cache = getLicenses(id);
        return cache.get(this);
    }

    public void grant(int id) {
        Script.executeUpdate("UPDATE licenses SET " + this.getDBName() + "=TRUE WHERE id=" + id);
    }

    public void remove(int id) {
        Script.executeUpdate("UPDATE licenses SET " + this.getDBName() + "=FALSE WHERE id=" + id);
    }

    public static Map<Licenses, Boolean> getLicenses(Integer id) {
        Map<Licenses, Boolean> cache = new HashMap<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT personalausweis, waffenschein, fuehrerschein, angelschein, jagdlizenz FROM licenses WHERE id=" + id)) {
            if (rs.next()) {
                cache.put(Licenses.PERSONALAUSWEIS, rs.getBoolean("personalausweis"));
                cache.put(Licenses.FUEHRERSCHEIN, rs.getBoolean("fuehrerschein"));
                cache.put(Licenses.WAFFENSCHEIN, rs.getBoolean("waffenschein"));
                cache.put(Licenses.ANGELSCHEIN, rs.getBoolean("angelschein"));
                cache.put(Licenses.JADDLIZENZ, rs.getBoolean("jagdlizenz"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Licenses l : Licenses.values()) {
            if (!cache.containsKey(l)) {
                cache.put(l, false);
            }
        }
        return cache;
    }
}