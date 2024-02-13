package de.newrp.Shop;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Script;
import de.newrp.House.HouseAddon;
import de.newrp.Waffen.Weapon;
import de.newrp.main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum ShopItem {

    BROT(0, "§fBrot", new ItemBuilder(Material.BREAD).setAmount(16).build(), 5, 1, 20, 2, 2400, true, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    KAFFEE(1, "§fKaffee", new ItemStack(Material.POTION, 1), 1, 1, 20, 2, 3900, true, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    LOTTOSCHEIN(2, "§7Lottoschein", new ItemStack(Material.PAPER), 1, 1, 20, 30, 1000, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.NEWS}),
    HAUSKASSE(3, "§7Hauskasse", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.HAUSKASSE.getPrice(), 34000, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    MIETERSLOT(4, "§7Mieterslot", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.SLOT.getPrice(), 39500, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    WAFFENSCHRANK(5, "§7Waffenschrank", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.WAFFENSCHRANK.getPrice(), 39500, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    ALARMANLAGE(7, "§7Alarmanlage", new ItemStack(Material.REDSTONE), 20, 1, 20, HouseAddon.ALARM.getPrice(), 39500, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    KUEHLSCHRANK(9, "§7Kühlschrank", new ItemStack(Material.CHEST), 20, 1, 20, HouseAddon.KUEHLSCHRANK.getPrice(), 39500, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    PISTOLE(10, "§7Pistole", new ItemStack(Material.IRON_HORSE_ARMOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_9MM(11, "§89mm Munition", new ItemBuilder(Material.ARROW).setAmount(Weapon.PISTOLE.getMagazineSize()).build(), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AK47(12, "§7AK-47", new ItemStack(Material.DIAMOND_HORSE_ARMOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    HEISSE_SCHOKOLADE(43, "§rHeiße Schokolade", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.CAFE}),
    FILTERKAFFEE(44, "§rFilterkaffee", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.CAFE}),
    LATTE_MACCHIATO(45, "§rLatte Macchiato", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.CAFE}),
    ESPRESSO(46, "§rEspresso", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.CAFE}),
    CRAPPUCHINO(47, "§rCrappuchino", new ItemStack(Material.FLOWER_POT), 1, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.CAFE}),
    Zeitung(48, "§9Zeitung", de.newrp.News.Zeitung.zeitung, 1, 2, 25, 30, 3000, true, true, false, new ShopType[] {ShopType.CAFE, ShopType.SUPERMARKET, ShopType.NEWS}),
    SCHMERZMITTEL(49, "§fSchmerzmittel", new ItemStack(Material.PAPER), 5, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.PHARMACY, ShopType.SUPERMARKET}),
    BASEBALLSCHLAEGER(50, "§7Baseballschläger", new ItemStack(Material.BONE), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SAMSUNG_HANDY(52, "§cGalaxy S21", new ItemStack(Material.IRON_INGOT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    HUAWEI_HANDY(53, "§cP60", new ItemStack(Material.IRON_INGOT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    GOOGLE_HANDY(54, "§cPixel 10", new ItemStack(Material.IRON_INGOT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    APPLE_HANDY(55, "§ciPhone 15", new ItemStack(Material.IRON_INGOT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    MAP(56, "§7Karte", new ItemStack(Material.MAP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.NEWS}),
    VERBAND(57, "§7Verband", new ItemStack(Material.PAPER), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.PHARMACY}),
    TRINKWASSER(58, "§7Trinkwasser", new ItemStack(Material.POTION), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.CAFE, ShopType.NEWS}),
    AMMO_762MM(59, "§7.762mm Munition", new ItemBuilder(Material.ARROW).setAmount(Weapon.AK47.getMagazineSize()).build(), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    KEVLAR(51, "§7Schutzweste", Script.kevlar(1), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SCHMERZMITTEL_HIGH(60, "§fSchmerzmittel (High)", new ItemStack(Material.PAPER), 10, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    ANTIBIOTIKA(61, "§fAntibiotika", new ItemStack(Material.PAPER), 5, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    HUSTENSAFT(62, "§fHustensaft", new ItemStack(Material.PAPER), 5, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    ENTZUENDUNGSHEMMENDE_SALBE(63, "§fEntzündungshemmende Salbe", new ItemStack(Material.PAPER), 5, 2, 25, 2, 3000, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    WINGSUIT(64, "§7Fallschirm", new ItemStack(Material.ELYTRA), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    CD_1(65, "§6Gold", new ItemStack(Material.MUSIC_DISC_11), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_2(66, "§6Green", new ItemStack(Material.MUSIC_DISC_13), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_3(67, "§6Blocks", new ItemStack(Material.MUSIC_DISC_BLOCKS), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_4(68, "§6Chirp", new ItemStack(Material.MUSIC_DISC_CHIRP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_5(69, "§6Far", new ItemStack(Material.MUSIC_DISC_FAR), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_6(70, "§6Mall", new ItemStack(Material.MUSIC_DISC_MALL), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_7(71, "§6Mellohi", new ItemStack(Material.MUSIC_DISC_MELLOHI), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_8(72, "§6Stal", new ItemStack(Material.MUSIC_DISC_STAL), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_9(73, "§6Strad", new ItemStack(Material.MUSIC_DISC_STRAD),   1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_10(74, "§6Ward", new ItemStack(Material.MUSIC_DISC_WARD), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_11(75, "§6Wait", new ItemStack(Material.MUSIC_DISC_WAIT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    ROSE(76, "§7Rose", new ItemStack(Material.WITHER_ROSE), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    TULPE(77, "§7Tulpe", new ItemStack(Material.RED_TULIP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE(78, "§7Margarite", new ItemStack(Material.OXEYE_DAISY), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    GERBERA(79, "§7Gerbera", new ItemStack(Material.ORANGE_TULIP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    LILIEN(80, "§7Lilien", new ItemStack(Material.WHITE_TULIP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE_PINK(81, "§7Pink Margarite", new ItemStack(Material.PINK_TULIP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    WATER_BUCKET(82, "§7Wasser", Script.setNameAndLore(new ItemStack(Material.WATER_BUCKET), "§9Wasser", "§65/5"), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.FLOWER}),
    DUENGER(83, "§7Dünger", new ItemStack(Material.INK_SAC), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.FLOWER}),
    EINZELZIMMER(84, "§7Einzelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DOPPELZIMMER(85, "§7Doppelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    MEHRBETTZIMMER(86, "§7Mehrbettzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    SUITE(87, "§7Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EXECUTIVE_SUITE(88, "§7Executive Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DELUXE_ZIMMER(89, "§7Deluxe Zimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    PRAESIDENTEN_SUITE(90, "§7Präsidenten Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EINZELFAHRASUSWEIS(91, "§6UBahn-Ticket", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [Einzelfahrausweis]").setLore("Verbleibende Fahrten: 1").build(), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.NEWS}),
    WOCHENFAHRASUSWEIS(92, "§6UBahn-Ticket", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [7 Fahrten]").setLore("Verbleibende Fahrten: 7").build(), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.NEWS}),
    MONATSFAHRASUSWEIS(93, "§6UBahn-Ticket", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [30 Fahrten]").setLore("Verbleibende Fahrten: 30").build(), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.NEWS});

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
    private final boolean premium;
    private ShopType[] types;

    ShopItem(int id, String name, ItemStack is, int size, int min, int max, int buyPrice, int licensePrice, boolean reopen, boolean addtoinv, boolean premium, ShopType[] types) {
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
        this.premium = premium;
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
        if (is == null) return null;
        if (!is.hasItemMeta()) return null;
        if (!is.getItemMeta().hasDisplayName()) return null;

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

    public boolean premiumNeeded() {
        return this.premium;
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
