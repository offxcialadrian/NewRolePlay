package de.newrp.Vehicle;

import de.newrp.NewRoleplayMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Strafzettel {

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
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT id, reason, preis, cop FROM strafzettel WHERE id = ? ")) {
            stmt.setInt(1, carID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Strafzettel(rs.getInt("id"), rs.getString("reason"), rs.getInt("preis"), rs.getInt("cop"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCarID() {
        return carID;
    }

    public String getReason() {
        return reason;
    }

    public int getPrice() {
        return price;
    }

    public int getCopID() {
        return copID;
    }
}
