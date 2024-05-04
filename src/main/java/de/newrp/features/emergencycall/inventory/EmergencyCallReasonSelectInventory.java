package de.newrp.features.emergencycall.inventory;

import de.newrp.API.ItemBuilder;
import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmergencyCallReasonSelectInventory {

    private final Inventory inventory;
    private final Beruf.Berufe faction;
    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    public EmergencyCallReasonSelectInventory(final Beruf.Berufe faction) {
        this.faction = faction;
        this.inventory = Bukkit.createInventory(new EmergencyCallReasonSelectInventoryHolder(faction), 9, "Warum benötigst du Hilfe?");
        this.fillInventory();
    }

    private void fillInventory() {
        for (final String reason : this.emergencyCallService.getReasonsForEmergency(this.faction)) {
            final ItemStack itemStack = new ItemBuilder(Material.PAPER)
                    .setName(reason)
                    .build();
            this.inventory.addItem(itemStack);
        }

        this.inventory.addItem(new ItemBuilder(Material.PAPER)
                .setName("Anderes")
                .build());
    }

    public void openToPlayer(final Player player) {
        player.closeInventory();
        player.openInventory(this.inventory);
    }


    @Getter
    @AllArgsConstructor
    public static class EmergencyCallReasonSelectInventoryHolder implements InventoryHolder {
        private final Beruf.Berufe faction;

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

}
