package de.newrp.Vehicle;

import de.newrp.main;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CarHandler implements Listener {

    public static HashMap<Car, Player> owner = new HashMap<>();
    public static HashMap<Car, Boolean> locked = new HashMap<>();
    public static HashMap<Car, Double> speeds = new HashMap<>();
    public static HashMap<Car, Double> fuels = new HashMap<>();

    @EventHandler
    public static void onDrive(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Car) {
            Car car = (Car) event.getVehicle();
            if (car.getDriver() != null) {
                Player player = (Player) car.getPassengers().get(0);
                speeds.putIfAbsent(car, 0D);
                double speed = speeds.get(car);
                double pitch;

                CarType carType = car.getCarType();
                if (locked.get(car)) {
                    if (speed < carType.getSpeed()) {
                        speed += ((0.01 * carType.getSpeed()) / ((0.1 * speed) + 1));
                    }
                    pitch = (5 - Math.pow(Math.abs(player.getLocation().getPitch()) / 90, 2)) / 5;
                    speed = speed * pitch;
                } else {
                    if (speed > 0) {
                        speed -= 0.02;
                    }
                }
                speeds.put(car, speed);

                if (Math.round(speed * 100) > 5) {
                    player.sendMessage(Component.text(Math.round(speed * 100) + " km/h"));

                    double y = -1;
                    Location block = car.getLocation();
                    Location next = block.add(car.getLocation().getDirection());
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

                    Vector direction = car.getLocation().getDirection();
                    car.setVelocity(direction.multiply(speed));
                    double finalY = y;
                    if (finalY > 0) {
                        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> car.setVelocity(car.getLocation().getDirection().setY(finalY)), 1L);
                    }
                    if (car.getFallDistance() > 0) {
                        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> car.setVelocity(car.getLocation().getDirection().setY(-1)), 2L);
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onExit(VehicleExitEvent event) {

    }
}
