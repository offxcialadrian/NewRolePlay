package de.newrp.features.roadblocks.listener;

import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.roadblocks.IFactionBlockService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class FactionBlockQuitListener implements Listener {

    private final IFactionBlockService factionBlockService = DependencyContainer.getContainer().getDependency(IFactionBlockService.class);

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Beruf.Berufe playerFaction = Beruf.getBeruf(event.getPlayer());
        final Player activePlayerInSystem = this.factionBlockService.isSystemActive(playerFaction);

        if(activePlayerInSystem != null && activePlayerInSystem.getUniqueId() == event.getPlayer().getUniqueId()) {
            this.factionBlockService.deactivateSystemForPlayer(event.getPlayer(), playerFaction);
        }
    }
}
