package de.newrp.features.bizwar.listener;

import de.newrp.API.Friedhof;
import de.newrp.API.Script;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BizWarPlayerDamageListener implements Listener {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        Player damager = null;
        if(event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if(event.getDamager() instanceof Arrow) {
            damager = (Player) ((Arrow) event.getDamager()).getShooter();
        }

        if(damager == null) {
            event.setDamage(0);
            return;
        }

        if(!bizWarService.isMemberOfBizWar(damager) && bizWarService.isMemberOfBizWar(player)) {
            event.setCancelled(true);
            damager.sendMessage(this.bizWarService.getPrefix() + "§cDu darfst keine Spieler angreifen, die sich im BizWar befinden.");
        } else if(bizWarService.isMemberOfBizWar(damager) && !bizWarService.isMemberOfBizWar(player)) {
            event.setCancelled(true);
            damager.sendMessage(this.bizWarService.getPrefix() + "§cDu darfst keine Spieler angreifen, die sich nicht im BizWar befinden.");
        }

        if(!Friedhof.isDead(player)) {
            return;
        }
        final Location location = player.getLocation().clone();
        location.add(0, 0.1f, 0);
        Script.WORLD.playEffect(location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    }
}
