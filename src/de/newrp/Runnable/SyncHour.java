package de.newrp.Runnable;

import de.newrp.API.Weather;
import org.bukkit.scheduler.BukkitRunnable;

public class SyncHour extends BukkitRunnable {
    @Override
    public void run() {
        Weather.updateWeather();
    }
}
