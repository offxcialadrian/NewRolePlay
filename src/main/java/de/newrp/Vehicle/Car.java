package de.newrp.Vehicle;

import de.newrp.API.Cache;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.SlotLimit;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
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

public class Car {

    public static final List<Car> CARS = new ArrayList<>();
    public static final HashMap<Car, Boolean> LIVE_BOMB = new HashMap<>();
    public static final String PREFIX = "§8[§6Auto§8] §6" + Messages.ARROW + " §7";

    private final int carID;
    private Boat boatEntity;
    private final CarType carType;
    private final Player owner;
    private double speed;
    private int fuel;
    private double carheal;
    private double mileage;
    private int insurance;
    private boolean locked;
    private boolean started;
    private boolean activated;
    private boolean bomb;
    private String licenseplate;
    private Strafzettel strafzettel;
    private final List<VehicleAddon> addons;

    public Car(int carID, Boat boatEntity, CarType carType, Player owner, double speed, int fuel, double carheal, double mileage, int insurance, boolean locked, boolean started,
               boolean activated, boolean bomb, String licenseplate, Strafzettel strafzettel, List<VehicleAddon> addons) {
        this.carID = carID;
        this.boatEntity = boatEntity;
        this.carType = carType;
        this.owner = owner;
        this.speed = speed;
        this.fuel = fuel;
        this.carheal = carheal;
        this.mileage = mileage;
        this.insurance = insurance;
        this.locked = locked;
        this.started = started;
        this.activated = activated;
        this.licenseplate = licenseplate;
        this.strafzettel = strafzettel;
        this.addons = addons;
        this.bomb = bomb;
    }

    public static Car getCarByCarID(int carID) {
        return CARS.stream().filter(car -> car.getCarID() == carID).findAny().orElse(null);
    }

    public static Car getCarByEntityID(int boatEntityID) {
        return CARS.stream().filter(car -> car.getBoatEntity().getEntityId() == boatEntityID).findAny().orElse(null);
    }

    public static Car getCarByLicenseplate(String licenseplate) {
        return CARS.stream().filter(car -> car.getLicenseplate().equalsIgnoreCase(licenseplate)).findAny().orElse(null);
    }

    public static Car getCarByLicenseplateCheckOwner(String licenseplate, Player owner) {
        for (Car car : CARS) {
            if (car.getLicenseplate().equalsIgnoreCase(licenseplate)) {
                if (car.getOwner() == owner) {
                    return car;
                }
            }
        }
        return null;
    }

    public static int getCarIDByLicenseplate(String licenseplate) {
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT id FROM vehicle WHERE kennzeichen = ?")) {

            stmt.setString(1, licenseplate.toUpperCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean carExcistByCarID(int carID) {
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT id FROM vehicle WHERE id = ?")) {

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

    public static Car createCar(CarType carType, Location loc, Player p) {
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement(
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
                    boat.setWoodType(carType.getType());
                    boat.setMaxSpeed(carType.getMaxSpeed());

                    Car car = new Car(carID, boat, carType, p, 0D, 100, carType.getCarheal(), 0, 0, true, false, true, false, "", null, new ArrayList<>());
                    CARS.add(car);
                    return car;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return null;
    }

    public static void spawnCars(Player p) {
        int count = 1;
        int userID = Script.getNRPID(p);
        int limit = SlotLimit.VEHICLE.get(userID);

        List<Car> cars = new ArrayList<>();

        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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

                Car car = new Car(carID, null, carType, p, 0D, fuel, heal, mileage, insurance, locked, false, activated, bomb, licenseplate, Strafzettel.loadStrafzettel(carID), addons);
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Car car : cars) {
            if (count++ <= limit) {
                Location loc = car.getSavedLocation();
                loc.getChunk().load();

                Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> {
                    Boat boat = (Boat) Script.WORLD.spawnEntity(loc, EntityType.BOAT);
                    boat.setWoodType(car.getCarType().getType());
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
                car.save();
                mc.remove();
                it.remove();
            }
        }
    }

    public void setCarSidebar(Player p, Car car) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("Silver", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Fahrzeug");
        Score platzhalter = o.getScore(ChatColor.RED + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bAuto§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getCarType().getName());
        Score score3 = o.getScore(ChatColor.GRAY + "§bTacho§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round(Math.floor(getSpeed() * 100)) + " km/h");
        Score score5 = o.getScore(ChatColor.GRAY + "§bGang§8:");
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getGear());
        Score score7 = o.getScore(ChatColor.GRAY + "§bTank§8:");
        Score score8 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getFuel() + "%");
        Score score9 = o.getScore(ChatColor.GRAY + "§bKilometer§8:");
        Score score10 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round((float) car.getMileage() / 1000) + " km");
        Score score11 = o.getScore(ChatColor.GRAY + "§bSchäden§8:");
        Score score12 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round((float) (car.getCarType().getCarheal() - getCarheal()) / 100) + "x");
        Score score13 = o.getScore(ChatColor.GRAY + "");


        platzhalter.setScore(13);
        score1.setScore(12);
        score2.setScore(11);
        score3.setScore(10);
        score4.setScore(9);
        score5.setScore(8);
        score6.setScore(7);
        score7.setScore(6);
        score8.setScore(5);
        score9.setScore(4);
        score10.setScore(3);
        score11.setScore(2);
        score12.setScore(1);
        score13.setScore(0);

        Cache.saveScoreboard(p);
        p.setScoreboard(b);
    }

    public void updateCarSidebar(Player p, Car car) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("Silver", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Fahrzeug");
        Score platzhalter = o.getScore(ChatColor.RED + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bAuto§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getCarType().getName());
        Score score3 = o.getScore(ChatColor.GRAY + "§bTacho§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round(Math.floor(getSpeed() * 100)) + " km/h");
        Score score5 = o.getScore(ChatColor.GRAY + "§bGang§8:");
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + getGear());
        Score score7 = o.getScore(ChatColor.GRAY + "§bTank§8:");
        Score score8 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + car.getFuel() + "%");
        Score score9 = o.getScore(ChatColor.GRAY + "§bKilometer§8:");
        Score score10 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round((float) car.getMileage() / 1000) + " km");
        Score score11 = o.getScore(ChatColor.GRAY + "§bSchäden§8:");
        Score score12 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Math.round((float) (car.getCarType().getCarheal() - getCarheal()) / 100) + "x");
        Score score13 = o.getScore(ChatColor.GRAY + "");


        platzhalter.setScore(13);
        score1.setScore(12);
        score2.setScore(11);
        score3.setScore(10);
        score4.setScore(9);
        score5.setScore(8);
        score6.setScore(7);
        score7.setScore(6);
        score8.setScore(5);
        score9.setScore(4);
        score10.setScore(3);
        score11.setScore(2);
        score12.setScore(1);
        score13.setScore(0);

        p.setScoreboard(b);
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }


    public int getGear() {
        double speed = this.getSpeed() - 0.01;
        return (int) Math.round(speed / (this.getCarType().getMaxSpeed() / 5)) + 1;
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

    public double getCarheal() {
        return carheal;
    }

    public void setCarHeal(double carHeal) {
        Script.executeAsyncUpdate("UPDATE vehicle SET heal=" + carHeal + " WHERE id=" + this.carID);
        this.carheal = carHeal;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double miles) {
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

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted(boolean started) {
        this.started = started;
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
        String plate = licenseplate;
        if (plate.length() == 0) {
            return "X-00-00";
        } else {
            return plate;
        }
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;

        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("UPDATE vehicle SET kennzeichen = ? WHERE id = ?")) {

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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT location_x, location_y, location_z FROM vehicle WHERE id=" + this.carID)) {
            if (rs.next()) {
                return new Location(Script.WORLD, rs.getDouble("location_x"), rs.getDouble("location_y"), rs.getDouble("location_z"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        Location loc = this.getLocation();

        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("UPDATE vehicle SET fuel = ?, heal = ?, mileage = ?, location_x = ?, location_y = ?, location_z = ? WHERE id = ?")) {

            stmt.setInt(1, this.fuel);
            stmt.setInt(2, (int) Math.round(this.carheal));
            stmt.setInt(3, (int) Math.round(this.mileage));
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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

    public void setCarSidebar() {
        if (this.getPassengers().isEmpty()) return;
        Player p = (Player) this.getDriver();
        setCarSidebar(p, this);
    }

    public void updateCarSidebar() {
        if (this.getPassengers().isEmpty()) return;
        Player p = (Player) this.getDriver();
        updateCarSidebar(p, this);
    }

    public boolean isCarOwner(Player p) {
        return this.owner.getUniqueId().equals(p.getUniqueId());
    }

    public void crash(double dmg) {
        setCarHeal(carheal - dmg);
        if (carheal <= 0) {
            this.carheal = 0;
            Player p = (Player) this.getDriver();
            p.leaveVehicle();
            if (isCarOwner(p)) {
                int v = getInsurance();
                if (v > 0) {
                    p.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen. §8[" + (v - 1) + "§7/1§8]");
                    setInsurance(v - 1);
                    schrottplatz();
                } else {
                    destroy(true);
                    p.sendMessage(PREFIX + "Dein Fahrzeug hat ein Totalschaden erlitten.");
                }
            } else {
                Player owner = getOwner();
                if (owner != null)
                    owner.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen.");
                schrottplatz();
            }

            updateCarSidebar();
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
            int v = getInsurance();
            if (v > 0) {
                if (owner != null)
                    owner.sendMessage("§eDeine Versicherung hat den Schaden ohne weitere Kosten übernommen. §8[§7" + (v - 1) + "/10§8]");
                mc.getWorld().createExplosion(mc.getLocation().getX(), mc.getLocation().getY(), mc.getLocation().getZ(), .4F, false, false);
                setInsurance(v - 1);
                schrottplatz();
                removeBomb();
            } else {
                destroy(true);
                if (owner != null)
                    owner.sendMessage(PREFIX + "Dein Fahrzeug wurde durch eine Autobombe komplett zerstört!");
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
        updateCarSidebar();
    }

    public void destroy(boolean explode) {
        Boat mc = this.getBoatEntity();
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

    public void schrottplatz() {
        int max_x = 696;
        int min_x = 675;

        int max_z = 401;
        int min_z = 380;

        int x = Script.getRandom(min_x, max_x);
        int z = Script.getRandom(min_z, max_z);

        this.getBoatEntity().teleport(new Location(Script.WORLD, x, 71, z));
    }

    public List<Entity> getPassengers() {
        return this.getBoatEntity().getPassengers();
    }

    public Entity getDriver() {
        if (this.getPassengers().isEmpty()) {
            return null;
        } else {
            return this.getPassengers().get(0);
        }
    }

    public Location getLocation() {
        return this.getBoatEntity().getLocation();
    }

    public void setVelocity(Vector velocity) {
        this.getBoatEntity().setVelocity(velocity);
    }

    public Vector getVelocity() {
        return this.getBoatEntity().getVelocity();
    }

    public Float getFallDistance() {
        return this.getBoatEntity().getFallDistance();
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