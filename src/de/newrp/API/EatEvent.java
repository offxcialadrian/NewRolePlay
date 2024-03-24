package de.newrp.API;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class EatEvent implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getItem() == null) return;
        Player p = (Player) e.getEntity();
        if(e.getItem().getType() == Material.COOKIE) Health.FAT.add(Script.getNRPID(p), Script.getRandomFloat(.01F, .02F));
    }

}
