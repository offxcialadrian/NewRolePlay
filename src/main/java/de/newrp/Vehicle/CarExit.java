package de.newrp.Vehicle;

import de.newrp.API.Cache;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class CarExit implements Listener {
    public static final HashMap<Player, Scoreboard> cache = new HashMap<>();

    @EventHandler
    public void onLeave(VehicleExitEvent e) {
        if (e.getVehicle().getType().equals(EntityType.BOAT)) {
            Player p = (Player) e.getExited();
            Boat mc = (Boat) e.getVehicle();
            Car car = Car.getCarByEntityID(mc.getEntityId());
            if (car == null) return;

            car.saveLocation(mc.getLocation());

            Drive.speed.remove(mc);
            Cache.loadScoreboard(p);
        }
    }

}
