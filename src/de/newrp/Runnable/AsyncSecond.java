package de.newrp.Runnable;

import de.newrp.API.Particle;
import de.newrp.API.Script;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Gangwar.GangwarZones;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncSecond extends BukkitRunnable {
    @Override
    public void run() {
        /*for (GangwarZones z : GangwarCommand.gangwar.keySet()) {
            if (GangwarCommand.gangwarIsActive(z)) {
                for (Location loc : z.getCapturePoints()) {
                    for (Location l : Script.circle(loc, .6D, 20)) {
                        for (Player p : GangwarCommand.getMember(z)) {
                            new Particle(org.bukkit.Particle.FIREWORKS_SPARK, l, true, 0, 0, 0, 0, 1).sendPlayer(p);
                        }
                    }
                }
            }
        }*/
    }
}
