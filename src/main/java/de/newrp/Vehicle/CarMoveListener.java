package de.newrp.Vehicle;

import de.newrp.API.CarMoveEvent;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftBoat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Set;

public class CarMoveListener implements Listener {
    public static void rotateVector3D(Vector vector, double angle) {
        rotateVector3DRadians(vector, Math.toRadians(angle));
    }

    private static void rotateVector3DRadians(Vector vector, double angleInRadians) {
        double x = vector.getX();
        double z = vector.getZ();

        double xRotated = x * Math.cos(angleInRadians) - z * Math.sin(angleInRadians);
        double zRotated = x * Math.sin(angleInRadians) + z * Math.cos(angleInRadians);

        vector.setX(xRotated);
        vector.setZ(zRotated);
    }

    @EventHandler
    public void onMove(CarMoveEvent e) {
        Set<CarMoveEvent.CarDirection> d = e.getDirections();

        CraftBoat mc = (CraftBoat) e.getVehicle();
        mc.setMaxSpeed(10D);


        float angle = 0;

        if (d.equals(CarMoveEvent.CarDirection.BRAKE)) {

        } else if (d.equals(CarMoveEvent.CarDirection.FORWARD)) {
            Vector vector = mc.getLocation().getDirection().multiply(2D);
            rotateVector3D(vector, 90);
            vector.setY(0);
            mc.setVelocity(vector);
        } else if (d.equals(CarMoveEvent.CarDirection.BACKWARD)) {
            Vector vector = mc.getLocation().getDirection().multiply(-.8D);
            rotateVector3D(vector, 90);
            vector.setY(0);
            mc.setVelocity(vector);
        } else if (d.equals(CarMoveEvent.CarDirection.LEFT)) {
            angle = -10F;
        } else if (d.equals(CarMoveEvent.CarDirection.RIGHT)) {
            angle = 10F;
        }

        if (angle != 0) {
            Location loc = mc.getLocation();
            mc.getHandle().setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw() + angle, 0);
        }

    }
}