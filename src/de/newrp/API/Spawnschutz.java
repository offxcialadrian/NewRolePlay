package de.newrp.API;

import de.newrp.Ticket.TicketCommand;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

public class Spawnschutz implements Listener {
    public static final HashMap<String, Long> spawnschutz = new HashMap<>();
    public static final HashMap<String, Long> cooldown = new HashMap<>();
    private static String PREFIX = "§8[§6Spawnschutz§8] §6" + Messages.ARROW + " ";

    public static boolean isInSpawnschutz(Player p) {
        return spawnschutz.containsKey(p.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.put(e.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        long time = System.currentTimeMillis();
        Long lastUsage = cooldown.get(p.getName());
        if (cooldown.containsKey(p.getName())) {
            if (lastUsage + 60 * 1000 > time) {
                return;
            }
        }
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            if (spawnschutz.containsKey(p.getName())) {
                e.getPlayer().sendMessage(PREFIX + "Spawnschutz ist vorbei!");
                spawnschutz.remove(e.getPlayer().getName());
            }
        }, 60 * 20L);
        spawnschutz.put(e.getPlayer().getName(), time);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            if (spawnschutz.containsKey(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(PREFIX + "Spawnschutz ist vorbei!");
                spawnschutz.remove(e.getPlayer().getName());
            }
        }, 60 * 20L);
        spawnschutz.put(e.getPlayer().getName(), time);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            if (spawnschutz.containsKey(p.getName())) {
                long time = System.currentTimeMillis();
                Long lastUsage = spawnschutz.get(p.getName());
                if (lastUsage + 60 * 1000L > time) {
                    e.setCancelled(true);
                }
            }

            if (e.getDamager().getType().equals(EntityType.PLAYER)) {
                Player p1 = (Player) e.getDamager();
                if (spawnschutz.containsKey(p1.getName())) {
                    long time = System.currentTimeMillis();
                    Long lastUsage = spawnschutz.get(p1.getName());
                    if (lastUsage + 60 * 1000L > time) {
                        spawnschutz.remove(p1.getName());
                        p1.sendMessage(PREFIX + "Spawnschutz ist vorbei!");
                    }
                }
            } else if (e.getDamager().getType().equals(EntityType.ARROW)) {
                Arrow a = (Arrow) e.getDamager();
                Player p1 = (Player) a.getShooter();
                if (spawnschutz.containsKey(p1.getName())) {
                    long time = System.currentTimeMillis();
                    Long lastUsage = spawnschutz.get(p1.getName());
                    if (lastUsage + 60 * 1000L > time) {
                        spawnschutz.remove(p1.getName());
                        p1.sendMessage(PREFIX + "Spawnschutz ist vorbei!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFall(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && (e.getEntity().getType().equals(EntityType.PLAYER))) {
            Entity p = e.getEntity();
            if (spawnschutz.containsKey(p.getName())) {
                long time = System.currentTimeMillis();
                Long lastUsage = spawnschutz.get(p.getName());
                if (lastUsage + 60 * 1000L > time) {
                    e.setCancelled(true);
                } else {
                    spawnschutz.remove(p.getName());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;

        Entity damager = e.getDamager();
        Player p = (Player) e.getEntity();

        if (!Spawnschutz.isInSpawnschutz(p)) return;

        if (damager.getType() == EntityType.PLAYER) {
            damager.sendMessage("§8[§6Spawnschutz§8] §6 "+ Messages.ARROW + " Der Spieler ist im Spawnschutz.");
        } else if (damager.getType() == EntityType.ARROW) {
            Arrow a = (Arrow) damager;
            ((Player) a.getShooter()).sendMessage("§8[§6Spawnschutz§8] §6 "+ Messages.ARROW + " Der Spieler ist im Spawnschutz.");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage1(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;

        Entity damager = e.getDamager();
        Player p = (Player) e.getEntity();

        if (Script.getLevel(p)!=1 && Script.getLevel((Player) damager)!=1) return;

        e.setCancelled(true);
        if (damager.getType() == EntityType.PLAYER) {
            damager.sendMessage("§8[§cNeulingsschutz§8] §c "+ Messages.ARROW + " Der Spieler ist im Neulingsschutz.");
        } else if (damager.getType() == EntityType.ARROW) {
            Arrow a = (Arrow) damager;
            ((Player) a.getShooter()).sendMessage("§8[§cNeulingsschutz§8] §c "+ Messages.ARROW + " Der Spieler ist im Neulingsschutz.");
        }
    }

    @EventHandler
    public void reportWarning(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;

        Entity damager = e.getDamager();
        Player p = (Player) e.getEntity();

        if(!TicketCommand.isInTicket(p)) return;

        if (damager.getType() == EntityType.PLAYER) {
            damager.sendMessage("§8[§cTicket§8] §c "+ Messages.ARROW + " Der Spieler ist im Ticket.");
        } else if (damager.getType() == EntityType.ARROW) {
            Arrow a = (Arrow) damager;
            ((Player) a.getShooter()).sendMessage("§8[§cTicket§8] §c "+ Messages.ARROW + " Der Spieler ist im Ticket.");
        }
    }

}
