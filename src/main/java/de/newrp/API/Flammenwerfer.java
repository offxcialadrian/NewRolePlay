package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Flammenwerfer implements Listener {

    public static final HashMap<String, Long> flammilast = new HashMap<>();
    public static final HashMap<String, BukkitTask> flammireleasetask = new HashMap<>();
    public static final HashMap<String, BukkitTask> flammifinaltask = new HashMap<>();
    public static final HashMap<String, Boolean> flammiloading = new HashMap<>();
    public static final HashMap<String, Boolean> flammiloaded = new HashMap<>();
    public static final HashMap<String, Long> flammiloadingcounter = new HashMap<>();
    public static final HashMap<String, String> flammireloadmsg = new HashMap<>();

    @EventHandler
    public static void onFlammi(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType() == Material.BLAZE_POWDER) {
                ItemStack flammi = player.getInventory().getItemInMainHand();
                ItemMeta meta = flammi.getItemMeta();
                String strlore = String.valueOf(meta.getLore());
                String[] ammos = strlore.split("/");
                ammos[0] = ammos[0].substring(3, ammos[0].length() - 2).replace("§", "");
                int ammo = Integer.parseInt(ammos[0]);
                if (ammo == 0) {
                    event.setCancelled(true);
                    return;
                }
                flammilast.putIfAbsent(player.getName(), 1L);
                flammiloaded.putIfAbsent(player.getName(), Boolean.FALSE);
                flammiloading.putIfAbsent(player.getName(), Boolean.FALSE);
                flammiloadingcounter.putIfAbsent(player.getName(), 100L);
                flammireleasetask.putIfAbsent(player.getName(), null);
                flammifinaltask.putIfAbsent(player.getName(), null);
                flammireloadmsg.putIfAbsent(player.getName(), ChatColor.translateAlternateColorCodes('&', "&8&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛"));
                if (flammilast.get(player.getName()) > 0L) {
                    if (System.currentTimeMillis() - flammiloadingcounter.get(player.getName()) >= 1050L)
                        if (flammiloading.get(player.getName())) {
                            flammireloadmsg.put(player.getName(), flammireloadmsg.get(player.getName()).replaceFirst("8", "a"));
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(flammireloadmsg.get(player.getName())));
                            flammiloadingcounter.put(player.getName(), flammilast.get(player.getName()));
                        } else if (flammiloaded.get(player.getName())) {
                            ArrayList<String> lore = new ArrayList<>();
                            String templore = ChatColor.translateAlternateColorCodes('&', "&6" + (ammo - 1) + "&8/&6" + 500);
                            lore.add(templore);
                            meta.setLore(lore);
                            flammi.setItemMeta(meta);
                            flammiloadingcounter.put(player.getName(), flammilast.get(player.getName()));
                        }
                    if (flammifinaltask.get(player.getName()) == null)
                        flammifinaltask.put(player.getName(), Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(NewRoleplayMain.getInstance(), () -> {
                            if (flammiloading.get(player.getName()) && !(Boolean) flammiloaded.get(player.getName())) {
                                flammiloaded.put(player.getName(), Boolean.TRUE);
                                flammiloading.put(player.getName(), Boolean.FALSE);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&a⬛&a⬛&a⬛&a⬛&a⬛&a⬛&a⬛&a⬛&a⬛&a⬛")));
                            }
                            flammifinaltask.put(player.getName(), null);
                        }, 200L));
                    if (System.currentTimeMillis() - flammilast.get(player.getName()) <= 250L) {
                        if (flammireleasetask.get(player.getName()) != null && Bukkit.getServer().getScheduler().isQueued(flammireleasetask.get(player.getName()).getTaskId()))
                            Bukkit.getServer().getScheduler().cancelTask(flammireleasetask.get(player.getName()).getTaskId());
                        flammireleasetask.put(player.getName(), Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(NewRoleplayMain.getInstance(), () -> {
                            flammireloadmsg.put(player.getName(), ChatColor.translateAlternateColorCodes('&', "&8&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛&8⬛"));
                            flammiloading.put(player.getName(), Boolean.FALSE);
                            flammiloaded.put(player.getName(), Boolean.FALSE);
                            if (flammifinaltask.get(player.getName()) != null && Bukkit.getServer().getScheduler().isQueued(flammifinaltask.get(player.getName()).getTaskId())) {
                                Bukkit.getServer().getScheduler().cancelTask(flammifinaltask.get(player.getName()).getTaskId());
                                flammifinaltask.put(player.getName(), null);
                            }
                        }, 40L));
                    } else {
                        flammiloading.put(player.getName(), Boolean.TRUE);
                    }
                }
                flammilast.put(player.getName(), System.currentTimeMillis());
                if (flammiloaded.get(player.getName())) {
                    Location origin = player.getEyeLocation().subtract(0.0D, 0.25D, 0.0D);
                    Vector direction = origin.getDirection();
                    direction.normalize();
                    for (int i = 0; i < 20.0D; i++) {
                        Location loc = origin.add(direction);
                        loc.getWorld().spawnParticle(Particle.FLAME, loc.subtract(direction.clone().multiply(0.75D)), 1, 0.05D, 0.05D, 0.05D, 0.0D);
                    }
                    List<Entity> nearbyE = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
                    ArrayList<LivingEntity> livingE = new ArrayList<>();
                    for (Entity e : nearbyE) {
                        if (e instanceof LivingEntity)
                            livingE.add((LivingEntity) e);
                    }
                    BlockIterator bItr = new BlockIterator(player, 5);
                    while (bItr.hasNext()) {
                        Block block = bItr.next();
                        int bx = block.getX();
                        int by = block.getY();
                        int bz = block.getZ();
                        for (LivingEntity e : livingE) {
                            if (e instanceof Player) {
                                if (AFK.isAFK((Player) e)) continue;
                                if (SDuty.isSDuty((Player) e)) continue;
                                if (Spawnschutz.isInSpawnschutz((Player) e)) continue;
                            }
                            Location loc = e.getLocation();
                            double ex = loc.getX();
                            double ey = loc.getY();
                            double ez = loc.getZ();
                            if (bx - 0.75D <= ex && ex <= bx + 1.75D && bz - 0.75D <= ez && ez <= bz + 1.75D && (by - 1) <= ey && ey <= by + 2.5D) {
                                e.damage(3.0D);
                                e.setFireTicks(200);
                            }
                        }
                    }
                }
            }
        }
    }
}
