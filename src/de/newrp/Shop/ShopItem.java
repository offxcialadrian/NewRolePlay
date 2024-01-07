package de.newrp.Shop;

import de.newrp.House.HouseAddon;
import de.newrp.News.Zeitung;
import de.newrp.Waffen.Weapon;
import de.newrp.main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum ShopItem {

    BROT(0, "§fBrot", new ItemStack(Material.BREAD), 16, 1, 20, 2, 2400, true, true, new ShopType[] {ShopType.SUPERMARKET}),
    KAFFEE(1, "§fKaffee", new ItemStack(Material.POTION, 1), 1, 1, 20, 2, 3900, true, true, new ShopType[] {ShopType.SUPERMARKET}),
    LOTTOSCHEIN(2, "§7Lottoschein", new ItemStack(Material.PAPER), 1, 1, 20, 30, 1000, false, true, new ShopType[] {ShopType.SUPERMARKET}),
    HAUSKASSE(3, "§7Hauskasse", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.HAUSKASSE.getPrice(), 34000, false, false, new ShopType[] {ShopType.HAUSADDON}),
    MIETERSLOT(4, "§7Mieterslot", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.SLOT.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON}),
    WAFFENSCHRANK(5, "§7Waffenschrank", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.WAFFENSCHRANK.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON}),
    ALARMANLAGE(7, "§7Alarmanlage", new ItemStack(Material.REDSTONE), 20, 1, 20, HouseAddon.ALARM.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON}),
    DROGENLAGER(8, "§7Drogenlager", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.DROGENLAGER.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON}),
    KUEHLSCHRANK(9, "§7Kühlschrank", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.KUEHLSCHRANK.getPrice(), 39500, false, false, new ShopType[] {ShopType.HAUSADDON}),
    PISTOLE(10, "§7Pistole", new ItemStack(Material.IRON_HORSE_ARMOR), 1, 1, 1, 1, 1, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_9MM(11, "§89mm Munition", new ItemStack(Material.ARROW), Weapon.PISTOLE.getMagazineSize(), 1, 1, 1, 1, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AK47(12, "§7AK-47", new ItemStack(Material.DIAMOND_HORSE_ARMOR), 1, 1, 1, 1, 1, false, false, new ShopType[] {ShopType.GUNSHOP}),
    HEISSE_SCHOKOLADE(43, "§rHeiße Schokolade", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE}),
    FILTERKAFFEE(44, "§rFilterkaffee", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE}),
    LATTE_MACCHIATO(45, "§rLatte Macchiato", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE}),
    ESPRESSO(46, "§rEspresso", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE}),
    CRAPPUCHINO(47, "§rCrappuchino", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE}),
    Zeitung(48, "§9Zeitung", de.newrp.News.Zeitung.zeitung, 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.CAFE, ShopType.SUPERMARKET}),
    SCHMERZMITTEL(49, "§fSchmerzmittel", new ItemStack(Material.PAPER), 1, 2, 25, 2, 3000, true, true, new ShopType[] {ShopType.APOTHEKE, ShopType.SUPERMARKET});

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
