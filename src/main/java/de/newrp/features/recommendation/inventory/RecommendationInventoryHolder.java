package de.newrp.features.recommendation.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class RecommendationInventoryHolder implements InventoryHolder {

    private final Player player;

    @Setter
    private boolean closed;

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
