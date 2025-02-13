package de.newrp.Runnable;

import de.newrp.API.Friedhof;
import de.newrp.API.Health;
import de.newrp.API.Krankheit;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.Checkpoints;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Drone;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Player.AFK;
import de.newrp.Police.Jail;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncHealth extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (AFK.isAFK(p)) continue;
            if (Health.BLEEDING.containsKey(p.getName()) && !GangwarCommand.isInGangwar(p)) {
                if(Script.getRandom(1, 100) <20) {
                    p.sendMessage(Health.PREFIX + "Deine Wunde hat sich entzündet...");
                    Krankheit.ENTZUENDUNG.add(Script.getNRPID(p));
                }
                float amount = Health.BLEEDING.get(p.getName());
                int id = Script.getNRPID(p);
                Health.BLOOD.remove(id, amount);
                final float f = amount;
                Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> p.damage(f < 1F ? .5D : 1D));
                if (amount < 1F) {
                    if(Script.getRandom(0, 1) == 0)
                    p.sendMessage(Health.PREFIX + "Du blutest leicht...");
                } else {
                    p.sendMessage(Health.PREFIX + "Du blutest stark...");
                }
            } else {
                Health.BLOOD.add(Script.getNRPID(p), Script.getRandomFloat(.2F, .3F));
            }
            if (!AFK.isAFK(p) && !Jail.isInJail(p) && !Friedhof.isDead(p) && !SDuty.isSDuty(p) && !BuildMode.isInBuildMode(p) && !Checkpoints.hasCheckpoints(p) && !Drone.isDrone(p)) {
                Health.THIRST.remove(Script.getNRPID(p), Script.getRandomFloat(.09F, .12F));
            }
        }
    }
}
