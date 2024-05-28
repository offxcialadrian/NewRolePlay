package de.newrp.House;

import de.newrp.API.Debug;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class House {

    public static final String PREFIX = "§8[§6Haus§8]§7 ";
    public static final ArrayList<House> HOUSES = new ArrayList<>();
    public static final HashMap<Integer, List<House>> PLAYER_HOUSES = new HashMap<>();
    public static final int MAX_DRUGS = 100;
    public static final int MAX_SNACKS = 250;
    public static final int MAX_MONEY = 1000000000;

    public static HashMap<Integer, ArrayList<ItemStack>> BRIEFKASTEN = new HashMap<>();

    private final int houseID;
    private final List<HouseAddon> addons;
    private final List<Mieter> mieter;
    private int owner;
    private Location[] location;
    private Location sign;
    private int kasse;
    private int slots;
    private int price;
    private int snacks;

    public House(int houseID, int owner, Location[] location, Location sign, int kasse, int slots, int price, int snacks, List<HouseAddon> addons, List<Mieter> mieter) {
        this.houseID = houseID;
        this.owner = owner;
        this.location = location;
        this.sign = sign;
        this.kasse = kasse;
        this.slots = slots;
        this.price = price;
        this.snacks = snacks;
        this.addons = addons;
        this.mieter = mieter;
    }

    public static Mieter getMieter(House h, int id) {
        for (Mieter m : h.getMieter()) {
            if (m.getID() == id) return m;
        }
        return null;
    }

    public static void loadHouses() {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT house.houseID, house.ownerID, house.min_x, house.min_y, house.min_z, house.max_x, house.max_y, house.max_z, "
                         + "house.sign, house.kasse, house.slots, house.price, house.snacks, nrpID_owner.name AS ownerName, "
                         + "GROUP_CONCAT(DISTINCT house_addon.addonID SEPARATOR '/') AS addons, "
                         + "GROUP_CONCAT(DISTINCT CONCAT(nrpID_mieter.name, '/', join_house_bewohner.mieterID, '/', join_house_bewohner.miete, '/', IFNULL(join_house_bewohner.nebenkosten, 0)) "
                         + "SEPARATOR ';') AS mieter FROM house LEFT JOIN house_addon ON house_addon.houseID = house.houseID "
                         + "LEFT JOIN house_bewohner AS join_house_bewohner ON join_house_bewohner.houseID = house.houseID "
                         + "LEFT JOIN nrp_id AS nrpID_owner ON nrpID_owner.id = house.ownerID "
                         + "LEFT JOIN nrp_id AS nrpID_mieter ON nrpID_mieter.id = join_house_bewohner.mieterID "
                         + "GROUP BY house.houseID")) {
                while (rs.next()) {
                    int houseID = rs.getInt("houseID");
                    if (houseID == 0) continue;

                    int owner = rs.getInt("ownerID");
                    String ownerName = rs.getString("ownerName");

                    Location min = new Location(Script.WORLD, rs.getInt("min_x"), rs.getInt("min_y"), rs.getInt("min_z"));
                    Location max = new Location(Script.WORLD, rs.getInt("max_x"), rs.getInt("max_y"), rs.getInt("max_z"));
                    String raw = rs.getString("sign");
                    String[] split = raw.split("/");
                    Location sign = new Location(Script.WORLD, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    int kasse = rs.getInt("kasse");
                    int slots = rs.getInt("slots");
                    int price = rs.getInt("price");
                    int snacks = rs.getInt("snacks");

                    List<HouseAddon> addons = new ArrayList<>();
                    String rawAddons = rs.getString("addons");
                    if (rawAddons != null) {
                        for (String addonString : rawAddons.split("/")) {
                            if (!Script.isInt(addonString)) continue;
                            int i = Integer.parseInt(addonString);
                            HouseAddon addon = HouseAddon.getHausAddonByID(i);
                            if (addon != null) addons.add(addon);
                        }
                    }

                    List<Mieter> mieter = new ArrayList<>();
                    String rawMieter = rs.getString("mieter");
                    if (rawMieter != null && !rawMieter.isEmpty()) {
                        for (String mieterString : rawMieter.split(";")) {
                            String[] mieter_all = mieterString.split("/");
                            String name = mieter_all[0];
                            int id = Integer.parseInt(mieter_all[1]);
                            int miete = Integer.parseInt(mieter_all[2]);
                            int nebenkosten = Integer.parseInt(mieter_all[3]);
                            mieter.add(new Mieter(name, id, miete, nebenkosten));
                        }
                    }

                    House house = new House(houseID, owner, new Location[]{min, max}, sign, kasse, slots, price, snacks, addons, mieter);

                    HOUSES.add(house);

                    getHouseSignLocation(houseID);
                    getHouseDoors(houseID);
                    Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> house.updateSign(ownerName));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.debug("SQLException -> " + e.getMessage());
            }
        });
    }

    public static House getInsideHouse(Player p) {
        for (House h : getHouses(Script.getNRPID(p))) {
            if (Script.isInRegion(p.getLocation(), h.getLocation()[0], h.getLocation()[1])) {
                return h;
            }
        }
        return null;
    }

    public static boolean isInHouse(Player p) {
        return getInsideHouse(p) != null;
    }

    public static List<House> getHouses(int id) {
        ArrayList<Integer> houses = new ArrayList<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT houseID FROM house_bewohner WHERE mieterID = " + id + " ORDER BY houseID")) {
            while (rs.next()) {
                houses.add(rs.getInt("houseID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (houses.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<House> list = new ArrayList<>();
            for (House h : HOUSES) {
                if (houses.contains(h.getID())) {
                    list.add(h);
                }
            }
            return list;
        }
    }

    public static List<House> getFreeHouses() {
        List<House> houses = new ArrayList<>();
        for (House h : HOUSES) {
            if (h.getOwner() != 0) continue;
            houses.add(h);
        }
        return houses;
    }

    public static int getHouseAmount(int id) {
        return getHouses(id).size()-1;
    }

    public static boolean hasHouse(int id) {
        return !getHouses(id).isEmpty();
    }

    public static House getHouseByID(int id) {
        for (House h : HOUSES) {
            if (h.getID() == id) {
                return h;
            }
        }
        return null;
    }

    public static boolean livesInHouse(Player p, int houseID) {
        for (House h : getHouses(Script.getNRPID(p))) {
            if (h.getID() == houseID) return true;
        }
        return false;
    }

    public static boolean livesInHouse(int id, int houseID) {
        for (House h : getHouses(id)) {
            if (h.getID() == houseID) return true;
        }
        return false;
    }

    public static House getHouseByDoor(Location loc) {
        for (House h : HOUSES) {
            for (Location l : h.getDoors()) {
                if (l.equals(loc)) return h;
            }
        }
        return null;
    }

    public static boolean isValid(int house) {
        for (House h : HOUSES) {
            if (h.getID() == house) return true;
        }
        return false;
    }

    public static House getNearHouse(Location loc, int distance) {
        for (House h : HOUSES) {
            if (Script.isInRange(h.getSignLocation(), loc, distance)) return h;
        }
        return null;
    }

    public static House getRandomHouse() {
        List<House> houses = new ArrayList<>();
        for (House h : HOUSES) {
            if (h.getOwner() != 0) houses.add(h);
        }
        return houses.get(Script.getRandom(0, houses.size() - 1));
    }

    public String toString() {
        return "{houseID: " + houseID + ", owner: " + owner + ", location: " + Arrays.toString(location) + ", sign: " + sign + ", kasse: " + kasse + ", slots: " + slots + ", price: " + price + ", snacks: " + snacks + ", addons: " + addons.toString() + ", mieter: " + mieter.toString() + "}";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getID() {
        return this.houseID;
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int id) {
        Script.executeAsyncUpdate("UPDATE house SET ownerID = " + id + " WHERE houseID = " + this.houseID);
        if (PLAYER_HOUSES.containsKey(this.owner)) PLAYER_HOUSES.get(this.owner).remove(this);
        this.owner = id;
    }

    public Location[] getLocation() {
        return this.location;
    }

    public void setLocation(Location min, Location max) {
        this.location = new Location[]{min, max};

        int min_x = min.getBlockX(), min_y = min.getBlockY(), min_z = min.getBlockZ();
        int max_x = max.getBlockX(), max_y = max.getBlockY(), max_z = max.getBlockZ();

        Script.executeAsyncUpdate("UPDATE house SET min_x = " + min_x + ", min_y = " + min_y + ", min_z = " + min_z + ", max_x = " + max_x + ", max_y = " + max_y + ", max_z = " + max_z + " WHERE houseID = " + this.houseID);
    }

    public Location getSignLocation() {
        return this.sign;
    }

    public void setSignLocation(Location loc) {
        this.sign = loc;

        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        String sign = x + "/" + y + "/" + z;
        Script.executeAsyncUpdate("UPDATE house SET sign='" + sign + "' WHERE houseID = " + this.houseID);
    }

    public int getKasse() {
        return this.kasse;
    }

    public void setKasse(int kasse) {
        if (kasse < 0) kasse = 0;
        if (kasse > MAX_MONEY) kasse = MAX_MONEY;
        this.kasse = kasse;
        Script.executeAsyncUpdate("UPDATE house SET kasse = " + kasse + " WHERE houseID = " + this.houseID);
    }

    public void addKasse(int amount) {
        setKasse(this.kasse + amount);
    }

    public int getSlots() {
        return this.slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
        Script.executeAsyncUpdate("UPDATE house SET slots = " + slots + " WHERE houseID = " + this.houseID);
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
        Script.executeAsyncUpdate("UPDATE house SET price = " + price + " WHERE houseID = " + this.houseID);
    }

    public int getSnacks() {
        return this.snacks;
    }

    public void setSnacks(int snacks) {
        if (snacks > MAX_SNACKS) snacks = MAX_SNACKS;
        this.snacks = snacks;
        Script.executeAsyncUpdate("UPDATE house SET snacks = " + snacks + " WHERE houseID = " + this.houseID);
    }

    public List<HouseAddon> getAddons() {
        return this.addons;
    }

    public boolean hasAddon(HouseAddon addon) {
        return this.addons.contains(addon);
    }

    public void addAddon(HouseAddon addon) {
        if (!this.addons.contains(addon)) this.addons.add(addon);
        final int houseID = this.houseID;
        Script.executeAsyncUpdate("INSERT INTO house_addon (houseID, addonID) VALUES (" + houseID + ", " + addon.getID() + ");");
    }

    public void removeAddon(HouseAddon addon) {
        this.addons.remove(addon);
        Script.executeAsyncUpdate("DELETE FROM house_addon WHERE houseID = " + this.houseID + " AND addonID = " + addon.getID());
    }

    public List<Mieter> getMieter() {
        return this.mieter;
    }

    public void addMieter(Mieter mieter, boolean vermieter) {
        if (!this.mieter.contains(mieter)) this.mieter.add(mieter);
        final int houseID = this.houseID;
        Script.executeAsyncUpdate("INSERT INTO house_bewohner (houseID, mieterID, vermieter, miete, nebenkosten, immobilienmarkt) VALUES (" + houseID + ", " + mieter.getID() + ", " + vermieter + ", " + mieter.getMiete() + ", 0, FALSE);");
        if (PLAYER_HOUSES.containsKey(mieter.getID())) {
            PLAYER_HOUSES.get(mieter.getID()).add(this);
        } else {
            List<House> list = new ArrayList<>();
            list.add(this);
            PLAYER_HOUSES.put(mieter.getID(), list);
        }
    }

    public boolean isMieter(int id) {
        for (Mieter m : getMieter()) {
            if (m.getID() == id) return true;
        }
        return false;
    }

    public int getFreeSlots() {
        return (this.slots + 1) - getMieter().size();
    }

    public void removeMieter(int id) {
        Debug.debug("removeMieter(" + id + ")");
        Mieter m = getMieter(this, id);
        Debug.debug("m == null = " + (m == null));
        if (m != null) {
            this.mieter.removeIf(mieter -> mieter.getID() == m.getID());
        }
        if (PLAYER_HOUSES.containsKey(id)) PLAYER_HOUSES.get(id).remove(this);
        Script.executeAsyncUpdate("DELETE FROM house_bewohner WHERE houseID = " + this.houseID + " AND mieterID = " + id);
    }

    public void reset() {
        this.owner = 0;
        this.kasse = 0;
        this.slots = 2;
        this.addons.clear();
        this.mieter.clear();
        Script.executeAsyncUpdate("UPDATE house SET ownerID = 0, kasse = 0, slots = 2 WHERE houseID = " + this.houseID);
        Script.executeAsyncUpdate("DELETE FROM house_addon WHERE houseID = " + this.houseID);
        Script.executeAsyncUpdate("DELETE FROM house_bewohner WHERE houseID = " + this.houseID);
        clearSign();
    }

    public void delete() {
        int houseID = this.houseID;
        Script.executeAsyncUpdate("DELETE FROM house WHERE houseID = " + houseID);
        Script.executeAsyncUpdate("DELETE FROM house_addon WHERE houseID = " + houseID);
        Script.executeAsyncUpdate("DELETE FROM house_bewohner WHERE houseID = " + houseID);
        Script.executeAsyncUpdate("DELETE FROM house_door WHERE houseID = " + houseID);
        HOUSES.remove(this);
    }

    public ArrayList<Location> getDoors() {
        return getHouseDoors(this.houseID);
    }

    public static ArrayList<Location> getHouseDoors(int house) {
        ArrayList<Location> list = new ArrayList<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT x, y, z FROM house_door WHERE houseID=" + house)) {
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                list.add(new Location(Script.WORLD, x, y, z));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isInside(Player p) {
        return Script.isInRegion(p.getLocation(), this.location[0], this.location[1]);
    }

    public boolean livesInHouse(Player p) {
        return getHouses(Script.getNRPID(p)).contains(this);
    }

    public boolean livesInHouse(OfflinePlayer p) {
        return getHouses(Script.getNRPID(p)).contains(this);
    }

    public void updateSign() {
        if (this.sign.getBlock().getType().equals(Material.OAK_WALL_SIGN)) {
            Sign s = (Sign) this.sign.getBlock().getState();
            s.setLine(0, "");
            s.setLine(1, "== " + this.houseID + " ==");
            s.setLine(2, Script.getOfflinePlayer(this.owner).getName());
            s.setLine(3, "");
            s.update(true);
        }
    }

    public void updateSign(String username) {
        if (this.sign.getBlock().getType().equals(Material.OAK_WALL_SIGN)) {
            Sign s = (Sign) this.sign.getBlock().getState();
            if (this.owner != 0 && username != null) {
                s.setLine(0, "");
                s.setLine(1, "== " + this.houseID + " ==");
                s.setLine(2, username);
                s.setLine(3, "");
                s.update(true);
            } else {
                s.setLine(0, "");
                s.setLine(1, "== " + this.houseID + " ==");
                s.setLine(2, "Frei");
                s.setLine(3, this.price + "€");
                s.update(true);
            }
        }
    }

    public void clearSign() {
        if (this.sign.getBlock().getType().equals(Material.OAK_WALL_SIGN)) {
            Sign s = (Sign) this.sign.getBlock().getState();
            s.setLine(0, "");
            s.setLine(1, "== " + this.houseID + " ==");
            s.setLine(2, "Frei");
            s.setLine(3, this.price + "$");
            s.update(true);
        }
    }

    public Mieter getMieterByID(int id) {
        for (Mieter mieter : this.mieter) {
            if (mieter.getID() == id) return mieter;
        }
        return null;
    }


    public boolean hasAccess(int id) {
        List<House> houses = getHouses(id);
        int i = 0;
        for (House h : houses) {
            if (h.getID() == getID()) return true;
            i++;
        }
        return false;
    }

    public int getMiete(int userID) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT miete FROM house_bewohner WHERE houseID = " + this.houseID + " AND mieterID = " + userID)) {
            if (rs.next()) {
                return rs.getInt("miete");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Location getHouseSignLocation(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT sign FROM house WHERE houseid=" + id)) {
            if (rs.next()) {
                String raw = rs.getString("sign");
                String[] split = raw.split("/");
                int x = Integer.parseInt(split[0]);
                int y = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                return new Location(Script.WORLD, x, y, z).clone();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Mieter {

        private final String name;
        private final int id;
        private final int miete;
        private int nebenkosten;

        public Mieter(String name, int id, int miete, int nebenkosten) {
            this.name = name;
            this.id = id;
            this.miete = miete;
            this.nebenkosten = nebenkosten;
        }


        public String toString() {
            return "{name: " + name + ", id: " + id + ", miete: " + miete + ", nebenkosten: " + nebenkosten + "}";
        }

        public String getName() {
            return this.name;
        }

        public int getID() {
            return this.id;
        }

        public int getMiete() {
            return this.miete;
        }

        public int getNebenkosten() {
            return this.nebenkosten;
        }

        public void setNebenkosten(House haus, int nebenkosten) {
            this.nebenkosten = nebenkosten;
            Script.executeAsyncUpdate("UPDATE house_bewohner SET nebenkosten = " + nebenkosten + " WHERE houseID = " + haus.getID() + " AND mieterID = " + getID());
        }
    }
}
