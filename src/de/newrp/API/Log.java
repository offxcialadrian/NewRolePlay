package de.newrp.API;

import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum Log {

    LOW(1),
    NORMAL(2),
    HIGH(3),
    WARNING(4);

    private final int importance;

    Log(int importance) {
        this.importance = importance;
    }

    public int getImportance() {
        return this.importance;
    }

    public void write(Player p, String log) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO log (id, nrp_id, log, importance, time) VALUES (NULL, ?, ?, ?, NOW());")) {
                stmt.setInt(1, Script.getNRPID(p));
                stmt.setString(2, log);
                stmt.setInt(3, getImportance());
                stmt.execute();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
    }

    public void write(OfflinePlayer p, String log) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO log (id, nrp_id, log, importance, time) VALUES (NULL, ?, ?, ?, NOW());")) {
                stmt.setInt(1, Script.getNRPID(p));
                stmt.setString(2, log);
                stmt.setInt(3, getImportance());
                stmt.execute();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
    }

    public void write(String log) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO log (id, nrp_id, log, importance, time) VALUES (NULL, NULL, ?, ?, NOW());")) {
                stmt.setString(1, log);
                stmt.setInt(2, getImportance());
                stmt.execute();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
    }
}
