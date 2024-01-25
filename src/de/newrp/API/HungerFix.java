package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Player.AFK;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class HungerFix implements Listener {
    public static final HashMap<String, Long> cooldowns = new HashMap<>();



    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        if(e.getFoodLevel() > ((Player) e.getEntity()).getFoodLevel()) {
            return;
        }
        int i = Script.getRandom(1, 40);
        Player p = (Player) e.getEntity();
        if (AFK.isAFK(p) || SDuty.isSDuty(p)) {
            e.setCancelled(true);
        } else {
            long time = System.currentTimeMillis();
            Long lastUsage = cooldowns.get(p.getName());
            if (cooldowns.containsKey(p.getName())) {
                if (lastUsage + 2000 > time) {
                    return;
                }
            }
            e.setCancelled(i != 1);
        }
    }
}