package de.newrp.Medic;

import de.newrp.API.Health;
import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.NewRoleplayMain;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Krankheitstest implements Listener {

    public static String PREFIX = "§8[§cKrankheitstest§8] §c" + Messages.ARROW + " §7";

    private static final Map<String, Long> SPRITZE_COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!(e.getRightClicked() instanceof Player)) return;

        Player p = e.getPlayer();
        if (!interact(p)) return;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du musst einen Beruf haben um das zu tun.");
            return;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.RETTUNGSDIENST) {
            p.sendMessage(Messages.ERROR + "Du musst im Rettungsdienst sein um das zu tun.");
            return;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein um das zu tun.");
            return;
        }



        long time = System.currentTimeMillis();
        Player tg = (Player) e.getRightClicked();
        if(AFK.isAFK(tg)) {
            Script.sendActionBar(p, Messages.ERROR + "Der Spieler ist AFK.");
        }

        Long lastUsage = SPRITZE_COOLDOWN.get(tg.getName());
        if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(4) > time) {
            long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.MINUTES.toMillis(4) - time);
            p.sendMessage(Messages.ERROR + "Dem Spieler wurde bereits Blut abgenommen. (" + cooldown + " Sekunden verbleibend)");
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

            float amount = Script.getRandomFloat(.2F, .3F);
            Health.BLOOD.remove(Script.getNRPID(tg), amount);

            Me.sendMessage(p, "nimmt " + Script.getName(tg) + " Blut ab.");
            p.sendMessage(Messages.INFO + "Bitte warte nun 2 Minuten..");
            tg.damage(2D);
            Stadtkasse.removeStadtkasse(50, "Blutabnahme von " + Script.getName(tg) + " durch " + Script.getName(p));
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendMessage(PREFIX + "Ergebnis des Krankheitstests von " + Script.getName(tg) + ":");
                    for(Krankheit k : Krankheit.values()) {
                        p.sendMessage(PREFIX + "§7" + k.getName() + ": " + (k.isInfected(Script.getNRPID(tg)) ? "§aJa" : "§cNein"));
                    }
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 20L*60L*2L);

            SPRITZE_COOLDOWN.put(tg.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Spritze");
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
        Script.sendActionBar(p, "§cBlut abnehmen.. §8» §a" + sb);
    }
}