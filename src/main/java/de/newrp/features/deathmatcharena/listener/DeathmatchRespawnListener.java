package de.newrp.features.deathmatcharena.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathmatchRespawnListener implements Listener {

    private final IDeathmatchArenaService deathmatchArenaService = DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class);

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        if(this.deathmatchArenaService.isInDeathmatch(player, false)) {
            event.setRespawnLocation(this.deathmatchArenaService.getRandomSpawnPoint());
            player.getInventory().clear();
            this.deathmatchArenaService.equipWeaponsAndDrugs(player);
        }
    }

}
