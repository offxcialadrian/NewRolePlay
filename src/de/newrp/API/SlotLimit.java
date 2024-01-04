package de.newrp.API;

import de.newrp.main;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public enum SlotLimit {

    HOUSE(0, 1, 2, "houselimit", "houselimit"),
    VEHICLE(1, 1, 2, "carlimit", "carlimit"),
    SHOP(2, 1, 2, "shoplimit", "shoplimit");

    private final int id;
    private final int defaultAmount;
    private final int defaultAmountPremium;
    private final String dbName;
    private final String colName;

    SlotLimit(int id, int defaultAmount, int defaultAmountPremium, String dbName, String colName) {
        this.id = id;
        this.defaultAmount = defaultAmount;
        this.defaultAmountPremium = defaultAmountPremium;
        this.dbName = dbName;
        this.colName = colName;
    }

    public int getID() {
        return this.id;
    }

    public int getDefaultAmount(boolean premium) {
        return (premium ? this.defaultAmountPremium : this.defaultAmount);
    }

    public int get(int id) {
        HashMap<SlotLimit, Integer> map = getSlotLimits(id);
        Debug.debug(map.get(this));
        return map.get(this);
    }

    public void set(int id, int amount) {
        Script.executeAsyncUpdate("INSERT INTO " + this.dbName + " (id, " + this.colName + ") VALUES (" + id + ", " + amount + ") ON DUPLICATE KEY " +
                "UPDATE " + this.colName + " = " + amount);
    }

    public void add(int id) {
        Script.executeAsyncUpdate("INSERT INTO " + this.dbName + " (id, " + this.colName + ") VALUES (" + id + ", 1) ON DUPLICATE KEY " +
                "UPDATE " + this.colName + " = " + this.colName + " + 1");
    }

    public static HashMap<SlotLimit, Integer> getSlotLimits(int id) {
        boolean premium = Premium.hasPremium(Script.getPlayer(id));
        HashMap<SlotLimit, Integer> map = new HashMap<>();
        for (SlotLimit sl : SlotLimit.values()) {
            map.put(sl, 0);
        }
        int h = SlotLimit.HOUSE.getDefaultAmount(premium), c = SlotLimit.VEHICLE.getDefaultAmount(premium), s = SlotLimit.SHOP.getDefaultAmount(premium);
        try (PreparedStatement statement = main.getConnection().prepareStatement(
                "SELECT ( SELECT houselimit " +
                        "FROM houselimit WHERE id = ? ) AS house, ( SELECT carlimit FROM carlimit WHERE id = ?) AS car, ( SELECT petlimit FROM petlimit WHERE id = ? ) AS pet, ( SELECT shoplimit FROM shoplimit WHERE id = ? ) AS pet")) {
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.setInt(3, id);
            statement.setInt(4, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    h += rs.getInt("house");
                    c += rs.getInt("car");
                    s += rs.getInt("shop");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        map.put(SlotLimit.HOUSE, h);
        map.put(SlotLimit.VEHICLE, c);
        map.put(SlotLimit.SHOP, s);

        return map;
    }
}
