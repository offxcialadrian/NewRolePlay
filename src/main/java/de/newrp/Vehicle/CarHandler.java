package de.newrp.Vehicle;

import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class CarHandler implements Listener {

    public static HashMap<Boat, Player> owner = new HashMap<>();
    public static HashMap<Boat, Boolean> cars = new HashMap<>();
    public static HashMap<Boat, Double> speeds = new HashMap<>();
    public static HashMap<Boat, Double> fuels = new HashMap<>();

    @EventHandler
    public static void onDrive(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Boat boat = (Boat) event.getVehicle();
            if (!boat.getPassengers().isEmpty()) {
                Player player = (Player) boat.getPassengers().get(0);
                speeds.putIfAbsent(boat, 0D);
                double speed = speeds.get(boat);
                double pitch;
                for (Cars car : Cars.values()) {
                    if (boat.getWoodType() == car.getType()) {
                        if (cars.get(boat)) {
                            if (speed < car.getSpeed()) {
                                speed += ((0.01 * car.getSpeed()) / ((0.1 * speed) + 1));
                            }
                            pitch = (5 - Math.pow(Math.abs(player.getLocation().getPitch()) / 90, 2)) / 5;
                            speed = speed * pitch;
                        } else {
                            if (speed > 0) {
                                speed -= 0.02;
                            }
                        }
                        speeds.put(boat, speed);
                    }
                }

                if (Math.round(speed * 100) > 5) {
                    player.sendMessage(Component.text(Math.round(speed * 100) + " km/h"));

                    double y = -1;
                    Location block = boat.getLocation();
                    Location next = block.add(boat.getLocation().getDirection());
                    if (block.getY() % 1 == 0.5) {
                        if (((Slab) next.getBlock().getBlockData()).getType() == Slab.Type.TOP) {
                            y = 0.5;
                            speed = -speed;
                        }
                    } else if (block.getY() % 1 == 0.0) {
                        if (((Slab) next.getBlock().getBlockData()).getType() == Slab.Type.BOTTOM) {
                            y = 0.5;
                            speed = -speed;
                        }
                    }

                    Vector direction = boat.getLocation().getDirection();
                    boat.setVelocity(direction.multiply(speed));
                    double finalY = y;
                    if (finalY > 0) {
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> boat.setVelocity(boat.getLocation().getDirection().setY(finalY)), 1L);
                    }
                    if (boat.getFallDistance() > 0) {
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> boat.setVelocity(boat.getLocation().getDirection().setY(-1)), 2L);
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onExit(VehicleExitEvent event) {

    }
}
