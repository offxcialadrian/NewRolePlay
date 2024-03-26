package de.newrp.Administrator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class AntiLeftHand implements Listener {
    @EventHandler
    public void onClick(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
        e.setOffHandItem(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getRawSlot() == 45) {
            e.setCancelled(true);
            e.getWhoClicked().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
    }
}
