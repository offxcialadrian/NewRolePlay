package de.newrp.Runnable;

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
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }
}
