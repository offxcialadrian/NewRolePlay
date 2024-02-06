package de.newrp.Administrator;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
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

public class WeatherControl implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§eW§9eather§eC§9ontrol");
        ItemStack sun = new ItemBuilder(Material.PAPER).setName("§eSonnig").setLore("§7 " + Messages.ARROW + " Stelle das Wetter auf §eSonnig").build();
        ItemStack rain = new ItemBuilder(Material.PAPER).setName("§9Stürmig").setLore("§7 " + Messages.ARROW + " Stelle das Wetter auf §9Stürmig").build();


        inv.setItem(3, sun);
        inv.setItem(5, rain);
        Script.fillInv(inv);
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getView().getTitle().equals("§eW§9eather§eC§9ontrol")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§eSonnig")) {
                p.closeInventory();
                Script.WORLD.setStorm(false);
                p.sendMessage("§8[§eW§9eather§eC§9ontrol§8] §eDu hast das Wetter auf Sonnig gestellt.");
                Script.sendTeamMessage(p, ChatColor.YELLOW, "hat das Wetter auf Sonnig gestellt", true);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§9Stürmig")) {
                p.closeInventory();
                Script.WORLD.setStorm(true);
                p.sendMessage("§8[§eW§9eather§eC§9ontrol§8] §9Du hast das Wetter auf Stürmig gestellt.");
                Script.sendTeamMessage(p, ChatColor.BLUE, "hat das Wetter auf Stürmig gestellt", true);
            }
        }
    }

}
