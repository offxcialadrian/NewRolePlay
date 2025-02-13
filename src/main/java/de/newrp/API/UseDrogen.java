package de.newrp.API;

import de.newrp.Administrator.BuildMode;
import de.newrp.Organisationen.Drogen;
import de.newrp.Player.Fesseln;
import de.newrp.Police.Handschellen;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UseDrogen implements Listener {

    public static final Map<String, Long> DRUG_COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if(BuildMode.isInBuildMode(e.getPlayer())) return;
        if(e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        if(p.getInventory().getItemInMainHand() == null) return;
        if(!p.getInventory().getItemInMainHand().hasItemMeta()) return;
        if(!p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
        if(droge == null) return;

        if(Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drogen konsumieren, wenn du gefesselt bist.");
            return;
        }

        long time = System.currentTimeMillis();

        Long lastUsage = DRUG_COOLDOWN.get(p.getName());
        if(!Script.isInTestMode()) {
            if(!DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p, false)) {
                if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(15) > time) {
                    long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(15) - time);
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
                    return;
                }
            }
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
        progressBar(p);

        if (level >= 3) {
            Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
            Debug.debug(purity);
            droge.consume(p, purity);

            PlayerInventory inv = p.getInventory();
            ItemStack is = inv.getItemInMainHand();
            if (is.getAmount() > 1) {
                is.setAmount(is.getAmount() - 1);
            } else {
                inv.setItemInMainHand(new ItemStack(Material.AIR));
            }


            DRUG_COOLDOWN.put(p.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    private static void progressBar(Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / (double) 3;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cKonsumieren.. §8» §a" + sb);
    }
}
