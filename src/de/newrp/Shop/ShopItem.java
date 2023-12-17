package de.newrp.Shop;

import de.newrp.House.HouseAddon;
import de.newrp.main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum ShopItem {

    BROT(0, "§fBrot", new ItemStack(Material.BREAD), 16, 1, 20, 2, 2400, true, true, new ShopType[] {ShopType.CAFE}),
    KAFFEE(1, "§fKaffee", new ItemStack(Material.POTION, 1, (short) 16421), 1, 1, 20, 2, 3900, true, true, new ShopType[] {ShopType.CAFE}),
    LOTTOSCHEIN(2, "§7Lottoschein", new ItemStack(Material.PAPER), 1, 1, 20, 30, 1000, false, true, new ShopType[] {ShopType.CAFE}),
    HAUSKASSE(3, "§7Hauskasse", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.HAUSKASSE.getPrice(), 34000, false, false, new ShopType[] {ShopType.HAUSADDON}),
    MIETERSLOT(4, "§7Mieterslot", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.SLOT.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON});

    private final int id;
    private final String name;
    private final ItemStack is;
    private final int size;
    private final int min;
    private final int max;
    private final int buyPrice;
    private final int licensePrice;
    private final boolean reopen;
    private final boolean addtoinv;
    private ShopType[] types;

    ShopItem(int id, String name, ItemStack is, int size, int min, int max, int buyPrice, int licensePrice, boolean reopen, boolean addtoinv, ShopType[] types) {
        this.id = id;
        this.name = name;
        this.is = is;
        this.size = size;
        this.min = min;
        this.max = max;
        this.buyPrice = buyPrice;
        this.licensePrice = licensePrice;
        this.reopen = reopen;
        this.addtoinv = addtoinv;
        this.types = types;
    }

    public static ItemStack getItemStack(int id) {
        for (ShopItem i : values()) {
            if (i.getID() == id) return i.getItemStack();
        }
        return null;
    }

    public static ShopItem getItem(int id) {
        for (ShopItem i : values()) {
            if (i.getID() == id) return i;
        }
        return null;
    }

    public boolean isReopen() {
        return reopen;
    }

    public int getLicensePrice() {
        return licensePrice;
    }

    public static ShopItem getShopItem(ItemStack is) {
        for (ShopItem a : values()) {
            if (a.getItemStack().isSimilar(is)) return a;
        }

        String displayName = is.getItemMeta().getDisplayName();
        if (is.getItemMeta().hasDisplayName()) {
            for (ShopItem a : values()) {
                ItemStack businessItem = a.getItemStack();
                if (businessItem == null) continue;

                ItemMeta itemMeta = businessItem.getItemMeta();
                if (itemMeta == null) continue;
                if (!itemMeta.hasDisplayName()) continue;

                if (businessItem.getItemMeta().getDisplayName().equals(displayName)) return a;
            }
        }

        return null;
    }

    public ShopType[] getShopTypes() {
        return types;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItemStack() {
        return this.is;
    }

    public int getSize() {
        return this.size;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }

    public boolean addToInventory() {
        return this.addtoinv;
    }

    public void setPrice(Shops b, int price) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE shopprice SET price=" + price + " WHERE shopID=" + b.getID() + " AND itemID=" + getID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAmount(Shops b) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT amount FROM shopprice WHERE shopID=" + b.getID() + " AND itemID=" + getID())) {
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPrice(Shops b) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT price FROM shopprice WHERE shopID=" + b.getID() + " AND itemID=" + getID())) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ShopItem getShopItem(String name) {
        for (ShopItem i : values()) {
            if (i.getName().equalsIgnoreCase(name)) return i;
        }
        return null;
    }


}
