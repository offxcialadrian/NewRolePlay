package de.newrp.Medic;

import java.util.HashMap;

import de.newrp.API.Friedhof;
import de.newrp.API.Particle;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Feuerloescher implements Listener {
    final HashMap<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            Player p = e.getPlayer();
            ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
            if (is.getType().equals(Material.LEVER) && is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Feuerlöscher")) {
                e.setCancelled(true);
                int amount = Script.getFeuerloescher(is);
                if (amount > 0) {
                    long time = System.currentTimeMillis();
                    Long lastUsage = cooldowns.get(p.getName());
                    if (cooldowns.containsKey(p.getName())) {
                        if (lastUsage + 0.2D * 1000 > time) {
                            return;
                        }
                    }
                    Location loc = p.getEyeLocation();
                    double maxLength = 8;
                    Script.playLocalSound(loc, Sound.WEATHER_RAIN, 5, 1F, 20F);
                    Script.playLocalSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 5, 1F, 1F);
                    Particle particle = new Particle(org.bukkit.Particle.CLOUD, loc, false, .22F, .22F, .22F, .01F, 4);
                    for (double d = 0; d <= maxLength; d += 0.4) {
                        loc.add(loc.getDirection().multiply(.2D));

                        particle.setLocation(loc);
                        particle.sendAll();
                        if (loc.getBlock().getType().equals(Material.FIRE)) {
                            if (FeuerwehrEinsatz.onFire.contains((loc.getBlock()))) {
                                FeuerwehrEinsatz.removeFire(loc.getBlock());
                                if (FeuerwehrEinsatz.onFire.isEmpty()) {
                                    FeuerwehrEinsatz.house = null;
                                    FeuerwehrEinsatz.onFire.clear();
                                }
                            }
                        }
                    }
                    cooldowns.put(p.getName(), System.currentTimeMillis());
                    p.getInventory().setItemInMainHand(Script.feuerloescher(is, amount - 1));
                } else {
                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    p.sendMessage("§7Der Feuerlöscher ist leer.");
                }
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
            Block b = e.getClickedBlock();
            if (is.getType().equals(Material.LEVER) && is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Feuerlöscher")) {
                e.setCancelled(true);
                int amount = Script.getFeuerloescher(is);
                if (amount > 0) {
                    long time = System.currentTimeMillis();
                    Long lastUsage = cooldowns.get(p.getName());
                    if (cooldowns.containsKey(p.getName())) {
                        if (lastUsage + 0.2D * 1000 > time) {
                            return;
                        }
                    }
                    Location loc = p.getEyeLocation();
                    double maxLength = 8;
                    Script.playLocalSound(loc, Sound.WEATHER_RAIN, 5, 1F, 20F);
                    Script.playLocalSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 5, 1F, 1F);
                    Particle particle = new Particle(org.bukkit.Particle.CLOUD, loc, false, .22F, .22F, .22F, .01F, 4);
                    for (double d = 0; d <= maxLength; d += 0.4) {
                        loc.add(loc.getDirection().multiply(.2D));

                        particle.setLocation(loc);
                        particle.sendAll();
                        if (loc.getBlock().getType().equals(Material.FIRE)) {
                            if (FeuerwehrEinsatz.onFire.contains((loc.getBlock()))) {
                                FeuerwehrEinsatz.removeFire(loc.getBlock());
                                if (FeuerwehrEinsatz.onFire.isEmpty()) {
                                    FeuerwehrEinsatz.house = null;
                                    FeuerwehrEinsatz.onFire.clear();
                                }
                            }
                        }
                    }
                    cooldowns.put(p.getName(), System.currentTimeMillis());
                    p.getInventory().setItemInMainHand(Script.feuerloescher(is, amount - 1));
                } else {
                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    p.sendMessage("§7Der Feuerlöscher ist leer.");
                }
            }
        }
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (e.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
                e.setCancelled(true);
                e.getClickedBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
                if (!Friedhof.isDead(p)) p.setFireTicks(60);
            }
        }
    }
}