package de.newrp.Administrator;

import de.newrp.API.Script;
import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Straflog {

    public Straflog(OfflinePlayer user, Player admin, Punish.Violation punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement statement = Main.getConnection().prepareStatement(
                    "INSERT INTO straflog (userID, adminID, punishmentID, time) VALUES (?, ?, ?, ?);"
            )) {
                statement.setInt(1, Script.getNRPID(user));
                statement.setInt(2, Script.getNRPID(admin));
                statement.setInt(3, punishment.getID());
                statement.setLong(4, System.currentTimeMillis());

                statement.execute();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

    }
}
