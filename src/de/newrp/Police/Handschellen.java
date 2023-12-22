package de.newrp.Police;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.Player.AFK;
import de.newrp.Player.AntiOfflineFlucht;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    public static HashMap<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!e.getRightClicked().getType().equals(EntityType.PLAYER)) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Player p = e.getPlayer();
        if (!Duty.isInDuty(p)) return;
        if(Beruf.getBeruf(p) != Beruf.Berufe.POLICE) return;

        ItemStack is = p.getInventory().getItemInMainHand();
        if (!is.getType().equals(Material.LEAD)) return;

        Player tg = (Player) e.getRightClicked();
        if (AFK.isAFK(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
            return;
        }

        if(SDuty.isSDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist im Supporter-Dienst.");
            return;
        }

        if (isCuffed(tg)) {
            p.sendMessage("§cDer Spieler ist bereits in Handschellen.");
            return;
        }


        if (cooldowns.containsKey(p.getName())) {
            long secondsLeft = ((cooldowns.get(p.getName()) / 1000) + 60) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                p.sendMessage(Messages.ERROR + "Du kannst Handschellen nur alle 60 Sekunden benutzen.");
                return;
            }
        }

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " Handschellen angelegt.");
        tg.sendMessage(PREFIX + "Dir wurde von " + Script.getName(p) + " Handschellen angelegt.");
        Me.sendMessage(p, "hat " + Script.getName(tg) + " Handschellen angelegt.");
        Handschellen.cuff(tg);
        Script.freeze(tg);
        cooldowns.put(p.getName(), System.currentTimeMillis());
        AntiOfflineFlucht.cooldowns.put(p.getName(), System.currentTimeMillis());

        if (is.getAmount() > 1) {
            is.setAmount(is.getAmount() - 1);
        } else {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
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