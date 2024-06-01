package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EventListener implements Listener {

    Event e = NewRoleplayMain.event;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if(e == Event.NO_DAMAGE) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPVE(EntityDamageByEntityEvent event) {
        if(e == Event.NO_DAMAGE) {
            if(event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

}
