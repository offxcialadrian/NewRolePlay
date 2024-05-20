package de.newrp.Medic;

import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Player.AFK;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Impfen implements Listener {
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!(e.getRightClicked() instanceof Player)) return;

        Player p = e.getPlayer();
        if (!interact(p)) return;

        long time = System.currentTimeMillis();
        Player rightClicked = (Player) e.getRightClicked();

        if(AFK.isAFK(rightClicked)) {
            Script.sendActionBar(p, Messages.ERROR + "Der Spieler ist AFK.");
        }

        if (Krankheit.HUSTEN.isImpfed(Script.getNRPID(rightClicked))) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits geimpft.");
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
        progressBar(11,  p);

        if (level >= 10) {
            PlayerInventory inv = p.getInventory();
            ItemStack is = inv.getItemInMainHand();
            if (is.getAmount() > 1) {
                is.setAmount(is.getAmount() - 1);
            } else {
                inv.setItemInMainHand(new ItemStack(Material.AIR));
            }

            Me.sendMessage(p, "impft " + Script.getName(rightClicked));
            Krankheit.HUSTEN.setImpfed(Script.getNRPID(rightClicked), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(14));
            p.sendMessage("§8[§aImpfen§8] §a» §7Du hast " + Script.getName(rightClicked) + " erfolgreich geimpft.");
            rightClicked.sendMessage("§8[§aImpfen§8] §a» §7Du wurdest von " + Script.getName(p) + " gegen Husten geimpft.");

            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Husten Impfung");
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
        Script.sendActionBar(p, "§cImpfen.. §8» §a" + sb.toString());
    }
}