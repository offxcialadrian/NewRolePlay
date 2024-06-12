package de.newrp.Runnable;

import de.newrp.API.*;
import de.newrp.GFB.Tabakplantage;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.LabBreakIn;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class SyncHour extends BukkitRunnable {
    @Override
    public void run() {
        try {
            if(LabBreakIn.brokeIn == null) LabBreakIn.repairDoors(false);
            Weather.updateWeather();
            Schwarzmarkt.spawnRandom();
            Dealer.respawn();
            Hologram.reload();
            Tabakplantage.respawnPlantage(new Location(Script.WORLD, 105, 65.0, 625), new Location(Script.WORLD, 118, 67, 656));
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }
}
