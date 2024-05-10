package de.newrp.features.roadblocks.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.roadblocks.IFactionBlockService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class FactionBlockDropItemListener implements Listener {

    private final IFactionBlockService factionBlockService = DependencyContainer.getContainer().getDependency(IFactionBlockService.class);

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if(itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if(itemStack.getItemMeta() == null) {
            return;
        }

        if(!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(this.factionBlockService.getItem().getItemMeta().getDisplayName())) {
            return;
        }

        event.setCancelled(true);
    }

}
