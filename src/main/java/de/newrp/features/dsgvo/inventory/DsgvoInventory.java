package de.newrp.features.dsgvo.inventory;

import de.newrp.API.ItemBuilder;
import de.newrp.features.dsgvo.inventory.holder.DsgvoInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DsgvoInventory {

    private final Inventory inventory;
    private boolean accepted = false;
    private int page = 0;
    private final ItemStack holder;

    public DsgvoInventory(final Player player) {
        this.holder = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§0").build();
        this.inventory = Bukkit.createInventory(new DsgvoInventoryHolder(player, this), 27, "§c§lDSGVO");
    }

    public int getCurrentPage() {
        return this.page + 1;
    }

    public boolean hasAccepted() {
        return this.accepted;
    }

    private int getMaxPageSize() {
        return 3;
    }

    private void fillInventory() {
        for(int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, this.holder);
        }

        for(int i = 9; i < 18; i++) {
            this.inventory.setItem(i, null);
        }

        this.inventory.setItem(10, new ItemBuilder(Material.GREEN_WOOL).setName("§a§lAkzeptieren").build());
        this.inventory.setItem(16, new ItemBuilder(Material.GREEN_WOOL).setName("§a§lAkzeptieren").build());
    }

    private void preparePage() {
        int previousPageSlot = 12;
        int nextPageSlot = 14;
        int itemSlot = 13;

        if(getCurrentPage() == 1) {
            this.inventory.setItem(previousPageSlot, null);
        } else {
            this.inventory.setItem(previousPageSlot, new ItemBuilder(Material.STONE_BUTTON).setName("§cZurück").setLore("§eSeite " + (getCurrentPage() - 1)).build());
        }


        if(getCurrentPage() == getMaxPageSize()) {
            this.inventory.setItem(nextPageSlot, null);
        } else {
            this.inventory.setItem(nextPageSlot, new ItemBuilder(Material.STONE_BUTTON).setName("§aNächste Seite").setLore("§eSeite " + (getCurrentPage() + 1)).build());
        }
    }

}
