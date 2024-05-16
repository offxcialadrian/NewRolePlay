package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VertragAPI {


    private int id;
    private int from;
    private int to;
    private String bedingung;
    private long time;

    public VertragAPI(int id, int from, int to, String bedingung, long time) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.bedingung = bedingung;
        this.time = time;
    }

    public int getID() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public String getBedingung() {
        return bedingung;
    }

    public long getTime() {
        return time;
    }

    public static VertragAPI getVertrag(int id) {
        int from;
        int to;
        String bedingung;
        long time;
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT * FROM vertrag WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                from = rs.getInt("userID_from");
                to = rs.getInt("userID_to");
                bedingung = rs.getString("bedingung");
                time = rs.getLong("time");
                return new VertragAPI(id, from, to, bedingung, time);
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<VertragAPI> getVertraege(Player p) {
        List<VertragAPI> list = new ArrayList<>();
        int from;
        int to;
        String bedingung;
        long time;
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT * FROM vertrag WHERE userID_from = ? OR userID_from = ? ORDER BY id DESC")) {
            statement.setInt(1, Script.getNRPID(p));
            statement.setInt(2, Script.getNRPID(p));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                from = rs.getInt("userID_from");
                to = rs.getInt("userID_to");
                bedingung = rs.getString("bedingung");
                time = rs.getLong("time");
                list.add(new VertragAPI(rs.getInt("id"), from, to, bedingung, time));
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}


