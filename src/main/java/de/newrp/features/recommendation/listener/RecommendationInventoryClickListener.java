package de.newrp.features.recommendation.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.recommendation.IRecommendationService;
import de.newrp.features.recommendation.inventory.RecommendationInventory;
import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RecommendationInventoryClickListener implements Listener {

    private final IRecommendationService recommendationService = DependencyContainer.getContainer().getDependency(IRecommendationService.class);

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if(event.getClickedInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getInventory().getHolder() instanceof RecommendationInventoryHolder) {
            event.setCancelled(true);

            if(event.getCurrentItem().getType() != Material.PAPER) {
                return;
            }

            recommendationService.giveRecommendation((Player) event.getWhoClicked(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
            recommendationService.closeInventory((Player) event.getWhoClicked());
            event.getWhoClicked().sendMessage("§8[§6§lEmpfehlung§8] §6§l» §7Du hast die Empfehlung erfolgreich abgegeben. Vielen Dank!");
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

}
