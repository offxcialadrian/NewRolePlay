package de.newrp.Runnable;

import de.newrp.API.Debug;
import de.newrp.API.Script;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class SyncMinute extends BukkitRunnable {
    @Override
    public void run() {
        Sign sign = (Sign) Script.WORLD.getBlockAt(447, 80, 840).getState();
        String lastdead = "seit ";
        if(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 7) {
            lastdead += TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer())/7 + " Wochen";
        } else  if(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 24) {
            lastdead += TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Tage";
        } else if(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 60) {
            lastdead += TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Stunden";
        } else {
            lastdead += TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Minuten";
        }
        sign.setLine(2, lastdead);
        sign.update();
    }
}
