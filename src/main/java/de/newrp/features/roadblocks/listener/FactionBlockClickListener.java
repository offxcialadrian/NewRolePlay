package de.newrp.features.roadblocks.listener;

import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.roadblocks.IFactionBlockService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FactionBlockClickListener implements Listener {

    private final IFactionBlockService factionBlockService = DependencyContainer.getContainer().getDependency(IFactionBlockService.class);
    private long lastUsed = System.currentTimeMillis();


    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getClickedBlock() == null) {
            return;
        }

        final ItemStack itemStack = event.getItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if(System.currentTimeMillis() - this.lastUsed < 500) {
            return;
        }

        if(itemStack.getItemMeta() == null) {
            return;
        }

        if(!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(this.factionBlockService.getItem().getItemMeta().getDisplayName())) {
            Bukkit.getLogger().warning("not the right item D:");
            return;
        }

        final Player activePlayerForCops = factionBlockService.isSystemActive(Beruf.Berufe.POLICE);
        final Player activePlayerForMedics = factionBlockService.isSystemActive(Beruf.Berufe.RETTUNGSDIENST);

        if(activePlayerForCops == null && activePlayerForMedics == null) {
            return;
        }

        final Player player = event.getPlayer();
        final boolean isCopActiveDuty = (activePlayerForCops != null && activePlayerForCops.getUniqueId() == player.getUniqueId());
        final boolean isMedicActiveDuty = (activePlayerForMedics != null && activePlayerForMedics.getUniqueId() == player.getUniqueId());
        if(isCopActiveDuty || isMedicActiveDuty) {
            this.factionBlockService.placeFactionBlock(player, event.getClickedBlock().getLocation(), isCopActiveDuty ? Beruf.Berufe.POLICE : Beruf.Berufe.RETTUNGSDIENST);
            lastUsed = System.currentTimeMillis();
            return;
        }
    }

}
