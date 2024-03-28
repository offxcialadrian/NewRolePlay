package de.newrp.Player;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Drone;
import de.newrp.Chat.Me;
import de.newrp.GFB.GFB;
import de.newrp.Police.Handschellen;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Fesseln implements Listener {

    private static final Map<String, Long> COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();
    public static final String PREFIX = "§8[§6Fesseln§8]§6 " + Messages.ARROW + " §7";
    public static final ArrayList<String> gefesselt = new ArrayList<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!interact(p)) return;
        if(Handschellen.isCuffed(p)) return;
        if (!(e.getRightClicked() instanceof Player)) return;
        if(isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du bist selbst gefesselt.");
            return;
        }


        long time = System.currentTimeMillis();
        Player rightClicked = (Player) e.getRightClicked();

        if(isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du bist selbst gefesselt.");
            return;
        }

        if(isTiedUp(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + "Der Spieler ist bereits gefesselt.");
            return;
        }

        Long lastUsage = COOLDOWN.get(rightClicked.getName());
        if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(120) > time) {
            p.sendMessage(Messages.ERROR + "Der Spieler kann gerade nicht gefesselt werden. (" + Script.getRemainingTime(lastUsage+TimeUnit.MINUTES.toMillis(120)) + " verbleibend)");
            return;
        }

        if(SDuty.isSDuty(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + "Der Spieler ist im Supporter-Dienst.");
            return;
        }

        if(AFK.isAFK(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + Script.getName(rightClicked) + " ist AFK.");
            return;
        }

        if(Drone.isDrone(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drohne fesseln.");
            return;
        }

        if(GFB.CURRENT.containsKey(rightClicked.getName()) && rightClicked.getLocation().distance(GFB.CURRENT.get(rightClicked.getName()).getLocation()) < 10) {
            p.sendMessage(Messages.ERROR + "Du kannst den Spieler nicht tragen.");
            return;
        }

        Long lastClick = LAST_CLICK.get(p.getName());
        if (lastClick == null) {
            LAST_CLICK.put(p.getName(), time);
            return;
        }

        long difference = time - lastClick;
        if (difference >= 800) LEVEL.remove(p.getName());

        int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);

        LAST_CLICK.put(p.getName(), time);
        LEVEL.replace(p.getName(), level + 1);
        progressBar(51,  p);

        if (level >= 50) {
            PlayerInventory inv = p.getInventory();
            ItemStack is = inv.getItemInMainHand();
            if (is.getAmount() > 1) {
                is.setAmount(is.getAmount() - 1);
            } else {
                inv.setItemInMainHand(new ItemStack(Material.AIR));
            }

            Me.sendMessage(p, "fesselt " + rightClicked.getName() + ".");
            tie(rightClicked);
            AntiOfflineFlucht.cooldowns.put(rightClicked.getName(), time);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(rightClicked) + " gefesselt.");
            rightClicked.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " gefesselt.");

            COOLDOWN.put(rightClicked.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Kabelbinder") && is.getType().equals(Material.STRING);
    }

    public static boolean isTiedUp(Player p) {
        return gefesselt.contains(p.getName());
    }

    public static void untie(Player p) {
        gefesselt.remove(p.getName());
        Script.unfreeze(p);
    }

    public static void tie(Player p) {
        gefesselt.add(p.getName());
        Script.freeze(p);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player damager = (Player) e.getDamager();
        if(isTiedUp(damager)) {
            e.setCancelled(true);
            damager.sendMessage(PREFIX + "Du kannst niemanden schlagen.");
        }
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cFesseln.. §8» §a" + sb.toString());
    }

}
