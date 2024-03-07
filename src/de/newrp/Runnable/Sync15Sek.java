package de.newrp.Runnable;

import de.newrp.API.Achievement;
import de.newrp.Player.UBahn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Sync15Sek extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(UBahn.isInSubway(p)) {
                Achievement.UBAHN.grant(p);
                UBahn.driveToNextStop(p);
            }
        }
    }
}
