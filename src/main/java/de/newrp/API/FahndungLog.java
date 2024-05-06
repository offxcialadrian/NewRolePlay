package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FahndungLog {

    public FahndungLog(OfflinePlayer user, Player admin, int amount) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                    "INSERT INTO wantedlog (userID, copID, amount, time) VALUES (?, ?, ?, ?);"
            )) {
                statement.setInt(1, Script.getNRPID(user));
                statement.setInt(2, Script.getNRPID(admin));
                statement.setInt(3, amount);
                statement.setLong(4, System.currentTimeMillis());

                statement.execute();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

}
