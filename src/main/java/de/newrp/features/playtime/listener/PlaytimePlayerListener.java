package de.newrp.features.playtime.listener;

import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.playtime.IPlaytimeService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimePlayerListener implements Listener {

    private final IPlaytimeService playtimeService = DependencyContainer.getContainer().getDependency(IPlaytimeService.class);

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            playtimeService.loadPlaytime(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            playtimeService.handleQuit(event.getPlayer());
        });
    }

}
