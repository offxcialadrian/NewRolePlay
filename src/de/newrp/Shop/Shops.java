package de.newrp.Shop;

import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public enum Shops {

    SH_CAFE(0, "Cafe", "Café Stadthalle", 25000, new Location(Script.WORLD, 626, 68, 1030), 200, 600, true, ShopType.CAFE),
    TEST(1, "Café am X3", "Café am X3", 50000, new Location(Script.WORLD, 754, 72, 924), 200, 600, true, ShopType.CAFE),
    IKEA(2, "AEKI", "AEKI", 50000, new Location(Script.WORLD, 673, 68, 898), 200, 600, true, ShopType.HAUSADDON);

    private final int id;
    private final String name;
    private final String publicname;
    private final int preis;
    private final Location buy;
    private final int rent;
    private final int running_cost;
    private final boolean lager;
    private final ShopType type;

    Shops(int id, String name, String publicname, int preis, Location buy, int rent, int running_cost, boolean lager, ShopType type) {
        this.id = id;
        this.name = name;
        this.publicname = publicname;
        this.preis = preis;
        this.rent = rent;
        this.running_cost = running_cost;
        this.buy = buy;
        this.lager = lager;
        this.type = type;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.preis;
    }

    public String getPublicName() {
        return this.publicname;
    }

    public int getRent() {
        return this.rent;
    }

    public int getRunningCost() {
        return this.running_cost;
    }

    public boolean hasLager() {
        return this.lager;
    }

    public int getKasse() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT kasse FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("kasse");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    public ShopType getType() {
        return this.type;
    }

    public static Shops getShop(int id) {
        for(Shops shop : Shops.values()) {
            if(shop.getID() == id) {
                return shop;
            }
        }
        return null;
    }

    public void addKasse(int i) {
        int k = (getKasse() + i);
        Script.executeUpdate("UPDATE shops SET kasse=" + k + " WHERE shopID=" + this.id);
    }

    public void removeKasse(int i) {
        int n = (getKasse() - i);
        if (n < 0) n = 0;
        Script.executeUpdate("UPDATE shops SET kasse=" + n + " WHERE shopID=" + this.id);
    }

    public int getLagerSize() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT lager_max FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("lager_max");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    public void addLagerSize(int i) {
        Script.executeUpdate("UPDATE shops SET lager_max=" + (getLagerSize() + i) + " WHERE shopID=" + this.id);
    }

    public int getLager() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT lager FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("lager");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    public int getOwner() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ownerID FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("ownerID");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    public void setOwner(int id) {
        Script.executeUpdate("UPDATE shops SET ownerID=" + id + " WHERE shopID=" + this.id);
    }

    public void addLager(int i) {
        int n = (getLager() + i);
        if (n > getLagerSize()) n = getLagerSize();
        Script.executeUpdate("UPDATE shops SET lager=" + n + " WHERE shopID=" + this.id);
    }

    public void removeLager(int i) {
        int n = (getLager() - i);
        if (n < 0) n = 0;
        Script.executeUpdate("UPDATE shops SET lager=" + n + " WHERE shopID=" + this.id);
    }

    public Location getLocation() {
        return this.buy;
    }

    public Location getBuyLocation() {
        return this.buy;
    }

    public static HashMap<Integer, int[]> getShopItemData(Shops s) {
        HashMap<Integer, int[]> c = new HashMap<>();
        try (
                Statement stmt = main.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT amount, price, itemID FROM shopprice WHERE shopID=" + s.getID())) {
            while (rs.next()) {
                c.put(rs.getInt("itemID"), new int[]{rs.getInt("amount"), rs.getInt("price")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public HashMap<Integer, ItemStack> getItems() {
        HashMap<Integer, ItemStack> l = new HashMap<>();
        HashMap<Integer, int[]> c = getShopItemData(this);
        for (Map.Entry<Integer, int[]> n : c.entrySet()) {
            ShopItem bi = ShopItem.getItem(n.getKey());
            ItemStack i = bi.getItemStack();
            int[] a = n.getValue();
            i.setAmount(a[0]);
            l.put(bi.getID(), Script.setNameAndLore(i, bi.getName(), "§8× §6" + a[1] + "€"));
        }
        return l;
    }

}
