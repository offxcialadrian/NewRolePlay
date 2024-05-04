package de.newrp.Vehicle;

import de.newrp.NewRoleplayMain;
import de.newrp.API.Script;
import de.newrp.Player.AFK;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Drive implements Listener {
    public static final HashMap<Player, Long> tacho = new HashMap<>();
    public static final HashMap<Player, Long> tank = new HashMap<>();
    public static final HashMap<Player, Car> cars = new HashMap<>();
    public static final HashMap<Boat, HashMap<Integer, Long>> speed = new HashMap<>();
    private static final ArrayList<Integer> damage_cooldown = new ArrayList<>();

    public static void cleanUp() {
        speed.entrySet().removeIf(ent -> ent.getKey() == null);
    }

    @EventHandler
    public void onMove(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if (vehicle.getPassengers() == null || vehicle.getPassengers().isEmpty()) return;
        Player p = (Player) vehicle.getPassengers().get(0);
        if (!p.isInsideVehicle()) return;
        if (!vehicle.getType().equals(EntityType.BOAT)) return;

        Boat mc = (Boat) vehicle;
        if (CarExit.cache.get(p) == null) CarExit.cache.put(p, p.getScoreboard());
        if (p.getLocation().getPitch() > 0.0F) {
            if (cars.containsKey(p)) {
                Car car = cars.get(p);
                if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) Car.setCarSidebar(p, car);
                if (car.getFuel() <= 0 || car.getCarheal() <= 0) return;
                CarType ct = car.getCarType();
                mc.setVelocity(p.getLocation().getDirection().multiply(getSpeedLevel(mc, car, ct)));
                Long time = System.currentTimeMillis();
                Long lastUsage = tacho.get(p);
                if (tacho.containsKey(p)) {
                    if (lastUsage + 2.3 * 1000 > time) {
                        return;
                    }
                }
                if (p.getLocation().getBlock().getType().equals(Material.WATER)) {
                    car.crash(25, mc);
                }
                if (AFK.isAFK(p)) AFK.setAFK(p, false);
                Car.updateCarSidebar(p, car, mc);
                tacho.put(p, time);

                if (!tank.containsKey(p)) tank.put(p, time);
                if (tank.get(p) + 36 * 1000 < time) {
                    car.fill(-1);
                    if (Script.getRandom(1, 2) == 2) car.setMileage(car.getMileage() + 1);
                    tank.put(p, time);
                }
            }
        }
    }


    public static float getSpeedLevel(Boat mc, Car c, CarType ct) {
        if (speed.containsKey(mc)) {
            HashMap<Integer, Long> map = speed.get(mc);
            Iterator<Map.Entry<Integer, Long>> it = map.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<Integer, Long> ent = it.next();
                int s = ent.getKey();
                long time = ent.getValue();
                long gear = (s * 2L) * 500;
                HashMap<Integer, Long> m = new HashMap<>();
                m.put(s, time);
                speed.put(mc, m);
                if (s == 6) {
                    if (c.getMileage() > 1000) {
                        if (!damage_cooldown.contains(c.getCarID())) {
                            int damage = (int) Script.getPercent(5, c.getCarType().getCarheal());
                            Script.sendActionBar((Player) mc.getPassengers().get(0), "§cDein Auto hat altersbedingte Schäden erlitten.");
                            damage_cooldown.add(c.getCarID());
                            c.crash(damage, mc);
                            c.updateCarSidebar(mc);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    damage_cooldown.remove(c.getCarID());
                                }
                            }.runTaskLater(NewRoleplayMain.getInstance(), 20 * 60 * 5);
                        }
                    }
                    return ct.getSpeed();
                } else {
                    if (time < System.currentTimeMillis()) {
                        if (Car.getCurrentSpeed(mc) > (s * 10)) {
                            m = new HashMap<>();
                            m.put((s + 1), System.currentTimeMillis() + (gear));
                            speed.put(mc, m);
                        } else {
                            m = new HashMap<>();
                            m.put((s - 1), System.currentTimeMillis() + (gear));
                            speed.put(mc, m);
                        }
                    }
                    float ds = (ct.getSpeed() * 1.23F) / 6;
                    return (s * ds);
                }
            }
        } else {
            HashMap<Integer, Long> map = new HashMap<>();
            map.put(1, System.currentTimeMillis() + (4 * 800));
            speed.put(mc, map);
        }
        return 0.2F;
    }
}
