package de.newrp.Berufe;

import de.newrp.API.Cache;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Drone implements Listener {

    public static String PREFIX = "§8[§9Drohne§8]§9 " + Messages.ARROW + " §7";
    public static HashMap<String, Location> location = new HashMap<>();
    public static ArrayList<String> drone = new ArrayList<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();

    public static void start(Player p) {
        location.put(p.getName(), p.getLocation());
        drone.add(p.getName());
        cooldown.put(p.getName(), System.currentTimeMillis() + 1000L);
        Cache.saveInventory(p);
        p.getInventory().clear();
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        p.getInventory().setHelmet(new ItemStack(Material.WITHER_SKELETON_SKULL));
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFlySpeed(2f);
        p.setWalkSpeed(2f);
        p.setFoodLevel(20);
    }

    public static void stop(Player p, boolean giveBackDrone) {
        Cache.loadInventory(p);
        p.teleport(location.get(p.getName()));
        drone.remove(p.getName());
        location.remove(p.getName());
        cooldown.remove(p.getName());
        p.setFlySpeed(0.1f);
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setWalkSpeed(0.2f);
        p.setFoodLevel(20);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        if(giveBackDrone) p.getInventory().addItem(new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne").build());
    }

    public static void crash(Player p) {
        stop(p, false);
        Beruf.getBeruf(p).sendMessage(PREFIX + "Die Drohne ist abgestürzt.");
        p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 1F, false, false);
    }

    public static boolean isDrone(Player p) {
        return drone.contains(p.getName());
    }


    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent e) {
        if(isDrone(e.getPlayer())) {
            if((e.getPlayer().getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR || e.getPlayer().getLocation().getBlock().getRelative(0, -2, 0).getType() != Material.AIR) && cooldown.get(e.getPlayer().getName())<System.currentTimeMillis()) {
                stop(e.getPlayer(), true);
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(isDrone(e.getPlayer())) {
            stop(e.getPlayer(), true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(isDrone(e.getPlayer())) {
            if(e.getPlayer().getLocation().getBlock().getType().equals(Material.WATER)) {
                crash(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(isDrone(p)) {
                crash(p);
                Player damager = (Player) e.getDamager();
                damager.sendMessage(PREFIX + "Du hast die Drohne abgeschossen.");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(isDrone((Player) e.getWhoClicked())) {
            if (e.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                stop((Player) e.getWhoClicked(), true);
                Player p = (Player) e.getWhoClicked();
                p.sendMessage(PREFIX + "Du hast die Drohne verlassen.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(Beruf.getBeruf(e.getPlayer()) == null) return;
        if(e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WITHER_SKELETON_SKULL) && e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith("§7Drohne")) {
            Player p = e.getPlayer();
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
            Beruf.getBeruf(p).sendMessage(PREFIX + Beruf.getAbteilung(p).getName() + " " + Script.getName(p) + " hat eine Drohne gestartet.");
            start(p);
        }
    }
}
