package de.newrp.features.emergencycall.inventory;

import de.newrp.API.ItemBuilder;
import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EmergencyCallFactionSelectionInventory implements Listener {

    private final Inventory inventory;

    public EmergencyCallFactionSelectionInventory() {
        this.inventory = Bukkit.createInventory(new EmergencyCallFactionSelectionInventoryHolder(), 9, "Setze deinen Notruf ab!");
        this.fillInventory();
    }

    private void fillInventory() {
        this.inventory.setItem(0, new ItemBuilder(Material.OAK_SIGN)
                .setName("§9Polizei")
                .build());

        this.inventory.setItem(1, new ItemBuilder(Material.OAK_SIGN)
                .setName("§4Rettungsdienst")
                .build());
    }

    public void openToPlayer(final Player player) {
        player.closeInventory();
        player.openInventory(this.inventory);
    }

    @Getter
    public static class EmergencyCallFactionSelectionInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }




}
