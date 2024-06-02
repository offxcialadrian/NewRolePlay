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

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(NewRoleplayMain.event == Event.NO_DAMAGE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPVE(EntityDamageByEntityEvent event) {
        if(NewRoleplayMain.event == Event.NO_DAMAGE) {
            if(event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

}
