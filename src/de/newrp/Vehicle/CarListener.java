package de.newrp.Vehicle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CarListener implements Listener {

    //TODO Von den Script funktionen weg und Code direkt hier rein
    //TODO Asynchron spawnen (Fragt beim Login jedes mal einen Query ab -> ineffizient)

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Car.spawnCars(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Car.deleteCars(e.getPlayer());
    }
}
