package de.newrp.Medic;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Verband implements Listener {
    private static final Map<String, Long> BANDAGE_COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!interact(p)) return;

        if(p.isSneaking()) {
            long time = System.currentTimeMillis();

            Long lastUsage = BANDAGE_COOLDOWN.get(p.getName());
            if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(4) > time) {
                long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.MINUTES.toMillis(4) - time);
                p.sendMessage(Messages.ERROR + "Du bist bereits bandagiert. (" + cooldown + " Sekunden verbleibend)");
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

                if (Health.BLEEDING.containsKey(p.getName())) {
                    float amount = Health.BLEEDING.get(p.getName());
                    if (amount < 1F) {
                        Health.BLEEDING.remove(p.getName());
                    }
                }

                Me.sendMessage(p, "legt sich einen Verband an.");
                p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 60 * 5, 1));

                BANDAGE_COOLDOWN.put(p.getName(), time);
                LAST_CLICK.remove(p.getName());
                LEVEL.remove(p.getName());
                return;
            }
            return;
        }

        if (!(e.getRightClicked() instanceof Player)) return;


        long time = System.currentTimeMillis();
        Player rightClicked = (Player) e.getRightClicked();

        Long lastUsage = BANDAGE_COOLDOWN.get(rightClicked.getName());
        if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(4) > time) {
            long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.MINUTES.toMillis(4) - time);
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits bandagiert. (" + cooldown + " Sekunden verbleibend)");
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

            if (Health.BLEEDING.containsKey(rightClicked.getName())) {
                float amount = Health.BLEEDING.get(rightClicked.getName());
                if (amount < 1F) {
                    Health.BLEEDING.remove(rightClicked.getName());
                }
            }

            Me.sendMessage(p, "legt " + Script.getName(rightClicked) + " einen Verband an.");
            rightClicked.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 60 * 5, 1));

            BANDAGE_COOLDOWN.put(rightClicked.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Verband");
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
        Script.sendActionBar(p, "§cVerband anlegen.. §8» §a" + sb.toString());
    }
}