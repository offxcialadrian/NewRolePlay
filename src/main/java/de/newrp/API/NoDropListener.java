package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class NoDropListener implements Listener {

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if(new ItemBuilder(itemStack).isNoDrop()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSwitch(final InventoryClickEvent event) {
        if(event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            final ItemStack itemStack = event.getCurrentItem();
            if(itemStack == null) {
                return;
            }

            if(new ItemBuilder(itemStack).isNoDrop()) {
                event.setCancelled(true);
            }
        }
    }

}
