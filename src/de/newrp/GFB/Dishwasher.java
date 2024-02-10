package de.newrp.GFB;

import de.newrp.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Dishwasher implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§bTellerwäscher§8] §b» §7";
    public static final HashMap<String, Long> cooldown = new HashMap<>();
    public static final HashMap<String, Integer> dishes = new HashMap<>();
    public static final HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static ArrayList<String> ON_JOB = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/tellerwäscher");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(cooldown.containsKey(p.getName())) {
            if(cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 587, 67, 746, -70.366f, 13.379983f))> 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Restaurant.");
            return true;
        }

        GFB.CURRENT.put(p.getName(), GFB.DISHWASHER);
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        p.sendMessage(PREFIX + "Du hast den Job angenommen!");
        int dish = GFB.DISHWASHER.getLevel(p) * Script.getRandom(10, 20);
        dishes.put(p.getName(), dish);
        TOTAL_SCORE.put(p.getName(), dish);
        p.sendMessage(PREFIX + "Du musst " + dish + " Teller waschen.");
        p.sendMessage(Messages.INFO + "Klicke nun auf die Teller um einen zu nehmen.");

        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(ON_JOB.contains(p.getName())) return;
        if(!(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 582, 68, 743)))) return;
        p.sendMessage(PREFIX + "Du hast erfolgreich einen Teller genommen.");
        p.sendMessage(Messages.INFO + "Klicke nun auf das Waschbecken hinter dir um den Teller zu waschen.");
        ON_JOB.add(p.getName());
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!ON_JOB.contains(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 586, 67, 749))) && !(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 585, 67, 749)))) return;
        Cache.saveInventory(p);
        p.getInventory().clear();
        ArrayList<Integer> numbers = Script.getRandomNumbers(0, 6*9, 10);
        Inventory inv = Bukkit.createInventory(null, 6*9, "§6Tellerwäscher");
        for(int i = 0; i < (6*9)-1; i++) {
            if(numbers.contains(i)) {
                inv.setItem(i, new ItemBuilder(Material.DIRT).setName("§8Dreck").build());
            } else {
                inv.setItem(i, new ItemBuilder(Material.BOWL).setName("§aTeller").build());
            }
        }
        inv.setItem(6*9-1, new ItemBuilder(Material.SPONGE).setName("§aFertig").build());
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!dishes.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equals("§6Tellerwäscher")) return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if(e.getCurrentItem().getType() == Material.BOWL) {
            p.sendMessage(PREFIX + "Du hast den Teller zerbrochen.");
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            int dish = dishes.get(p.getName());
            dish--;
            dishes.put(p.getName(), dish);
            TOTAL_SCORE.replace(p.getName(), TOTAL_SCORE.get(p.getName())-1);
            return;
        }
        Inventory inv = e.getInventory();
        if(e.getCurrentItem().getType() == Material.DIRT) {
            inv.setItem(e.getSlot(), new ItemBuilder(Material.BOWL).setName("§aTeller").build());
            p.openInventory(inv);
            return;
        }

        if(e.getCurrentItem().getType() == Material.SPONGE) {
            for(ItemStack is : inv.getContents()) {
                if(is == null || is.getType().equals(Material.AIR)) continue;
                if(is.getType() == Material.DIRT) {
                    p.openInventory(inv);
                    return;
                }
            }
        }

        p.closeInventory();
        ON_JOB.remove(p.getName());
        p.sendMessage(PREFIX + "Du hast einen Teller gewaschen.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        int dish = dishes.get(p.getName());
        dish--;
        dishes.put(p.getName(), dish);
        if(dish <= 0) {
            p.sendMessage(PREFIX + "Du hast alle Teller gewaschen.");
            GFB.CURRENT.remove(p.getName());
            dishes.remove(p.getName());
            GFB.DISHWASHER.addExp(p, TOTAL_SCORE.get(p.getName()));
            Script.addEXP(p, GFB.DISHWASHER.getLevel(p) * TOTAL_SCORE.get(p.getName()));
            PayDay.addPayDay(p, GFB.DISHWASHER.getLevel(p) * TOTAL_SCORE.get(p.getName())/2);
            TOTAL_SCORE.remove(p.getName());
            Cache.loadInventory(p);
            return;
        }
        p.sendMessage(PREFIX + "Du musst noch " + dish + " Teller waschen.");
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!dishes.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equals("§6Tellerwäscher")) return;
        ON_JOB.remove(p.getName());
        Cache.loadInventory(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!dishes.containsKey(p.getName())) return;
        GFB.CURRENT.remove(p.getName());
        TOTAL_SCORE.remove(p.getName());
        dishes.remove(p.getName());
        ON_JOB.remove(p.getName());
        Cache.loadInventory(p);
    }

}
