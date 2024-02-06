package de.newrp.Player;

import de.newrp.API.Achievement;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AchievementCommand implements CommandExecutor, Listener {
    public static void open(Player p, int page) {
        Inventory inv = p.getServer().createInventory(null, 4 * 9, "§6Achievements");
        HashMap<Achievement, Boolean> map = Achievement.getAchievements(Script.getNRPID(p));
        Iterator<Map.Entry<Achievement, Boolean>> it = map.entrySet().iterator();
        int i = 0;
        if (page == 2) inv.clear();
        while (it.hasNext()) {
            Map.Entry<Achievement, Boolean> ent = it.next();
            if (page == 1) {
                if (ent.getKey().getID() > 27) break;
                if (ent.getValue()) {
                    inv.setItem(i++, Script.setNameAndLore(Material.DIAMOND, "§6" + ent.getKey().getName(), ent.getKey().getText()));
                } else {
                    inv.setItem(i++, Script.setNameAndLore(Material.CHEST, "§9" + ent.getKey().getName(), "§7Noch nicht freigeschaltet."));
                }
            } else  if (page == 2) {
                if (ent.getKey().getID() > 27) {
                    if (ent.getValue()) {
                        inv.setItem(i++, Script.setNameAndLore(Material.DIAMOND, "§6" + ent.getKey().getName(), ent.getKey().getText()));
                    } else {
                        inv.setItem(i++, Script.setNameAndLore(Material.CHEST, "§9" + ent.getKey().getName(), "§7Noch nicht freigeschaltet."));
                    }
                }
                if (ent.getKey().getID() == 55) break;
            }
        }
        if (page == 1) {
            inv.setItem(35, Script.setName(Material.STONE_BUTTON, "§7» §6§lNächste Seite"));
        } else {
            inv.setItem(27, Script.setName(Material.STONE_BUTTON, "§7« §6§lVorherige Seite"));
        }
        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        open(p, 1);
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§6Achievements")) {
            e.setCancelled(true);
            e.getView().close();
            Achievement achievement = Achievement.getAchievementByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
            if(achievement.justExplained()) {
                achievement.grant((Player) e.getWhoClicked());
                return;
            }
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                Player p = (Player) e.getWhoClicked();
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§7» §6§lNächste Seite")) {
                    AchievementCommand.open(p, 2);
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§7« §6§lVorherige Seite")) {
                    AchievementCommand.open(p, 1);
                }
            }
        }
    }
}
