package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum Token {
    PERSONALAUSWEIS(0, "personalausweis", 0, 1),
    ACCOUNT_RESET(1, "account_reset", 0, 1);

    private final int id;
    private final String name;
    private final int default_amount;
    private final int default_premium_amount;
    Token(int id, String name, int default_amount, int default_premium_amount) {
        this.id = id;
        this.name = name;
        this.default_amount = default_amount;
        this.default_premium_amount = default_premium_amount;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDefaultPremiumAmount() {
        return this.default_premium_amount;
    }

    public int getDefaultAmount() {
        return this.default_amount;
    }

    public int getDefaultValue(Player p) {
        return (Premium.hasPremium(p)?this.default_premium_amount:this.default_amount);
    }

    public int get(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                return rs.getInt(this.getName());
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return getDefaultValue(Script.getPlayer(id));
    }

    public void add(int id, int amount) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                Script.executeAsyncUpdate("UPDATE change_token SET " + this.getName() + "=" + (rs.getInt(this.getName()) + amount) + " WHERE id=" + id);
            } else {
                Script.executeAsyncUpdate("INSERT INTO change_token (id, " + this.getName() + ") VALUES (" + id + ", " + (1 + amount) + ");");
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void remove(int id, int amount) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + this.getName() + " FROM change_token WHERE id=" + id)) {
            if (rs.next()) {
                int i = (rs.getInt(this.getName()) - amount);
                if (i < 0) i = 0;
                Script.executeAsyncUpdate("UPDATE change_token SET " + this.getName() + "=" + i + " WHERE id=" + id);
            } else {
                Script.executeAsyncUpdate("INSERT INTO change_token (id, " + this.getName() + ") VALUES (" + id + ", " + (1 - amount) + ");");
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
    }
}
