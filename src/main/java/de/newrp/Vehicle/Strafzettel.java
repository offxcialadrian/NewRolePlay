package de.newrp.Vehicle;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class Strafzettel {

    public static HashMap<Player, String> reasons = new HashMap<>();
    public static HashMap<Player, Integer> prices = new HashMap<>();
    public static List<Player> removes = new ArrayList<>();

    private final int carID;
    private final String reason;
    private final int price;
    private final int copID;

    public Strafzettel(int carID, String reason, int price, int copID) {
        this.carID = carID;
        this.reason = reason;
        this.price = price;
        this.copID = copID;
    }

    public static Strafzettel loadStrafzettel(int carID) {
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT car_id, betrag, grund, cop_id FROM strafzettel WHERE car_id = ? ")) {
            stmt.setInt(1, carID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Strafzettel(rs.getInt("car_id"), rs.getString("grund").replaceAll("\"", ""), rs.getInt("betrag"), rs.getInt("cop_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveStrafzettel(int carID, String reason, int price, int copID) {
        Script.executeAsyncUpdate("INSERT INTO strafzettel (car_id, betrag, grund, cop_id) VALUES (" + carID + ", " + price + ", \"" + reason + "\", " + copID + ")");
    }

    public static void deleteStrafzettel(int carID) {
        Script.executeAsyncUpdate("DELETE FROM strafzettel WHERE car_id=" + carID);
    }

    public static boolean isTicketing(Player player) {
        return reasons.containsKey(player);
    }

    public static boolean isRemoving(Player player) {
        return removes.contains(player);
    }
}
