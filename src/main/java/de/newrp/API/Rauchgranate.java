package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Rauchgranate implements Listener {
    final HashMap<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(Script.rauchgranate())) {
            Player player = e.getPlayer();
            long time = System.currentTimeMillis();
            Long lastUsage = cooldowns.get(e.getPlayer().getName());
            if (cooldowns.containsKey(e.getPlayer().getName())) {
                if (lastUsage + 10 * 1000 > time) {
                    e.setCancelled(true);
                    return;
                }
            }
            cooldowns.put(e.getPlayer().getName(), time);
            e.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(NewRoleplayMain.getInstance(), new Runnable() {
                ArrayList<Location> locs = null;

                public void run() {
                    if (locs == null) {
                        locs = Script.getBlocksAroundCenter(e.getItemDrop().getLocation(), 4);
                        e.getItemDrop().remove();
                    }
                    for (Location loc : locs) {
                        new Particle(org.bukkit.Particle.CLOUD, loc, false, 0.06F, 0.06F, 0.06F, 0.12F, 5).sendAll();
                    }
                    for (int i = 0; i < 5; i++) {
                        if (locs.size() > 0) {
                            locs.remove(Script.getRandom(0, (locs.size() - 1)));
                        }
                    }
                }
            }, 50L, 5L);
            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> Bukkit.getScheduler().cancelTask(taskId), 16 * 20L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if ( e.getPlayer().getInventory().getItemInMainHand().equals(Script.rauchgranate())) {
                long time = System.currentTimeMillis();
                Long lastUsage = cooldowns.get(e.getPlayer().getName());
                if (cooldowns.containsKey(e.getPlayer().getName())) {
                    if (lastUsage + 10 * 1000 > time) {
                        e.setCancelled(true);
                        return;
                    }
                }
                cooldowns.put(e.getPlayer().getName(), time);
                e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                final Item i = e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Script.rauchgranate());
                i.setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.2F));
                i.setPickupDelay(Integer.MAX_VALUE);
                final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(NewRoleplayMain.getInstance(), new Runnable() {
                    ArrayList<Location> locs = null;

                    public void run() {
                        if (locs == null) {
                            locs = Script.getBlocksAroundCenter(i.getLocation(), 4);
                            i.remove();
                        }
                        for (Location loc : locs) {
                            new Particle(org.bukkit.Particle.CLOUD, loc, false, 0.06F, 0.06F, 0.06F, 0.12F, 5).sendAll();
                        }
                        for (int i = 0; i < 5; i++) {
                            if (locs.size() > 0) {
                                locs.remove(Script.getRandom(0, (locs.size() - 1)));
                            }
                        }
                    }
                }, 50L, 5L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> Bukkit.getScheduler().cancelTask(taskId), 16 * 20L);
            }
        }
    }
}