package de.newrp.Runnable;

import de.newrp.API.DaylightCycle;
import de.newrp.API.Debug;
import de.newrp.API.Script;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncDaylightCycle extends BukkitRunnable {
    @Override
    public void run() {
        DaylightCycle.refreshDaylight();
    }
}
