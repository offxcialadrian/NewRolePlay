package de.newrp.API;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecartRideable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.vehicle.VehicleEvent;

import java.util.Set;

public class CarMoveEvent extends VehicleEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Player p;
    private final Set<CarDirection> directions;

    public CarMoveEvent(CraftMinecartRideable vehicle, Player p, Set<CarDirection> directions) {
        super(vehicle);
        this.p = p;
        this.directions = directions;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Set<CarDirection> getDirections() {
        return this.directions;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public enum CarDirection {
        UNKNOWN(),
        FORWARD(),
        BACKWARD(),
        LEFT(),
        RIGHT(),
        BRAKE()
    }
}
