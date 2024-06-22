package de.newrp.API;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

}
