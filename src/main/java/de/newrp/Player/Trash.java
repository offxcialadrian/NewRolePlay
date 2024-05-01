package de.newrp.Player;

import java.util.HashMap;

import de.newrp.API.Achievement;
import de.newrp.API.FireworkLaunchEvent;
import de.newrp.API.Script;
import de.newrp.API.TrashcanEmptyEvent;
import de.newrp.Chat.Me;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Trash implements Listener {
    final HashMap<Location, Long> cooldowns = new HashMap<>();

    final HashMap<String, Location> cache = new HashMap<>();
    final HashMap<Location, ItemStack> content = new HashMap<>();
	/*ItemStack[] items = new ItemStack[]{new ItemStack(Material.PAPER), Script.Pfandflasche(), (Script.getRandom(1, 5)==5?new ItemStack(Material.POISONOUS_POTATO):new ItemStack(Material.POTATO_ITEM)),
			new ItemStack(Material.ROTTEN_FLESH), new ItemStack(Material.PAPER), new ItemStack(Material.APPLE), new ItemStack(Material.CARROT_ITEM)};*/

    final ItemStack[] items = new ItemStack[]{new ItemStack(Material.PAPER), (Script.getRandom(1, 5) == 5 ? new ItemStack(Material.POISONOUS_POTATO) : new ItemStack(Material.POTATO)),
            new ItemStack(Material.ROTTEN_FLESH), new ItemStack(Material.APPLE), new ItemStack(Material.CARROT)};

    @EventHandler
    public void onInteract(TrashcanEmptyEvent e) {
        Player p = e.getPlayer();
        p.sendMessage("§6Du durchwühlst den Mülleimer.");
        boolean b = true;
        long time = System.currentTimeMillis();
        if (cooldowns.containsKey(e.getBlock().getLocation())) {
            Long lastUsage = cooldowns.get(e.getBlock().getLocation());
            if (lastUsage + 60 * 1000L > time) {
                b = false;
            }
        }
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§7Mülleimer");
        if (b) {
            ItemStack is;
            if (content.containsKey(e.getBlock().getLocation())) {
                is = content.get(e.getBlock().getLocation());
            } else {
                is = items[Script.getRandom(0, items.length - 1)];
            }
            inv.setItem(Script.getRandom(0, 4), is);
            content.put(e.getBlock().getLocation(), is);
        }
        p.openInventory(inv);
        cache.put(p.getName(), e.getBlock().getLocation());
        cooldowns.put(cache.get(p.getName()), System.currentTimeMillis());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§7Mülleimer") && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                if (e.getSlot() > e.getView().getTopInventory().getSize()) return;
                ItemStack is = e.getCurrentItem();
                e.setCancelled(true);
                e.getView().close();
                boolean valid = false;
                for (ItemStack all : items) {
                    if (all.equals(is)) {
                        valid = true;
                        break;
                    }
                }
                Player p = (Player) e.getWhoClicked();
                if (!cache.containsKey(p.getName())) valid = false;
                if (valid) {
                    Achievement.TRASHCAN.grant(p);
                    p.getInventory().addItem(is);
                    p.sendMessage("§7Du hast etwas aus dem Mülleimer genommen.");
                    Me.sendMessage(p, "hat etwas aus dem Mülleimer genommen.");
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        cache.remove(p.getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        Player p = e.getPlayer();
        if (action == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
            if (e.getClickedBlock().getType().equals(Material.CAULDRON)) {
                Bukkit.getServer().getPluginManager().callEvent(new TrashcanEmptyEvent(p, e.getClickedBlock()));
            }
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (p.getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET) {
                Bukkit.getServer().getPluginManager().callEvent(new FireworkLaunchEvent(p, p.getInventory().getItemInMainHand()));
            } else if (p.getInventory().getItemInOffHand().getType() == Material.FIREWORK_STAR) {
                Bukkit.getServer().getPluginManager().callEvent(new FireworkLaunchEvent(p, p.getInventory().getItemInOffHand()));
            }
        }
    }
}