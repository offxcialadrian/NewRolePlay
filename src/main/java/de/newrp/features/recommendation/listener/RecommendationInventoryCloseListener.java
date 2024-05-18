package de.newrp.features.recommendation.listener;

import de.newrp.NewRoleplayMain;
import de.newrp.features.recommendation.inventory.RecommendationInventory;
import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class RecommendationInventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof RecommendationInventoryHolder) {
            if(!((RecommendationInventoryHolder) inventory.getHolder()).isClosed()) {
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                    event.getPlayer().openInventory(inventory);
                }, 2L);
            }
        }
    }

}
