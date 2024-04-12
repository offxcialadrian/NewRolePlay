package de.newrp.API;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MuscleDamage implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDMG(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType().equals(EntityType.PLAYER) && e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player damager = (Player) e.getDamager();
            int lvl = Health.getMuscleLevel(Script.getNRPID(damager));
            if (lvl == 0) {
                e.setDamage(0D);
                e.setCancelled(true);
            } else {
                double[] damage = {0, .5, 1};
                e.setDamage(damage[lvl]);
            }
        }
    }
}
