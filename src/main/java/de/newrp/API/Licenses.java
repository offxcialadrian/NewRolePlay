package de.newrp.API;

import de.newrp.NewRoleplayMain;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public enum Licenses {

    PERSONALAUSWEIS(0, "personalausweis", "Personalausweis"),
    FUEHRERSCHEIN(1, "fuehrerschein", "Führerschein"),
    WAFFENSCHEIN(2, "waffenschein", "Waffenschein"),
    ANGELSCHEIN(3, "angelschein", "Angelschein"),
    ERSTE_HILFE(4, "erste_hilfe", "Erste Hilfe");

    private final int id;
    private final String dbName;
    private final String name;

    Licenses(int id, String dbName, String name) {
        this.id = id;
        this.dbName = dbName;
        this.name = name;
    }

    public int getID() {
        return this.id;
    }

    public String getDBName() {
        return this.dbName;
    }

    public String getName() {
        return this.name;
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT personalausweis, waffenschein, fuehrerschein, angelschein, erste_hilfe FROM licenses WHERE id=" + id)) {
            if (rs.next()) {
                cache.put(Licenses.PERSONALAUSWEIS, rs.getBoolean("personalausweis"));
                cache.put(Licenses.FUEHRERSCHEIN, rs.getBoolean("fuehrerschein"));
                cache.put(Licenses.WAFFENSCHEIN, rs.getBoolean("waffenschein"));
                cache.put(Licenses.ANGELSCHEIN, rs.getBoolean("angelschein"));
                cache.put(Licenses.ERSTE_HILFE, rs.getBoolean("erste_hilfe"));
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
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