package de.newrp.API;

import de.newrp.Administrator.Notications;
import de.newrp.Administrator.SDuty;
import de.newrp.main;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class FriedhofListener implements Listener {

    public static final HashMap<String, EntityDamageEvent.DamageCause> DEATH_REASON = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;

        Player p = e.getEntity();

        Chair.NO_TELEPORT.add(p.getName());
        if (p.isInsideVehicle()) p.leaveVehicle();


        //Sekunden
        int deathtime = 480;

        boolean explosion = p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;

        World w = p.getWorld();
        Location deathLocation = p.getLocation();

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName("§6" + p.getName());
        head.setItemMeta(meta);
        Item item = w.dropItemNaturally(deathLocation, head);
        if (deathtime > 480 || explosion) {
            item.setCustomName("§8\u271F" + p.getName());
        } else {
            item.setCustomName("§7\u271F" + p.getName());
        }
        item.setCustomNameVisible(true);
        item.setVelocity(item.getVelocity().zero());
        item.setPickupDelay(Integer.MAX_VALUE);
        int cash = Script.getMoney(p, PaymentType.CASH);
        Script.setMoney(p, PaymentType.CASH, 0);

        ItemStack[] inventoryContent = p.getInventory().getContents();
        p.getInventory().clear();

        if (p.getItemOnCursor() != null) p.getItemOnCursor().setType(Material.AIR);

        Player killer = p.getKiller();
        Notications.sendMessage(Notications.NotificationType.DEAD, Script.getName(p) + " ist gestorben. " + (killer!=null ? Messages.ARROW + " " + Script.getName(killer):""));
        Friedhof friedhof = new Friedhof(Script.getNRPID(p), p.getName(), deathLocation, System.currentTimeMillis(), deathtime, item, cash, inventoryContent);
        Friedhof.setDead(p, friedhof);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Friedhof.isDead(p)) {
            Friedhof f = Friedhof.getDead(p);
            long deathtime = f.getDeathTime();
            long current = System.currentTimeMillis();

            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(current - deathtime);
            if (seconds > 0) {
                int duration = f.getDuration() - seconds;
                if (duration > 0) {
                    Script.executeAsyncUpdate("INSERT INTO friedhof (id, time) VALUES (" + f.getUserID() + ", " + duration + ") ON DUPLICATE KEY UPDATE time = " + duration);
                }
            }
            if (f.getSkull() != null) f.getSkull().remove();
            if (f.getTaskID() != 0) Bukkit.getScheduler().cancelTask(f.getTaskID());
            Friedhof.FRIEDHOF.remove(p.getName());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            Player p = e.getPlayer();
            int id = Script.getNRPID(p);
            int i = Friedhof.getDeathtimeDatabase(p);
            if (i > 0) {
                Friedhof.setDead(p, new Friedhof(id, p.getName(), null, System.currentTimeMillis(), i, null, 0, null));
            }
        });
    }

    @EventHandler
    public void onHeadDespawn(ItemDespawnEvent e) {
        Item item = e.getEntity();
        if (item.getItemStack().getType() == Material.PLAYER_HEAD) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (Friedhof.isDead(p)) {
            if (!e.getMessage().startsWith("/friedhof") && !e.getMessage().startsWith("/passwort") && e.getMessage().startsWith("/password") && !e.getMessage().startsWith("/aduty") && !e.getMessage().startsWith("/tc") && !e.getMessage().startsWith("/sduty") && !SDuty.isSDuty(p)) {
                e.setCancelled(true);
                p.sendMessage(Messages.ERROR + "Du kannst während du tot bist keine Befehle ausführen.");
            }
        }
    }
}
