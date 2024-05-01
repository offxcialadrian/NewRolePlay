package de.newrp.API;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class AntiFireSpread implements Listener {

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void fireDamageControl(BlockSpreadEvent event) {
        if (event.getNewState().getType().equals(Material.FIRE)) {
            event.setCancelled(true);
        }
    }
}
