package de.newrp.Runnable;

import de.newrp.API.DaylightCycle;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncDaylightCycle extends BukkitRunnable {
    @Override
    public void run() {
        DaylightCycle.refreshDaylight();
    }
}
