package de.newrp.features.deathmatcharena.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DeathmatchQuitListener implements Listener {

    private final IDeathmatchArenaService deathmatchArenaService = DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class);

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

       deathmatchArenaService.isInDeathmatch(player, true);
    }
}
