package de.newrp.Vehicle;

import de.newrp.API.Cache;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.SlotLimit;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static de.newrp.API.Script.df;

public class Car {

    public static final List<Car> CARS = new ArrayList<>();
    public static final HashMap<Car, Boolean> LIVE_BOMB = new HashMap<>();
    public static final String PREFIX = "§8[§6Auto§8] §6" + Messages.ARROW + " §7";

    private final int carID;
    private Boat boatEntity;
    private final CarType carType;
    private final Player owner;
    private int fuel;
    private int carheal;
    private int mileage;
    private int insurance;
    private boolean locked;
    private boolean activated;
    private boolean bomb;
    private String licenseplate;
    private Strafzettel strafzettel;
    private final List<VehicleAddon> addons;

    public Car(int carID, Boat boatEntity, CarType carType, Player owner, int fuel, int carheal, int mileage, int insurance, boolean locked,
               boolean activated, boolean bomb, String licenseplate, Strafzettel strafzettel, List<VehicleAddon> addons) {
        this.carID = carID;
        this.boatEntity = boatEntity;
        this.carType = carType;
        this.owner = owner;
        this.fuel = fuel;
        this.carheal = carheal;
        this.mileage = mileage;
        this.insurance = insurance;
        this.locked = locked;
        this.activated = activated;
        this.licenseplate = licenseplate;
        this.strafzettel = strafzettel;
        this.addons = addons;
        this.bomb = bomb;
    }

    public static Car getCarByCarID(int carID) {
        return CARS.stream().filter(car -> car.getCarID() == carID).findAny().orElse(null);
    }

    public static Car getCarByEntityID(int minecartEntityIDID) {
        return CARS.stream().filter(car -> car.getBoatEntity().getEntityId() == minecartEntityIDID).findAny().orElse(null);
    }

    public static Car getCarByLicenseplate(String licenseplate) {
        return CARS.stream().filter(car -> car.getLicenseplate().equalsIgnoreCase(licenseplate)).findAny().orElse(null);
    }

    public static int getCarIDByLicenseplate(String licenseplate) {
        try (PreparedStatement stmt = main.getConnection().prepareStatement("SELECT id FROM vehicle WHERE kennzeichen = ?")) {

            stmt.setString(1, licenseplate.toUpperCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean carExcistByCarID(int carID) {
        try (PreparedStatement stmt = main.getConnection().prepareStatement("SELECT id FROM vehicle WHERE id = ?")) {

            stmt.setInt(1, carID);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clearAll() {
        for (Boat mc : Script.WORLD.getEntitiesByClass(Boat.class)) {
            Car car = getCarByEntityID(mc.getEntityId());

            if (car != null && car.getOwner() == null) {
                mc.remove();
            }
        }
    }

    public static void createCar(CarType carType, Location loc, Player p) {
        try (PreparedStatement stmt = main.getConnection().prepareStatement(
                "INSERT INTO vehicle (owner, cartype, fuel, heal, mileage, locked, location_x, location_y, location_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", com.mysql.jdbc.Statement.RETURN_GENERATED_KEYS
        )) {
            stmt.setInt(1, Script.getNRPID(p));
            stmt.setString(2, carType.getName());
            stmt.setInt(3, 100);
            stmt.setInt(4, carType.getCarheal());
            stmt.setInt(5, 0);
            stmt.setBoolean(6, true);
            stmt.setDouble(7, loc.getBlockX());
            stmt.setDouble(8, loc.getBlockY());
            stmt.setDouble(9, loc.getBlockZ());

            stmt.execute();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int carID = keys.getInt(1);

                    Boat boat = (Boat) Script.WORLD.spawnEntity(loc.clone().add(0, .5, 0), EntityType.BOAT);
                    boat.setMaxSpeed(carType.getMaxSpeed());

                    Car car = new Car(carID, boat, carType, p, 100, carType.getCarheal(), 0, 0, true, true, false, "", null, new ArrayList<>());
                    CARS.add(car);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void spawnCars(Player p) {
        int count = 1;
        int userID = Script.getNRPID(p);
        int limit = SlotLimit.VEHICLE.get(userID);

        List<Car> cars = new ArrayList<>();

        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT vehicle.id, vehicle.cartype, vehicle.fuel, vehicle.heal, vehicle.mileage, vehicle.insurance, vehicle.locked, vehicle.activated, vehicle.kennzeichen, vehicle.bomb, vehicle_addon.akku, vehicle_addon.musik \n" +
                     "FROM vehicle\n" +
                     "LEFT JOIN vehicle_addon ON vehicle_addon.id = vehicle.id\n" +
                     "WHERE vehicle.owner = " + userID + " AND vehicle.activated = TRUE")) {
            while (rs.next()) {

                int carID = rs.getInt("id");

                CarType carType = CarType.getCarTypeByName(rs.getString("cartype"));
                int fuel = rs.getInt("fuel");
                int heal = rs.getInt("heal");
                int mileage = rs.getInt("mileage");
                String licenseplate = rs.getString("kennzeichen");
                if (licenseplate == null) licenseplate = "";

                List<VehicleAddon> addons = new ArrayList<>();
                if (rs.getBoolean("akku")) addons.add(VehicleAddon.LADESTATION);

                int insurance = rs.getInt("insurance");

                boolean locked = rs.getBoolean("locked");
                boolean activated = rs.getBoolean("activated");
                boolean bomb = rs.getBoolean("bomb");

                cars.add(new Car(carID, null, carType, p, fuel, heal, mileage, insurance, locked, activated, bomb, licenseplate, Strafzettel.loadStrafzettel(carID), addons));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Car car : cars) {
            if (count++ <= limit) {
                Location loc = car.getSavedLocation();
                loc.getChunk().load();

                Bukkit.getScheduler().runTask(main.getInstance(), () -> {
                    Boat boat = (Boat) Script.WORLD.spawnEntity(loc, EntityType.BOAT);
                    boat.setMaxSpeed(car.getCarType().getMaxSpeed());

                    car.setBoatEntity(boat);

                    CARS.add(car);
                });
            }
        }
    }

    public static void deleteCars(Player p) {
        Iterator<Car> it = CARS.iterator();
        while (it.hasNext()) {
            Car car = it.next();
            if (car.getOwner().getUniqueId().equals(p.getUniqueId())) {
                Boat mc = car.getBoatEntity();
                car.save(mc);
                mc.remove();
                it.remove();
            }
        }
    }

    public static void setCarSidebar(Player p, Car car) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("Silver", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Fahrzeug");
        Score platzhalter1 = o.getScore(ChatColor.RED + "");
        Score platzhalter2 = o.getScore(ChatColor.YELLOW + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bAuto§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getCarType().getName());
        Score score3 = o.getScore(ChatColor.GRAY + "§bGang§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getGear(car.getBoatEntity()));
        Score score5 = o.getScore(ChatColor.GRAY + "§bTank§8:");
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getFuel() + "%");
        Score score7 = o.getScore(ChatColor.GRAY + "§bKilometer§8:");
        Score score8 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getMileage() + " km");
        Score score9 = o.getScore(ChatColor.GRAY + "");
        Score score10 = o.getScore(ChatColor.GRAY + "§bTacho§8:");
        Score score11 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getCurrentSpeed(car.getBoatEntity()) + " km/h");
        Score space = o.getScore("§8");
        Score space1 = o.getScore("§4");


        platzhalter1.setScore(14);
        score1.setScore(13);
        score2.setScore(12);
        platzhalter2.setScore(11);
        score3.setScore(10);
        score4.setScore(9);
        space.setScore(8);
        score5.setScore(7);
        score6.setScore(6);
        space1.setScore(5);
        score7.setScore(4);
        score8.setScore(3);
        score9.setScore(2);
        score10.setScore(1);
        score11.setScore(0);

        Cache.saveScoreboard(p);
        p.setScoreboard(b);
    }

    public static void updateCarSidebar(Player p, Car car, Boat mc) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("Silver", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Fahrzeug");
        Score platzhalter1 = o.getScore(ChatColor.RED + "");
        Score platzhalter2 = o.getScore(ChatColor.YELLOW + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bAuto§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getCarType().getName());
        Score score3 = o.getScore(ChatColor.GRAY + "§bGang§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getGear(car.getBoatEntity()));
        Score score5 = o.getScore(ChatColor.GRAY + "§bTank§8:");
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getFuel() + "%");
        Score score7 = o.getScore(ChatColor.GRAY + "§bKilometer§8:");
        Score score8 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getMileage() + " km");
        Score score9 = o.getScore(ChatColor.GRAY + "");
        Score score10 = o.getScore(ChatColor.GRAY + "§bTacho§8:");
        Score score11 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getCurrentSpeed(car.getBoatEntity()) + " km/h");
        Score space = o.getScore("§8");
        Score space1 = o.getScore("§4");


        platzhalter1.setScore(14);
        score1.setScore(13);
        score2.setScore(12);
        platzhalter2.setScore(11);
        score3.setScore(10);
        score4.setScore(9);
        space.setScore(8);
        score5.setScore(7);
        score6.setScore(6);
        space1.setScore(5);
        score7.setScore(4);
        score8.setScore(3);
        score9.setScore(2);
        score10.setScore(1);
        score11.setScore(0);

        p.setScoreboard(b);
    }

    public static int getGear(Boat mc) {
        if (Drive.speed.containsKey(mc)) {
            HashMap<Integer, Long> map = Drive.speed.get(mc);
            Iterator<Map.Entry<Integer, Long>> it = map.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<Integer, Long> ent = it.next();
                return ent.getKey();
            }
        }
        return 1;
    }

    public static Car getNearbyCar(Player p, int distance) {
        for (Car c : CARS) {
            if (c.getBoatEntity().getLocation().distance(p.getLocation()) < distance) {
                return c;
            }
        }
        return null;
    }

    public static int getCurrentSpeed(Boat mc) {
        if (mc == null) return 0;
        Vector vector = mc.getVelocity();
        double speed = Math.sqrt(NumberConversions.square(vector.getX()) + NumberConversions.square(vector.getZ()));
        return (int) (speed * 60);
    }

    public static List<Car> getCars(Player p) {
        List<Car> list = new ArrayList<>();

        for (Car c : CARS) {
            if (c.getOwner().getUniqueId() == p.getUniqueId()) {
                list.add(c);
            }
        }
        return list;
    }

    public static void activateCars(Player p, boolean b) {
        // update cached value
        for (Car car : CARS) {
            if (!car.getOwner().getName().equals(p.getName())) continue;

            car.setActivated(b);
        }

        // update in database
        Script.executeAsyncUpdate("UPDATE vehicle SET activated=" + b + " WHERE owner=" + Script.getNRPID(p));
    }

    @Override
    public String toString() {
        return "Car{" +
                "carID=" + carID +
                ", boatEntity=" + boatEntity +
                ", carType=" + carType +
                ", owner=" + owner +
                ", fuel=" + fuel +
                ", carheal=" + carheal +
                ", mileage=" + mileage +
                ", insurance=" + insurance +
                ", locked=" + locked +
                ", activated=" + activated +
                ", bomb=" + bomb +
                ", licenseplate='" + licenseplate + '\'' +
                ", strafzettel=" + strafzettel +
                ", addons=" + addons +
                '}';
    }

    public int getCarID() {
        return carID;
    }

    public Boat getBoatEntity() {
        return this.boatEntity;
    }

    public void setBoatEntity(Boat minecartEntity) {
        this.boatEntity = minecartEntity;
    }

    public CarType getCarType() {
        return carType;
    }

    public Player getOwner() {
        return owner;
    }

    public int getFuel() {
        return fuel;
    }

    public boolean hasBomb() {
        if (LIVE_BOMB.containsKey(this)) return true;
        return bomb;
    }

    public void fill(int amount) {
        int tank = getFuel() + amount;
        if (tank > 100) tank = 100;
        Script.executeAsyncUpdate("UPDATE vehicle SET fuel=" + tank + " WHERE id=" + this.carID);
        this.fuel = tank;
    }

    public int getCarheal() {
        return carheal;
    }

    public void setCarHeal(int carHeal) {
        Script.executeAsyncUpdate("UPDATE vehicle SET heal=" + carHeal + " WHERE id=" + this.carID);
        this.carheal = carHeal;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int miles) {
        Script.executeAsyncUpdate("UPDATE vehicle SET mileage=" + miles + " WHERE id=" + this.carID);
        this.mileage = miles;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;

        Script.executeAsyncUpdate("UPDATE vehicle SET locked=" + locked + " WHERE id=" + this.carID);
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;

        Script.executeAsyncUpdate("UPDATE vehicle SET activated = " + activated + " WHERE id = " + this.carID);
    }

    public int getInsurance() {
        return this.insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
        Script.executeAsyncUpdate("UPDATE vehicle SET insurance=" + insurance + " WHERE id=" + this.carID);
    }

    public Strafzettel getStrafzettel() {
        return strafzettel;
    }

    public void setStrafzettel(Strafzettel strafzettel) {
        this.strafzettel = strafzettel;
    }

    public String getLicenseplate() {
        return licenseplate;
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;

        try (PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE vehicle SET kennzeichen = ? WHERE id = ?")) {

            stmt.setString(1, licenseplate.toUpperCase());

            stmt.setInt(2, this.carID);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLicenseplate() {
        return this.licenseplate != null && !this.licenseplate.isEmpty();
    }

    public List<VehicleAddon> getAddons() {
        return addons;
    }

    public boolean isSpawned() {
        return CARS.contains(this);
    }

    public Location getSavedLocation() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT location_x, location_y, location_z FROM vehicle WHERE id=" + this.carID)) {
            if (rs.next()) {
                return new Location(Script.WORLD, rs.getDouble("location_x"), rs.getDouble("location_y"), rs.getDouble("location_z"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Boat minecart) {
        Location loc = minecart.getLocation();

        try (PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE vehicle SET fuel = ?, heal = ?, mileage = ?, location_x = ?, location_y = ?, location_z = ? WHERE id = ?")) {

            stmt.setInt(1, this.fuel);
            stmt.setInt(2, this.carheal);
            stmt.setInt(3, this.mileage);
            stmt.setDouble(4, loc.getX());
            stmt.setDouble(5, loc.getY());
            stmt.setDouble(6, loc.getZ());

            stmt.setInt(7, this.carID);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(Location loc) {
        Script.executeAsyncUpdate("UPDATE vehicle SET location_x=" + loc.getX() + ", location_y=" + loc.getY() + ", location_z=" + loc.getZ() + "  WHERE id=" + this.carID);
    }

    public boolean hasAddon(VehicleAddon addon) {
        return this.addons.contains(addon);
    }

    public void addAddon(VehicleAddon addon) {
        this.addons.add(addon);
        Script.executeAsyncUpdate("INSERT INTO vehicle_addon (id, " + addon.getName() + ") VALUES (" + this.carID + ", true) ON DUPLICATE KEY UPDATE vehicle_addon SET " + addon.getName() + "=true WHERE id=" + this.carID);
    }

    public void removeAddon(VehicleAddon addon) {
        this.addons.remove(addon);
        Script.executeAsyncUpdate("INSERT INTO vehicle_addon (id, " + addon.getName() + ") VALUES (" + this.carID + ", false) ON DUPLICATE KEY UPDATE vehicle_addon SET " + addon.getName() + "=true WHERE id=" + this.carID);
    }

    public int getTotalTrunkAmount() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS total FROM trunk WHERE carID=" + this.carID + " AND amount>0")) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void clearTrunk() {
        Script.executeUpdate("UPDATE trunk SET amount = 0 WHERE carID = " + this.carID);
    }

    public void updateCarSidebar(Boat mc) {
        if (mc != null) {
            if (mc.getPassengers().size() == 0) return;
            Player p = (Player) mc.getPassengers().get(0);
            updateCarSidebar(p, this, mc);
        }
    }

    public boolean isCarOwner(Player p) {
        return this.owner.getUniqueId().equals(p.getUniqueId());
    }

    public void crash(int dmg, Boat mc) {
        setCarHeal(carheal - dmg);
        if (carheal <= 0) {
            this.carheal = 0;
            if (mc != null) {
                Player p = (Player) mc.getPassengers().get(0);
                p.leaveVehicle();
                if (isCarOwner(p)) {
                    int v = getInsurance();
                    if (v > 0) {
                        p.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen. §8[" + (v - 1) + "§7/1§8]");
                        setInsurance(v - 1);
                        schrottplatz(mc);
                    } else {
                        destroy(true, mc);
                        p.sendMessage(PREFIX + "Dein Fahrzeug hat ein Totalschaden erlitten.");
                    }
                } else {
                    Player owner = getOwner();
                    if (owner != null)
                        owner.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen.");
                    schrottplatz(mc);
                }
            }
            HashMap<Integer, Long> m = new HashMap<>();
            m.put(1, System.currentTimeMillis() + (4 * 800));
            Drive.speed.put(mc, m);

            updateCarSidebar(mc);
        }
    }

    public void removeBomb() {
        Script.executeUpdate("UPDATE vehicle SET bomb=null WHERE id = " + this.carID);
        LIVE_BOMB.remove(this);
    }

    public void crashByBomb(Boat mc) {
        Location loc = mc.getLocation();
        setCarHeal(0);
        Player owner = getOwner();
        if (carheal <= 0) {
            this.carheal = 0;
            if (mc != null) {
                int v = getInsurance();
                if (v > 0) {
                    if (owner != null)
                        owner.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen. §8[§7" + (v - 1) + "/10§8]");
                    mc.getWorld().createExplosion(mc.getLocation().getX(), mc.getLocation().getY(), mc.getLocation().getZ(), .4F, false, false);
                    setInsurance(v - 1);
                    schrottplatz(mc);
                    removeBomb();
                } else {
                    destroy(true, mc);
                    if (owner != null)
                        owner.sendMessage(PREFIX + "Dein Fahrzeug wurde durch eine Autobombe komplett zerstört!");
                }
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                double distance = online.getLocation().distance(loc);
                if (distance < 20D) {
                    online.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 0, 0, false));
                    online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1, false));

                    if (distance < 7D) {
                        double damage = ((150D - (distance * 1.5D)) * .8D);
                        online.damage(damage);
                    }
                }
            }
        }
        updateCarSidebar(mc);
    }

    public void destroy(boolean explode, Boat mc) {
        if (mc != null) {
            if (explode)
                mc.getWorld().createExplosion(mc.getLocation().getX(), mc.getLocation().getY(), mc.getLocation().getZ(), .4F, false, false);
            mc.getLocation().getChunk().load();
            mc.remove();
        }
        Script.executeAsyncUpdate("DELETE FROM vehicle WHERE id=" + this.carID);
        Script.executeAsyncUpdate("DELETE FROM trunk WHERE carID=" + this.carID);

        Car.CARS.remove(this);
    }

    public void schrottplatz(Boat mc) {
        int max_x = 696;
        int min_x = 675;

        int max_z = 401;
        int min_z = 380;

        int x = Script.getRandom(min_x, max_x);
        int z = Script.getRandom(min_z, max_z);

        if (mc != null) mc.teleport(new Location(Script.WORLD, x, 71, z));
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    /*
        Score platzhalter1 = o.getScore(ChatColor.RED + "");
        Score platzhalter2 = o.getScore(ChatColor.YELLOW + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bOnline§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §a" + (Bukkit.getOnlinePlayers().size()- AFK.afk.size()) + " §8| §c" + AFK.afk.size() + " §8| §e" + Bukkit.getOnlinePlayers().size());
        Score score3 = o.getScore(ChatColor.GRAY + "§bTickets§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §eBug: " + amount.get(TicketTopic.BUG));
        Score score5 = o.getScore(ChatColor.DARK_AQUA + " §8» §eFrage: " + amount.get(TicketTopic.FRAGE));
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §eSpieler: " + amount.get(TicketTopic.SPIELER));
        Score score7 = o.getScore(ChatColor.DARK_AQUA + " §8» §eAccount: " + amount.get(TicketTopic.ACCOUNT));
        Score score8 = o.getScore(ChatColor.DARK_AQUA + " §8» §eSonstiges: " + amount.get(TicketTopic.SONSTIGES));
        Score score9 = o.getScore(ChatColor.GRAY + "");
        Score score10 = o.getScore(ChatColor.GRAY + "§bStadtkasse§8:");
        Score score11 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + df.format(stadtkasse) + "€");
     */
}