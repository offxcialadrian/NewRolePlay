package de.newrp.Police;

import de.newrp.API.Debug;
import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.Player.AFK;
import de.newrp.Player.AntiOfflineFlucht;
import de.newrp.Player.Fesseln;
import de.newrp.main;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Handschellen implements Listener {
    private static final Set<String> CUFFED = new HashSet<>();

    public static boolean isCuffed(Player p) {
        return CUFFED.contains(p.getName());
    }

    public static void uncuff(Player p) {
        CUFFED.remove(p.getName());
    }

    public static void cuff(Player p) {
        CUFFED.add(p.getName());
    }

    public static String PREFIX = "§8[§9Handschellen§8] §9» ";

    public static HashMap<Player, Integer> hits = new HashMap<>();

    private static final Map<String, Long> COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!(e.getRightClicked() instanceof Player)) return;

        if(AFK.isAFK((Player) e.getRightClicked())) {
            Script.sendActionBar(e.getPlayer(), Messages.ERROR + Script.getName((Player) e.getRightClicked()) + " ist AFK.");
            return;
        }

        if(SDuty.isSDuty((Player) e.getRightClicked())) {
            Script.sendActionBar(e.getPlayer(), Messages.ERROR + Script.getName((Player) e.getRightClicked()) + " ist im Supporter-Dienst.");
            return;
        }

        Player p = e.getPlayer();
        if (!interact(p)) return;

        long time = System.currentTimeMillis();
        Player rightClicked = (Player) e.getRightClicked();

        if(Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
            return;
        }

        if(Handschellen.isCuffed(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + "Der Spieler ist bereits gefesselt.");
            return;
        }

        Long lastClick = LAST_CLICK.get(p.getName());
        if (lastClick == null) {
            LAST_CLICK.put(p.getName(), time);
            return;
        }

        long difference = time - lastClick;
        if (difference >= 500) LEVEL.remove(p.getName());

        int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);

        LAST_CLICK.put(p.getName(), time);
        LEVEL.put(p.getName(), level + 1);
        progressBar(30,  p);

        if (level >= 30) {
            PlayerInventory inv = p.getInventory();
            ItemStack is = inv.getItemInMainHand();
            if (is.getAmount() > 1) {
                is.setAmount(is.getAmount() - 1);
            } else {
                inv.setItemInMainHand(new ItemStack(Material.AIR));
            }

            p.sendMessage(PREFIX + "Du hast " + Script.getName(rightClicked) + " Handschellen angelegt.");
            rightClicked.sendMessage(PREFIX + "Dir wurde von " + Script.getName(p) + " Handschellen angelegt.");
            Me.sendMessage(p, "hat " + Script.getName(rightClicked) + " Handschellen angelegt.");
            Handschellen.cuff(rightClicked);
            Script.freeze(rightClicked);


            COOLDOWN.put(rightClicked.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;
        if (!Duty.isInDuty(p)) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Handschellen");
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cHandschellen anlegen.. §8» §a" + sb.toString());
    }


    @EventHandler
    public void onDMG(EntityDamageEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            if (Handschellen.isCuffed(p)) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onDMG(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getDamager();
            if (Handschellen.isCuffed(p)) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (Handschellen.isCuffed(p)) {
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            Script.unfreeze(p);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (Handschellen.isCuffed(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Handschellen.isCuffed(p)) Debug.debug("auto arrest");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        long time = System.currentTimeMillis();
        Player p = e.getPlayer();
        //if (!Handschellen.isCuffed(p)) Handschellen.cuff(p);
    }
}