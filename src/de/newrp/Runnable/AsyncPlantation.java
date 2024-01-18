package de.newrp.Runnable;

import de.newrp.Organisationen.Plantage;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncPlantation extends BukkitRunnable {
    @Override
    public void run() {
        Plantage.tick();
    }
}