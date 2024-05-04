package de.newrp.API;

import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Flashbang implements Listener {
    final HashMap<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (e.getItemDrop().getItemStack().equals(Script.flashbang())) {
            long time = System.currentTimeMillis();
            Long lastUsage = cooldowns.get(e.getPlayer().getName());
            if (cooldowns.containsKey(e.getPlayer().getName())) {
                if (lastUsage + 10 * 1000 > time) {
                    e.setCancelled(true);
                    return;
                }
            }
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            cooldowns.put(e.getPlayer().getName(), time);
            e.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                for (Entity ent : e.getItemDrop().getNearbyEntities(10D, 5D, 10D)) {
                    if (ent instanceof Player) {
                        Player p = (Player) ent;
                        int distance = (int) e.getItemDrop().getLocation().distance(p.getLocation());
                        if (distance <= 4) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 4 * 20, 1), false);
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Math.abs(13 - distance) * 40, 1), false);
                    }
                }
                e.getItemDrop().remove();
            }, 2 * 27L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getPlayer().getInventory().getItemInMainHand().equals(Script.flashbang())) {
                long time = System.currentTimeMillis();
                Long lastUsage = cooldowns.get(e.getPlayer().getName());
                if (cooldowns.containsKey(e.getPlayer().getName())) {
                    if (lastUsage + 10 * 1000 > time) {
                        e.setCancelled(true);
                        return;
                    }
                }
                cooldowns.put(e.getPlayer().getName(), time);
                e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                final Item i = e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Script.flashbang());
                i.setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.2F));
                i.setPickupDelay(Integer.MAX_VALUE);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    for (Entity ent : i.getNearbyEntities(10D, 5D, 10D)) {
                        if (ent instanceof Player) {
                            Player p = (Player) ent;
                            int distance = (int) i.getLocation().distance(p.getLocation());
                            if (distance <= 4) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2 * 200, 1), false);
                            }
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Math.abs(10 - distance) * 20, 1), false);
                        }
                    }
                    i.remove();
                }, 2 * 27L);
            }
        }
    }
}