package de.newrp.Runnable;

import de.newrp.API.Hologram;
import de.newrp.API.Schwarzmarkt;
import de.newrp.API.Script;
import de.newrp.API.Weather;
import de.newrp.GFB.Tabakplantage;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.LabBreakIn;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class SyncHour extends BukkitRunnable {
    @Override
    public void run() {
        try {
            LabBreakIn.repairDoors(false);
            Weather.updateWeather();
            Schwarzmarkt.spawnRandom();
            Hologram.reload();
            Tabakplantage.respawnPlantage(new Location(Script.WORLD, 105, 65.0, 625), new Location(Script.WORLD, 118, 67, 656));
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }
}
