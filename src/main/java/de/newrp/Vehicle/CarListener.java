package de.newrp.Vehicle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CarListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Car.spawnCars(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Car.deleteCars(e.getPlayer());
    }
}
