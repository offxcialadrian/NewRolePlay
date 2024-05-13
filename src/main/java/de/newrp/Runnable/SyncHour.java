package de.newrp.Runnable;

import de.newrp.API.Hologram;
import de.newrp.API.Schwarzmarkt;
import de.newrp.API.Weather;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.LabBreakIn;
import org.bukkit.scheduler.BukkitRunnable;

public class SyncHour extends BukkitRunnable {
    @Override
    public void run() {
        try {
            LabBreakIn.repairDoors(false);
            Weather.updateWeather();
            Schwarzmarkt.spawnRandom();
            Hologram.reload();
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }
}
