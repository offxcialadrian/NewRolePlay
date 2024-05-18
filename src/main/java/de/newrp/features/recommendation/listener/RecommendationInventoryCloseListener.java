package de.newrp.features.recommendation.listener;

import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.recommendation.IRecommendationService;
import de.newrp.features.recommendation.inventory.RecommendationInventory;
import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class RecommendationInventoryCloseListener implements Listener {

    private final IRecommendationService recommendationService = DependencyContainer.getContainer().getDependency(IRecommendationService.class);

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if(!(event.getPlayer() instanceof Player)) {
            return;
        }

        if (inventory.getHolder() instanceof RecommendationInventoryHolder) {
            if(recommendationService.hasActiveChatInput((Player) event.getPlayer())) {
                return;
            }

            if(!((RecommendationInventoryHolder) inventory.getHolder()).isClosed()) {
                System.out.println("Not closed");
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                    event.getPlayer().openInventory(inventory);
                }, 2L);
            }
        }
    }

}
