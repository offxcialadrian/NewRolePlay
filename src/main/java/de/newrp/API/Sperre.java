package de.newrp.API;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import de.newrp.NewRoleplayMain;

public enum Sperre {

    PERSONALAUSWEIS(1, "personalausweis", true),
    MUTE(2, "mutesperre", true),
    TIMEBAN(3, "timeban", true),
    FRAKTIONSSPERRE(4, "fraktionsperre", false),
    WAFFENSPERRE(5, "waffensperre", true),
    ADSPERRE(6, "adsperre", false),
    TRAGENSPERRE(7, "tragensperre", true);

    private final int id;
    private final String database;
    private final boolean active;

    Sperre(int id, String database, boolean active) {
        this.id = id;
        this.database = database;
        this.active = active;
    }

    public int getID() {
        return this.id;
    }

    public String getDatabaseName() {
        return this.database;
    }

    public boolean active() {
        return this.active;
    }

    public void setSperre(int p, long min) {
        long current = System.currentTimeMillis();
        long time = (current + ((min * 60) * 1000));
        HashMap<Sperre, Long> sperren = getSperren(p);
        sperren.put(this, time);
        if (this == TIMEBAN) {
            Script.executeUpdate("INSERT INTO " + this.database + " (id, time) VALUES (" + p + ", " + time + ") ON DUPLICATE KEY UPDATE id='" + p + "', time='" + time + "'");
        } else {
            Script.executeAsyncUpdate("INSERT INTO " + this.database + " (id, time) VALUES (" + p + ", " + time + ") ON DUPLICATE KEY UPDATE id='" + p + "', time='" + time + "'");
        }

    }

    public boolean isActive(int p) {
        if (getTime(p) <= 0) return false;
        long sperre = getTime(p);
        long time = System.currentTimeMillis();
        if (sperre > time) {
            return true;
        } else {
            remove(p);
            return false;
        }
    }

    @SuppressWarnings("SqlResolve")
    public static HashMap<Sperre, Long> getSperren(int id) {
        HashMap<Sperre, Long> sperren = new HashMap<>();
        for (Sperre sperre : Sperre.values()) {
            sperren.put(sperre, 0L);
            if (sperre.active()) {
                try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT time FROM " + sperre.getDatabaseName() + " WHERE id=" + id)) {
                    if (rs.next()) {
                        sperren.put(sperre, (rs.getLong("time") > System.currentTimeMillis() ? rs.getLong("time") : 0L));
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sperren;
    }

    public long getTime(int id) {
        HashMap<Sperre, Long> sperren = getSperren(id);
        return sperren.get(this);
    }

    public void remove(int p) {
        Script.executeUpdate("DELETE FROM " + this.database + " WHERE id=" + p);
        HashMap<Sperre, Long> sperren = getSperren(p);
        sperren.put(this, 0L);
    }
}
