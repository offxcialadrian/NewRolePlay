package de.newrp.Shop;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Script;
import de.newrp.main;
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

    SH_CAFE(0, "Cafe", "Café Stadthalle", 35000, new Location(Script.WORLD, 626, 68, 1030), 30, 600, true, ShopType.CAFE),
    CAFE(1, "Café am X3", "Café am X3", 20000, new Location(Script.WORLD, 754, 72, 924), 15, 600, true, ShopType.CAFE),
    IKEA(2, "AEKI", "AEKI", 120000, new Location(Script.WORLD, 681, 68, 902), 100, 300, true, ShopType.HOUSEADDON),
    AEKI_CAFE(3, "Café am AEKI", "Café am AEKI", 10000, new Location(Script.WORLD, 688, 138, 908), 5, 600, true, ShopType.CAFE),
    GUNSHOP(4, "Schmid und Schmiedt Waffenladen", "Schmid und Schmiedt Waffenladen", 219000, new Location(Script.WORLD, 453, 69, 929), 150, 600, true, ShopType.GUNSHOP),
    SATURN(5, "Hankys", "Hankys", 80000, new Location(Script.WORLD, 865, 74, 964), 100, 600, true, ShopType.ELECTRONIC),
    SUPERMARKT(6, "Supermarkt", "Supermarkt", 149000, new Location(Script.WORLD, 640, 68, 861), 200, 600, true, ShopType.SUPERMARKET),
    MUSIK(7, "Musikladen", "Musikladen", 25000, new Location(Script.WORLD, 864, 74, 905), 10, 600, true, ShopType.MUSIC),
    FLOWER(8, "Blumenladen", "Blumenladen", 50000, new Location(Script.WORLD, 866, 74, 950), 25, 600, true, ShopType.FLOWER),
    APOTHEKE(9, "Apotheke", "Apotheke", 45000, new Location(Script.WORLD, 346, 76, 1079), 30, 600, true, ShopType.PHARMACY),
    MOTEL99(10, "Motel 99", "Motel 99", 35000, new Location(Script.WORLD, 809, 64, 1221, -89.25004f, -1.3500005f), 5, 600, true, ShopType.HOTEL),
    HOTEL_CALIFORNIA(11, "Hotel California", "Hotel California", 50000, new Location(Script.WORLD, 275, 66, 971, -90.44937f, 4.9502726f), 20, 600, false, ShopType.HOTEL),
    APOTHEKE_AEKI(12, "Apotheke am AEKI", "Apotheke am AEKI", 45000, new Location(Script.WORLD, 659, 68, 854, 88.650856f, 15.150019f), 30, 600, true, ShopType.PHARMACY),
    SHOP_GANG(13, "Gangshop", "Gangshop", 120000, new Location(Script.WORLD, 617, 65, 1277, 174.60092f, 15.899984f), 150, 600, true, ShopType.SUPERMARKET),
    GYM(14, "Fitnessstudio", "Fitnessstudio", 60000, new Location(Script.WORLD, 460, 67, 743, 58.60189f, 21.110485f), 20, 600, true, ShopType.GYM),
    SHOE_MALL(15, "Schuhladen", "Schuhladen", 30000, new Location(Script.WORLD, 843, 74, 962, -226.53546f, 41.1285f), 30, 600, true, ShopType.SHOE_STORE),
    MUSICSTORE_HAFEN(16, "Musikladen am Hafen", "Musikladen am Hafen", 9000, new Location(Script.WORLD, 329, 77, 997, 148.47311f, 16.413198f), 3, 600, true, ShopType.MUSIC),
    WAFFENLADEN_GANG(18, "Waffenladen Gang", "Waffenladen Gang", 199000, new Location(Script.WORLD, 534, 65, 1314, -269.9749f, 90.0f), 100, 600, true, ShopType.GUNSHOP),
    GEMUESE(19, "Gemüseladen", "Gemüseladen", 15000, new Location(Script.WORLD, 842, 74, 918, -356.24542f, 9.236951f), 15, 600, true, ShopType.GEMUESE),
    BUCHHALTUNG(20, "Buchladen", "Buchladen", 29000, new Location(Script.WORLD, 841, 74, 907, -1.7087402f, 20.008091f), 10, 600, true, ShopType.NEWS),
    NEWS_GERICHT(21, "Newsstand am Gericht", "Newsstand am Gericht", 20000, new Location(Script.WORLD, 794, 72, 951, -271.5559f, 7.0643415f), 5, 600, true, ShopType.NEWS),
    NEWS_HAFEN(22, "Newsstand am Strand", "Newsstand am Strand", 5000, new Location(Script.WORLD, 604, 65, 689, -358.99744f, 16.507566f), 2, 600, true, ShopType.NEWS),
    NEWS_PROMENADE(23, "Newsstand an der Promenade", "Newsstand an der Promenade", 5000, new Location(Script.WORLD, 275, 66, 892, -1.6918945f, 6.6413584f), 2, 600, true, ShopType.NEWS),
    NEWS_UBAHN(24, "Newsstand an der U-Bahn", "Newsstand an der U-Bahn", 5000, new Location(Script.WORLD, 336, 52, 1143, -269.4386f, 12.283148f), 2, 600, true, ShopType.NEWS),
    DOENER(25, "Dönerladen", "Dönerladen", 21000, new Location(Script.WORLD, 449, 65, 665, -270.5968f, 8.693133f), 10, 600, true, ShopType.FASTFOOD),
    ELEKTRO_GANG(27, "Elektroladen Gang", "Elektroladen Gang", 60000, new Location(Script.WORLD, 557, 64, 1270, -0.9939575f, 90.0f), 80, 600, true, ShopType.ELECTRONIC),
    AUTODEALER(29, "Autohaus", "Autohaus", 130000, new Location(Script.WORLD, 411, 77, 1081, -180.1416f, 90.0f), 30, 600, true, ShopType.CARDEALER),
    BLUMENLADEN_GYM(30, "Blumenladen Fitnessstudio", "Blumenladen Fitnessstudio", 55000, new Location(Script.WORLD, 517, 66, 774, -1.5904541f, 90.0f), 15, 600, true, ShopType.FLOWER),
    HANKYS(34, "Hankys", "Hankys", 60000, new Location(Script.WORLD, 557, 64, 1270, -359.05908f, 12.675349f), 50, 600, true, ShopType.ELECTRONIC),
    JAGDHUETTE(35, "Jagdhütte", "Jagdhütte", 30000, new Location(Script.WORLD, 476, 70, 1186, 89.78363f, 89.33273f), 5, 600, true, ShopType.JAGDHUETTE),
    BUCHHANDLUNG_INNENSTADT(36, "Buchladen Innenstadt", "Buchladen Innenstadt", 29000, new Location(Script.WORLD, 663, 67, 824, -270.25092f, 24.689606f), 10, 600, true, ShopType.NEWS),
    ANGELLADEN(37, "Angelladen", "Angelladen", 7500, new Location(Script.WORLD, 766, 64, 780, 266.49414f, 89.59964f), 5, 600, true, ShopType.ANGELSHOP),
    BAECKERI(38, "Bäckerei", "Bäckerei", 5000, new Location(Script.WORLD, 450, 66, 644, 90.62988f, 22.937416f), 3, 600, true, ShopType.CAFE),
    BURGERBRATER_SHOP(39, "Burgerladen", "Burgerladen", 30000, new Location(Script.WORLD, 460, 67, 771, -191.7402f, 6.5769224f), 10, 600, true, ShopType.FASTFOOD),
    BLUMENLADEN(40, "Blumenhandlung", "Blumenhandlung", 50000, new Location(Script.WORLD, 316, 76, 932, -181.94073f, 90.0f), 3, 600, true, ShopType.FLOWER);


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

    public boolean acceptCard() {
        try (Statement stmt = main.getConnection().createStatement();
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
            if(i == null) continue;
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
                Statement stmt = main.getConnection().createStatement();
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
                Statement stmt = main.getConnection().createStatement();
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
