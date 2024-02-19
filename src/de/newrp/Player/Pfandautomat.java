package de.newrp.Player;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Pfandautomat implements Listener {

    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player p = e.getPlayer();

        if (p.getInventory().getItemInMainHand().getType() != Material.GLASS_BOTTLE) return;
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.OAK_SIGN && e.getClickedBlock().getType() != Material.OAK_WALL_SIGN)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        if (!((Sign) e.getClickedBlock().getState()).getLine(2).equalsIgnoreCase("§lPfandautomat")) return;
        if (p.getInventory().getItemInMainHand().getAmount() < 4) {
            Script.sendActionBar(p, Messages.ERROR + "Du benötigst mindestens 4 Flaschen.");
            return;
        }

        long time = System.currentTimeMillis();

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
        progressBar(11, p);

        if (level >= 10) {
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 4);
            Script.addMoney(p, PaymentType.CASH, 1);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
            return;
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
        Script.sendActionBar(p, "§cPfand einwerfen.. §8» §a" + sb.toString());
    }

}
