package de.newrp.API;

import de.newrp.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum Token {
    PERSONALAUSWEIS(0, "personalausweis", 1),
    ACCOUNT_RESET(1, "account_reset", 0);

    private final int id;
    private final String name;
    private final int default_amount;

    Token(int id, String name, int default_amount) {
        this.id = id;
        this.name = name;
        this.default_amount = default_amount;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDefaultValue() {
        return this.default_amount;
    }

    public int get(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                return rs.getInt(this.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.default_amount;
    }

    public void add(int id, int amount) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                Script.executeAsyncUpdate("UPDATE change_token SET " + this.getName() + "=" + (rs.getInt(this.getName()) + amount) + " WHERE id=" + id);
            } else {
                Script.executeAsyncUpdate("INSERT INTO change_token (id, " + this.getName() + ") VALUES (" + id + ", " + (1 + amount) + ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id, int amount) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                int i = (rs.getInt(this.getName()) - amount);
                if (i < 0) i = 0;
                Script.executeAsyncUpdate("UPDATE change_token SET " + this.getName() + "=" + i + " WHERE id=" + id);
            } else {
                Script.executeAsyncUpdate("INSERT INTO change_token (id, " + this.getName() + ") VALUES (" + id + ", " + (1 - amount) + ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
