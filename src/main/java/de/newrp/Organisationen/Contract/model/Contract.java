package de.newrp.Organisationen.Contract.model;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Contract {

    public static final String PREFIX = "§8[§6Contract§8] §6" + Messages.ARROW + " §7";
    public static final long COOLDOWN = TimeUnit.DAYS.toMillis(3);

    public static List<Contract> CONTRACTS = new ArrayList<>();
    public static List<Integer> CUSTOMER = new ArrayList<>();

    public static HashMap<String, Contract> offers = new HashMap<>();

    private int userID;
    private int price;
    private long time;
    private boolean active;

    public Contract(int userID, int price, long time, boolean active) {
        this.userID = userID;
        this.price = price;
        this.time = time;
        this.active = active;
    }

    public static List<Contract> getContacts() {
        return CONTRACTS;
    }

    public static int getAmount() {
        int i = 0;
        for (Contract ct : Contract.getContacts()) if (ct.isActive()) i++;
        return i;
    }

    public static boolean hasContract(OfflinePlayer player) {
        int id = Script.getNRPID(player);
        for (Contract ct : CONTRACTS)
            if (ct.getUserID() == id)
                if (ct.isActive()) return true;
        return false;
    }

    public static Contract getContract(OfflinePlayer player) {
        int id = Script.getNRPID(player);
        for (Contract ct : CONTRACTS)
            if (ct.getUserID() == id)
                if (ct.isActive()) return ct;
        return null;
    }

    public static Contract create(OfflinePlayer player, int price) {
        return new Contract(Script.getNRPID(player), price, System.currentTimeMillis(), true);
    }

    public static void add(Contract ct) {
        Script.executeAsyncUpdate("INSERT INTO contract VALUES (" + ct.getUserID() + ", " + ct.getPrice() + ", " + ct.getTime() + ", " + ct.isActive() + ")");
        CONTRACTS.add(ct);
    }

    public static void remove(Contract ct) {
        Script.executeAsyncUpdate("UPDATE contract SET active=" + false +  " WHERE userID=" + ct.getUserID());
        ct.setActive(false);
    }

    public static void addOffer(Player player, Contract ct) {
        offers.put(player.getName(), ct);
    }

    public static Contract getOffer(Player player) {
        return offers.get(player.getName());
    }

    public static void removeOffer(Player player) {
        offers.remove(player.getName());
    }

    public static boolean wasCustomer(Player player) {
        return CUSTOMER.contains(Script.getNRPID(player));
    }

    public static void addCustomer(Player player) {
        CUSTOMER.add(Script.getNRPID(player));
    }

    public static void load() {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            List<Contract> contract = new ArrayList<>();
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM contract")) {
                while (rs.next()) {
                    int userID = rs.getInt("userID");
                    int price = rs.getInt("price");
                    long time = rs.getLong("time");
                    boolean active = rs.getBoolean("active");
                    contract.add(new Contract(userID, price, time, active));
                }
                CONTRACTS = contract;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
