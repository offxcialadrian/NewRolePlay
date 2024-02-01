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

public class Imker implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§eImker§8] §e» §7";
    public static final HashMap<String, Long> cooldown = new HashMap<>();
    public static final HashMap<String, Integer> honeys = new HashMap<>();
    public static final HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static ArrayList<String> ON_JOB = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/imker");
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

        if(p.getLocation().distance(new Location(Script.WORLD, 222, 66, 771, 174.1878f, 10.542069f)) >5 ) {
            p.sendMessage(Messages.ERROR + "Du bist nicht an der Imkerei.");
            return true;
        }

        GFB.CURRENT.put(p.getName(), GFB.DISHWASHER);
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        p.sendMessage(PREFIX + "Du hast den Job angenommen!");
        int honey = GFB.DISHWASHER.getLevel(p) * Script.getRandom(3, 5);
        honeys.put(p.getName(), honey);
        TOTAL_SCORE.put(p.getName(), honey);
        p.sendMessage(PREFIX + "Du musst " + honey + "x Honig besorgen.");
        p.sendMessage(Messages.INFO + "Klicke nun auf einen Bienenstock um Honig zu besorgen.");

        return false;
    }


    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock().getLocation().distance(new Location(Script.WORLD, 208, 66, 767, 46.838097f, 24.342066f))>30) return;
        if(!e.getClickedBlock().getType().equals(Material.BEE_NEST)) return;
        Cache.saveInventory(p);
        p.getInventory().clear();
        ArrayList<Integer> numbers = Script.getRandomNumbers(0, 6*9, 10);
        Inventory inv = Bukkit.createInventory(null, 6*9, "§eImker");
        for(int i = 0; i < (6*9)-1; i++) {
            if(numbers.contains(i)) {
                inv.setItem(i, new ItemBuilder(Material.HONEY_BLOCK).setName("§eHonig").build());
            } else {
                inv.setItem(i, new ItemBuilder(Material.HONEYCOMB_BLOCK).setName("").build());
            }
        }
        inv.setItem(6*9-1, new ItemBuilder(Material.GREEN_WOOL).setName("§aFertig").build());
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!honeys.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equals("§eImker")) return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if(e.getCurrentItem().getType() == Material.HONEYCOMB_BLOCK) return;
        Inventory inv = e.getInventory();
        if(e.getCurrentItem().getType() == Material.HONEY_BLOCK) {
            inv.setItem(e.getSlot(), new ItemBuilder(Material.HONEYCOMB_BLOCK).setName("").build());
            p.openInventory(inv);
            return;
        }

        if(e.getCurrentItem().getType() == Material.GREEN_WOOL) {
            for(ItemStack is : inv.getContents()) {
                if(is == null || is.getType().equals(Material.AIR)) continue;
                if(is.getType() == Material.HONEY_BLOCK) {
                    p.openInventory(inv);
                    return;
                }
            }
        }

        p.closeInventory();
        ON_JOB.remove(p.getName());
        p.sendMessage(PREFIX + "Du hast einmal Honig entnommen.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        int dish = honeys.get(p.getName());
        dish--;
        honeys.put(p.getName(), dish);
        if(dish == 0) {
            p.sendMessage(PREFIX + "Du hast ausreichend Honig entnommen.");
            GFB.CURRENT.remove(p.getName());
            honeys.remove(p.getName());
            p.getInventory().clear();
            Cache.loadInventory(p);
            GFB.IMKER.addExp(p, TOTAL_SCORE.get(p.getName())*Script.getRandom(2,3));
            Script.addEXP(p, GFB.IMKER.getLevel(p) * TOTAL_SCORE.get(p.getName())*Script.getRandom(1, 2));
            PayDay.addPayDay(p, GFB.IMKER.getLevel(p) + TOTAL_SCORE.get(p.getName()));
            TOTAL_SCORE.remove(p.getName());
            return;
        }
        p.sendMessage(PREFIX + "Du musst noch " + dish + "x Honig entnehmen.");
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!honeys.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equals("§eImker")) return;
        ON_JOB.remove(p.getName());
        p.getInventory().clear();
        Cache.loadInventory(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(!honeys.containsKey(p.getName())) return;
        GFB.CURRENT.remove(p.getName());
        TOTAL_SCORE.remove(p.getName());
        honeys.remove(p.getName());
        ON_JOB.remove(p.getName());
        p.getInventory().clear();
        Cache.loadInventory(p);
    }

}
