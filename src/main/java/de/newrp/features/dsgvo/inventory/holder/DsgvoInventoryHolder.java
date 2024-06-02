package de.newrp.features.dsgvo.inventory.holder;

import de.newrp.features.dsgvo.inventory.DsgvoInventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class DsgvoInventoryHolder implements InventoryHolder {

    private final Player player;
    private final DsgvoInventory dsgvoInventory;

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
