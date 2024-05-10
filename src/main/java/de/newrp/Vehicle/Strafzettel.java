package de.newrp.Vehicle;

import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Getter
public class Strafzettel {

    public static HashMap<Player, String> reasons = new HashMap<>();
    public static HashMap<Player, Integer> prices = new HashMap<>();

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
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT car_id, betrag, grund, cop_id FROM strafzettel WHERE id = ? ")) {
            stmt.setInt(1, carID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Strafzettel(rs.getInt("id"), rs.getString("betrag").replaceAll("\"", ""), rs.getInt("grund"), rs.getInt("cop"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveStrafzettel(int carID, String reason, int price, int copID) {
        Script.executeAsyncUpdate("INSERT INTO strafzettel (car_id, betrag, grund, cop_id) VALUES (" + carID + ", " + price + ", \"" + reason + "\", " + copID + ")");
    }

    public static boolean isTicketing(Player player) {
        return reasons.containsKey(player);
    }
}
