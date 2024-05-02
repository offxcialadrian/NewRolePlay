package de.newrp.Player;

import de.newrp.API.Health;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.Entertainment.Boxen;
import de.newrp.Police.Handschellen;
import de.newrp.main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestoSpritze implements Listener {


    private static final Map<String, Long> COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player p = e.getPlayer();
        if (!interact(p)) return;
        if (Handschellen.isCuffed(p)) return;
        if (Fesseln.isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du bist gefesselt.");
            return;
        }
        if (p.isSneaking()) {
            long time = System.currentTimeMillis();

            Long lastUsage = COOLDOWN.get(p.getName());
            if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(30) > time) {
                long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.MINUTES.toMillis(30) - time);
                p.sendMessage(Messages.ERROR + "Du hast bereits eine Testo-Spritze benutzt. (" + cooldown + " Sekunden verbleibend)");
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
            progressBar(11, p);

            if (level >= 10) {
                PlayerInventory inv = p.getInventory();
                ItemStack is = inv.getItemInMainHand();
                if (is.getAmount() > 1) {
                    is.setAmount(is.getAmount() - 1);
                } else {
                    inv.setItemInMainHand(new ItemStack(Material.AIR));
                }


                Me.sendMessage(p, "verabreicht sich eine Testosteron-Spritze.");
                Boxen.onTesto.put(p.getName(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(30));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Boxen.onTesto.remove(p.getName());
                    }
                }.runTaskLater(main.getInstance(), 20 * 60 * 30);

                COOLDOWN.put(p.getName(), time);
                LAST_CLICK.remove(p.getName());
                LEVEL.remove(p.getName());
                return;
            }
            return;
        }
    }

    private static boolean interact(Player p) {
        return p.getInventory().getItemInMainHand().equals(new ItemBuilder(Material.END_ROD).setName("§7Testosteron-Spritze").build());
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
        Script.sendActionBar(p, "§cTestosteronspritze verwenden.. §8» §a" + sb.toString());
    }
}