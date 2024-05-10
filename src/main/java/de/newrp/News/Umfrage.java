package de.newrp.News;

import de.newrp.API.Debug;
import de.newrp.NewRoleplayMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Umfrage {

    private int id;
    private String frage;
    private HashMap<String, Integer> antworten;
    private boolean active;

    public static ArrayList<String> players = new ArrayList<>();


    public Umfrage(int id, String frage, HashMap<String, Integer> antworten, boolean active) {
        this.id = id;
        this.frage = frage;
        this.antworten = antworten;
        this.active = active;
    }

    public int getID() {
        return id;
    }

    public String getFrage() {
        return frage;
    }

    public HashMap<String, Integer> getAntworten() {
        return antworten;
    }

    public boolean isActive() {
        return active;
    }

    public static Umfrage getActiveUmfrage() {
        int id;
        String frage;
        HashMap<String, Integer> antworten = new HashMap<>();
        boolean active;
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT * FROM umfragen WHERE active = 1")) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
                frage = rs.getString("frage");
                active = rs.getBoolean("active");
                try (PreparedStatement statement2 = NewRoleplayMain.getConnection().prepareStatement(
                        "SELECT * FROM umfragen_antworten WHERE umfrageID = ? ORDER BY id ASC")) {
                    statement2.setInt(1, id);
                    ResultSet rs2 = statement2.executeQuery();
                    while (rs2.next()) {
                        antworten.put(rs2.getString("antwort"), rs2.getInt("votes"));
                    }
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
            return null;
        }
        return new Umfrage(id, frage, antworten, active);
    }

    public static void endUmfrage(Umfrage u) {
        players.clear();
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "UPDATE umfragen SET active = 0 WHERE id = ?")) {
            statement.setInt(1, u.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
    }

    public static void createUmfrage(String frage, String[] antworten) {
        for (int i = 0; i < antworten.length; i++) {
            antworten[i] = antworten[i].substring(1);
        }
        try {
            String insertUmfrageQuery = "INSERT INTO umfragen (frage, active) VALUES (?, 1)";
            try (PreparedStatement umfrageStatement = NewRoleplayMain.getConnection().prepareStatement(insertUmfrageQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                umfrageStatement.setString(1, frage);
                int umfrageAffectedRows = umfrageStatement.executeUpdate();
                if (umfrageAffectedRows == 0) {
                    throw new SQLException("Creating Umfrage failed, no rows affected.");
                }

                try (ResultSet generatedKeys = umfrageStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int umfrageID = generatedKeys.getInt(1);

                        String insertAntwortQuery = "INSERT INTO umfragen_antworten (umfrageID, antwort, votes) VALUES (?, ?, 0)";
                        try (PreparedStatement antwortStatement = NewRoleplayMain.getConnection().prepareStatement(insertAntwortQuery)) {
                            for (String antwort : antworten) {
                                antwortStatement.setInt(1, umfrageID);
                                antwortStatement.setString(2, antwort);
                                antwortStatement.executeUpdate();
                            }
                        }
                    } else {
                        throw new SQLException("Creating Umfrage failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
    }

    public void vote(String antwort) {
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "UPDATE umfragen_antworten SET votes = votes + 1 WHERE umfrageID = ? AND antwort = ?")) {
            statement.setInt(1, id);
            statement.setString(2, antwort);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
