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

            final Player player = (Player) event.getWhoClicked();
            final String value = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            if(value.equalsIgnoreCase("Sonstiges")) {
                recommendationService.closeInventory(player, (RecommendationInventoryHolder) event.getInventory().getHolder());
                recommendationService.activateChatInput(player);
                event.getWhoClicked().sendMessage("§8[§6§lMarktforschung§8] §6§l» §7Bitte gebe in den Chat ein, woher du von NewRoleplay mitbekommen hast.");
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                return;
            }

            recommendationService.giveRecommendation(player, value);
            recommendationService.closeInventory(player, (RecommendationInventoryHolder) event.getInventory().getHolder());
            event.getWhoClicked().sendMessage("§8[§6§lMarktforschung§8] §6§l» §7Vielen Dank für deine Rückmeldung!");
            player.playSound(event.getWhoClicked().getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

}
