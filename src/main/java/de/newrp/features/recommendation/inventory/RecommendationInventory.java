package de.newrp.features.recommendation.inventory;

import de.newrp.API.ItemBuilder;
import de.newrp.config.MainConfig;
import de.newrp.dependencies.DependencyContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RecommendationInventory {

    private final Inventory inventory;
    private final MainConfig mainConfig = DependencyContainer.getContainer().getDependency(MainConfig.class);
    private final ItemStack holderItemStack = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("").build();

    public RecommendationInventory(final Player player) {
        this.inventory = Bukkit.createInventory(new RecommendationInventoryHolder(player, false), 27, "§8» §6Wie hast du von NewRoleplay mitbekommen?");
        this.fillInventory();
    }

    private void fillInventory() {
        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, holderItemStack);
        }

        for (int i = 18; i < 27; i++) {
            this.inventory.setItem(i, holderItemStack);
        }

        for (String recommendationItem : mainConfig.getRecommendationItems()) {
            this.inventory.addItem(new ItemBuilder(Material.PAPER)
                    .setName("§e" + recommendationItem)
                    .build());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
