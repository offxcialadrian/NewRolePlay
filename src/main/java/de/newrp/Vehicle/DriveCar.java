package de.newrp.Vehicle;

import de.newrp.API.Cache;
import de.newrp.API.Debug;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class DriveCar implements Listener {

    private static final double JUMP_HEIGHT = 1.5; // Höhe, um die das Boot springen soll
    private static final double SPEED_MULTIPLIER = 1.2; // Multiplikator für die Geschwindigkeit

    @EventHandler
    public void onDriveBoat(VehicleMoveEvent e) {
        Player p = (Player) e.getVehicle().getPassengers().get(0);

        Vehicle vehicle = (Vehicle) p.getVehicle();
        if (!(vehicle instanceof Boat)) return;

        Boat boat = (Boat) vehicle;


        // Block vor dem Boot
        Block b = p.getLocation().getBlock();
        Slab slab = (Slab) b.getBlockData();

        Slab.Type type = slab.getType();

        Vector direction = p.getLocation().getDirection().normalize();

// Berechne die Koordinaten des Blocks hinter dem gegebenen Block basierend auf der Blickrichtung des Spielers
        int x = (int) Math.round(b.getX() + direction.getX());
        int y = (int) Math.round(b.getY() + direction.getY());
        int z = (int) Math.round(b.getZ() + direction.getZ());

        Location loc = new Location(Script.WORLD, x, y, z);
        Block blockInFront = loc.getBlock();
        Slab slabInFront = (Slab) blockInFront.getBlockData();

        if(type != slabInFront.getType()) {
            //make the boat go up
            boat.setVelocity(new Vector(0, JUMP_HEIGHT, 0));
        } else {
            Vector boatVelocity = p.getLocation().getDirection().multiply(SPEED_MULTIPLIER).normalize();
            boat.setVelocity(boatVelocity);
        }

        // Check if there's a block in front and if it's higher than the current block
        //Debug.debug("Block in front: " + blockInFront.getType() + " at " + blockInFront.getLocation());
        /*if (!blockInFront.isEmpty() && blockAbove.isEmpty() && blockInFront.getY() > boat.getLocation().getBlockY()) {
            Debug.debug("Boat is about to jump");
            // Adjust boat velocity to make it jump
            boat.setVelocity(boat.getVelocity().setY(0.5)); // Adjust the Y velocity as needed
        } else {*/
            //Debug.debug("Boat is not about to jump");
            // Modify boat speed without making it jump
            // You can adjust the boat's velocity here based on your requirements
            // For example, increase or decrease the velocity
        //}
    }


    @EventHandler
    public void onEnter(VehicleEnterEvent e) {
        if (e.getEntered() instanceof Player) {
            Debug.debug("Player entered vehicle");
            Player p = (Player) e.getEntered();
            if (e.getVehicle() instanceof Boat) {
                Debug.debug("Vehicle is boat");
                Car car = Car.getCarByEntityID(e.getVehicle().getEntityId());
                if (car != null) {
                    Debug.debug("Car is not null");
                    Car.setCarSidebar(p, car);
                }
            }
        }
    }

    @EventHandler
    public void onExit(VehicleExitEvent e) {
        Cache.loadScoreboard((Player) e.getExited());
    }

}
