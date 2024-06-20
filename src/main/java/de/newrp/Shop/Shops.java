package de.newrp.Shop;

import de.newrp.API.Debug;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.Hotel;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Shops {

    SH_CAFE(0, "Cafe", "Café Stadthalle", 35000, new Location(Script.WORLD, 626, 68, 1030), 3, 600, true, ShopType.CAFE, new Location(Script.WORLD, 627.5, 68, 1033.7, 179.1f, 0.7f)),
    CAFE(1, "Café am X3", "Café am X3", 20000, new Location(Script.WORLD, 754, 72, 924), 2, 600, true, ShopType.CAFE, new Location(Script.WORLD, 757.9, 72, 925.7, 90f, 0.1f)),
    IKEA(2, "AEKI", "AEKI", 120000, new Location(Script.WORLD, 681, 68, 902), 50, 50, true, ShopType.HOUSEADDON, new Location(Script.WORLD, 678, 68, 902.5, -90.2f, 1f)),
    AEKI_CAFE(3, "Café am AEKI", "Café am AEKI", 10000, new Location(Script.WORLD, 688, 138, 908), 2, 600, true, ShopType.CAFE, new Location(Script.WORLD, 688.9, 138, 912.9, -179.6f, 0.1f)),
    GUNSHOP(4, "Schmid und Schmiedt Waffenladen", "Schmid und Schmiedt Waffenladen", 219000, new Location(Script.WORLD, 453, 69, 929), 50, 600, true, ShopType.GUNSHOP, new Location(Script.WORLD, 457.5, 69, 929.5, 90.6f, 1.2f)),
    JUPITER(5, "Jupiter", "Jupiter", 80000, new Location(Script.WORLD, 865, 74, 964), 15, 600, true, ShopType.ELECTRONIC, new Location(Script.WORLD, 865.5, 74, 966.5, -180.2f, -0.1f)),
    SUPERMARKT(6, "Supermarkt", "Supermarkt", 149000, new Location(Script.WORLD, 640, 68, 861), 30, 600, true, ShopType.SUPERMARKET, new Location(Script.WORLD, 640.5, 68, 865.5, 180f, 0.5f)),
    MUSIK(7, "Musikladen", "Musikladen", 25000, new Location(Script.WORLD, 864, 74, 905), 0, 600, true, ShopType.MUSIC, new Location(Script.WORLD, 864.5, 74, 903.5, -0.1f, 2.4f)),
    FLOWER(8, "Blumenladen", "Blumenladen", 50000, new Location(Script.WORLD, 866, 74, 950), 12, 600, true, ShopType.FLOWER, new Location(Script.WORLD, 865.5, 74, 948.5, -0.3f, 0f)),
    APOTHEKE(9, "Apotheke", "Apotheke", 45000, new Location(Script.WORLD, 346, 76, 1079), 15, 600, true, ShopType.PHARMACY, new Location(Script.WORLD, 348.5, 76, 1079.5, -270f, -0.6f)),
    MOTEL99(10, "Motel 99", "Motel 99", 35000, new Location(Script.WORLD, 809, 64, 1221, -89.25004f, -1.3500005f), 0, 600, true, ShopType.HOTEL, new Location(Script.WORLD, 812.6, 64, 1221.5, 90.3f, 0.7f)),
    HOTEL_CALIFORNIA(11, "Hotel California", "Hotel California", 50000, new Location(Script.WORLD, 275, 66, 971, -90.44937f, 4.9502726f), 0, 600, false, ShopType.HOTEL, new Location(Script.WORLD, 279.6, 66, 971.5, 90.4f, -0.2f)),
    APOTHEKE_AEKI(12, "Apotheke am AEKI", "Apotheke am AEKI", 45000, new Location(Script.WORLD, 659, 68, 854, 88.650856f, 15.150019f), 15, 600, true, ShopType.PHARMACY, new Location(Script.WORLD, 659.5, 68, 851.4, -359.7f, -1.6f)),
    SHOP_GANG(13, "Gangshop", "Gangshop", 120000, new Location(Script.WORLD, 617, 65, 1277, 174.60092f, 15.899984f), 25, 600, true, ShopType.SUPERMARKET, new Location(Script.WORLD, 620.5, 65, 1276.5, 89.5f, -0.6f)),
    GYM(14, "Fitnessstudio", "Fitnessstudio", 60000, new Location(Script.WORLD, 460, 67, 743, 58.60189f, 21.110485f), 20, 600, true, ShopType.GYM, new Location(Script.WORLD, 458.6, 67, 744.5, 270.2f, 2.3f)),
    SHOE_MALL(15, "Schuhladen", "Schuhladen", 30000, new Location(Script.WORLD, 843, 74, 962, -226.53546f, 41.1285f), 0, 600, true, ShopType.SHOE_STORE, new Location(Script.WORLD, 843.5, 74, 959.5, -0.1f, -0.5f)),
    MUSICSTORE_HAFEN(16, "Musikladen am Hafen", "Musikladen am Hafen", 9000, new Location(Script.WORLD, 329, 77, 997, 148.47311f, 16.413198f), 0, 600, true, ShopType.MUSIC, new Location(Script.WORLD, 328.5, 76, 987, 68.5f, 1.6f)),
    WAFFENLADEN_GANG(18, "Waffenladen Gang", "Waffenladen Gang", 199000, new Location(Script.WORLD, 534, 65, 1314, -269.9749f, 90.0f), 35, 600, true, ShopType.GUNSHOP, new Location(Script.WORLD, 530.5, 65, 1314.9, -90.4f, 1.6f)),
    GEMUESE(19, "Gemüseladen", "Gemüseladen", 15000, new Location(Script.WORLD, 842, 74, 918, -356.24542f, 9.236951f), 5, 600, true, ShopType.GEMUESE, new Location(Script.WORLD, 842.5, 74, 921.5, -179.3f, 1f)),
    BUCHHANDLUNG(20, "Buchladen", "Buchladen", 29000, new Location(Script.WORLD, 841, 74, 907, -1.7087402f, 20.008091f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 842.5, 74, 910.5, 180.2f, 0.5f)),
    NEWS_GERICHT(21, "Newsstand am Gericht", "Newsstand am Gericht", 20000, new Location(Script.WORLD, 794, 72, 952, -271.5559f, 7.0643415f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 790.5, 72, 950.4, -179.9f, 0.5f)),
    NEWS_HAFEN(22, "Newsstand am Strand", "Newsstand am Strand", 5000, new Location(Script.WORLD, 604, 65, 689, -358.99744f, 16.507566f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 603.4, 65, 693.6, 90.6f, 1.5f)),
    NEWS_PROMENADE(23, "Newsstand an der Promenade", "Newsstand an der Promenade", 5000, new Location(Script.WORLD, 275, 66, 892, -1.6918945f, 6.6413584f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 274.5, 66, 896.5, -269.7f, 0f)),
    NEWS_UBAHN(24, "Newsstand an der U-Bahn", "Newsstand an der U-Bahn", 5000, new Location(Script.WORLD, 336, 52, 1143, -269.4386f, 12.283148f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 332.5, 52, 1142.5, -179.9f, 0.7f)),
    DOENER(25, "Dönerladen", "Dönerladen", 21000, new Location(Script.WORLD, 449, 65, 665, -270.5968f, 8.693133f), 0, 600, true, ShopType.FASTFOOD, new Location(Script.WORLD, 446.2, 65, 664.9, -89f, 0.4f)),
    ELEKTRO_GANG(27, "Elektroladen Gang", "Elektroladen Gang", 60000, new Location(Script.WORLD, 557, 64, 1270, -0.9939575f, 90.0f), 12, 600, true, ShopType.ELECTRONIC, new Location(Script.WORLD, 558.5, 64, 1272.4, 179.8f, 0.9f)),
    AUTODEALER(29, "Autohaus", "Autohaus", 130000, new Location(Script.WORLD, 411, 77, 1081, -180.1416f, 90.0f), 30, 600, false, ShopType.CARDEALER, new Location(Script.WORLD, 412.1, 77, 1079.4, -0.4f, 0.8f)),
    BLUMENLADEN_GYM(30, "Blumenladen Fitnessstudio", "Blumenladen Fitnessstudio", 55000, new Location(Script.WORLD, 517, 66, 774, -1.5904541f, 90.0f), 12, 600, true, ShopType.FLOWER, new Location(Script.WORLD, 518.1, 66, 776.8, 181f, 1.9f)),
    // HANKYS(34, "Hankys", "Hankys", 60000, new Location(Script.WORLD, 557, 64, 1270, -359.05908f, 12.675349f), 15, 600, true, ShopType.ELECTRONIC, new Location(Script.WORLD, 558.5, 64, 1272.4, 179.9f, 3.3f)),
    JAGDHUETTE(35, "Jagdhütte", "Jagdhütte", 30000, new Location(Script.WORLD, 476, 70, 1186, 89.78363f, 89.33273f), 0, 600, true, ShopType.JAGDHUETTE, new Location(Script.WORLD, 475.2, 70.1, 1187, 238.3f, 1.9f)),
    BUCHHANDLUNG_INNENSTADT(36, "Buchladen Innenstadt", "Buchladen Innenstadt", 29000, new Location(Script.WORLD, 663, 67, 824, -270.25092f, 24.689606f), 0, 600, true, ShopType.NEWS, new Location(Script.WORLD, 659.5, 67, 824.5, 269.9f, 1.9f)),
    ANGELLADEN(37, "Angelladen", "Angelladen", 7500, new Location(Script.WORLD, 766, 64, 780, 266.49414f, 89.59964f), 2, 600, true, ShopType.ANGELSHOP, new Location(Script.WORLD, 769.4, 64, 780.6, 89.5f, 1.3f)),
    BAECKEREI(38, "Bäckerei", "Bäckerei", 5000, new Location(Script.WORLD, 450, 66, 644, 90.62988f, 22.937416f), 3, 600, true, ShopType.CAFE, new Location(Script.WORLD, 446.5, 66, 644.1, -91.1f, 1.3f)),
    BURGERBRATER_SHOP(39, "Burgerladen", "Burgerladen", 30000, new Location(Script.WORLD, 460, 67, 771, -191.7402f, 6.5769224f), 0, 600, true, ShopType.FASTFOOD, new Location(Script.WORLD, 460.5, 67, 767.6, 0f, 1.2f)),
    BLUMENLADEN(40, "Blumenhandlung", "Blumenhandlung", 50000, new Location(Script.WORLD, 316, 76, 932, -181.94073f, 90.0f), 12, 600, true, ShopType.FLOWER, new Location(Script.WORLD, 318.5, 76, 934.5, 90.2f, 0.4f)),
    //BLUMENLADEN_GANG(41, "Blumenladen Gang", "Blumenladen Gang", 579000, new Location(Script.WORLD, 512, 65, 1319, -83.85142f, 12.219168f), 12, 600, true, ShopType.FLOWER),
    WHITE_LOUNGE(42, "White Lounge", "White Lounge", 30000, new Location(Script.WORLD, 707, 65, 1213, 0.0f, 0.0f), 0, 600, true, ShopType.SHISHA, new Location(Script.WORLD, 707.6, 65, 1211.3, -359f, 1.5f)),
    GAS_STATION_HOSPITAL(43, "Tankstelle am Krankenhaus", "Tankstelle am Krankenhaus", 100000, new Location(Script.WORLD, 434, 76, 1072, 0.0f, 0.0f), 20, 600, false, ShopType.GAS_STATION, new Location(Script.WORLD, 438.4, 77, 1073.4, 91.9f, 2.2f)),
    GAS_STATION_GANG(44, "Tankstelle Gang", "Tankstelle Gang", 100000, new Location(Script.WORLD, 819, 67, 1326, 0.0f, 0.0f), 20, 600, false, ShopType.GAS_STATION, new Location(Script.WORLD, 820.5, 67, 1323.3, 4f, 0.4f)),
    BAR(45, "Bar", "Bar", 30000, new Location(Script.WORLD, 704.5, 69, 859.5, 0.0f, 0.0f), 2, 600, true, ShopType.BAR, new Location(Script.WORLD, 702.1, 69, 863.9, 213.8f, 0.9f)),
    CLUB(46, "Club-Bar", "Club-Bar", 25000, new Location(Script.WORLD, 477.5, 67, 1285.5, 0.0f, 0.0f), 2, 600, true, ShopType.BAR, new Location(Script.WORLD, 478.4, 67.1, 1280.3, 285.3f, 1.4f)),
    ZOO(47, "Zoo", "Zoo", 35000, new Location(Script.WORLD, 66.5, 76, 694.5, 0.0f, 0.0f), 2, 600, true, ShopType.MERCH, new Location(Script.WORLD, 73.4, 66, 681.5, -90.0f, 1.4f)),
    // später erst MERCHANDISE(48, "Merchandise", "Merchandise", 30000, new Location(Script.WORLD, 806, 66, 727, 0.0f, 0.0f), 2, 600, true, ShopType.MERCH);
    PET(49, "Tierhandlung", "Tierhandlung", 60000, new Location(Script.WORLD, 593.5, 69, 1114.5, 16.8f, 90f), 50, 600, true, ShopType.PETS, new Location(Script.WORLD, 593.5, 69, 1118.5, 179.6f, 4.1f));


    private final int id;
    private final String name;
    private final int preis;
    private final Location buy;
    private final int rent;
    private final int running_cost;
    private final boolean lager;
    private final ShopType type;
    private final String publicname;
    private final Location npcLoc;

    Shops(int id, String name, String publicname, int preis, Location buy, int rent, int running_cost, boolean lager, ShopType type, Location npcLoc) {
        this.id = id;
        this.name = name;
        this.publicname = publicname;
        this.preis = preis;
        this.rent = rent;
        this.running_cost = running_cost;
        this.buy = buy;
        this.lager = lager;
        this.type = type;
        this.npcLoc = npcLoc;
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

    public Location getNpcLoc() {
        return this.npcLoc;
    }

    public int getRunningCost() {
        int runningcost = 0;
        HashMap<Integer, ItemStack> c = this.getItems();
        if (this.getType() != ShopType.HOTEL) {
            for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
                ItemStack is = n.getValue();
                if (is == null) continue;
                if (ShopItem.getShopItem(is) != null)
                    runningcost += ShopItem.getShopItem(is).getTax();
            }
            if (this.acceptCard()) runningcost += Math.round((float) this.getRent() / 2);
        } else {
            Hotel.Hotels hotel = Hotel.Hotels.getHotelByShop(this);
            assert hotel != null;
            for (Hotel.Rooms room : hotel.getRentedRooms()) {
                runningcost += room.getPrice() / 2;
            }
        }
        return runningcost;
    }

    public boolean hasLager() {
        return this.lager;
    }

    public static Shops getShopByLocation(Location loc) {
        for (Shops shop : Shops.values()) {
            if (shop.getLocation().distance(loc) < 5) {
                return shop;
            }
        }
        return null;
    }

    public static Shops getShopByLocationFurther(Location loc) {
        for (Shops shop : Shops.values()) {
            if (shop.getLocation().distance(loc) < 8) {
                return shop;
            }
        }
        return null;
    }

    public static Shops getShopByLocation(Location loc, float distance) {
        Shops shop = null;
        for(Shops s : Shops.values()) {
            if (s.getLocation().distance(loc) <= distance) {
                if (shop == null) shop = s;
                else if (s.getLocation().distance(loc) < shop.getLocation().distance(loc)) shop = s;
            }
        }
        return shop;
    }

    public int getKasse() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT kasse FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("kasse");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            Debug.debug("SQLException -> " + e1.getMessage());
        }
        return 0;
    }

    public boolean isLocked() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT locked FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getBoolean("locked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
        return false;
    }

    public void setLocked(boolean locked) {
        Script.executeAsyncUpdate("UPDATE shops SET locked=" + locked + " WHERE shopID=" + this.id);
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

    public static Shops getShop(String name) {
        for(Shops shop : Shops.values()) {
            if(shop.getName().equalsIgnoreCase(name)) {
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
        Script.executeUpdate("UPDATE shops SET kasse=" + n + " WHERE shopID=" + this.id);
    }

    public int getLagerSize() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT lager_max FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("lager_max");
            }
        } catch (SQLException e1) {
            NewRoleplayMain.handleError(e1);
        }
        return 0;
    }

    public void addLagerSize(int i) {
        Script.executeUpdate("UPDATE shops SET lager_max=" + (getLagerSize() + i) + " WHERE shopID=" + this.id);
    }

    public boolean acceptCard() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT card FROM shops WHERE shopID=" + this.id)) {
            if (rs.next()) {
                return rs.getInt("card")==1;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public int getLager() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
                Statement stmt = NewRoleplayMain.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT amount, price, itemID FROM shopprice WHERE shopID=" + s.getID())) {
            while (rs.next()) {
                final ShopItem si = ShopItem.getItem(rs.getInt("itemID"));
                c.put(si.getID(), new int[]{si.getItemStack().getAmount(), rs.getInt("price")});
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
            if(i == null) continue;
            i = i.clone();
            int[] a = n.getValue();
            i.setAmount(bi.getItemStack().getAmount());
            if(i.getItemMeta().hasDisplayName()) {
                ItemMeta im = i.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add("§8× §6" + a[1] + "€");
                im.setLore(lore);
                i.setAmount(bi.getItemStack().getAmount());
                i.setItemMeta(im);
                l.put(bi.getID(), i);
            } else {
                l.put(bi.getID(), new ItemBuilder(i.getType()).setName(bi.getName()).setLore("§8× §6" + a[1] + "€").setAmount(bi.getItemStack().getAmount()).build());
            }
        }
        return l;
    }

    public boolean isInShop(ShopItem si) {
        try (
                Statement stmt = NewRoleplayMain.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shopprice WHERE shopID=" + this.getID() + " AND itemID=" + si.getID())) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Shops getShopyByID(int id) {
        for (Shops s : Shops.values()) {
            if (s.getID() == id) {
                return s;
            }
        }
        return null;
    }

    public static List<Shops> getShopsByPlayer(int id) {
        ArrayList<Shops> s = new ArrayList<>();
        try (
                Statement stmt = NewRoleplayMain.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shops WHERE ownerID=" + id)) {
            while (rs.next()) {
                s.add(getShopyByID(rs.getInt("shopID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }

}
