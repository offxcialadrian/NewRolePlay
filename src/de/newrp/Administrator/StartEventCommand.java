package de.newrp.Administrator;

import de.newrp.API.Event;
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
import org.jetbrains.annotations.NotNull;

public class StartEventCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§l§6Event");
        for (Event e : Event.values()) {
            inv.addItem(Script.setName(Material.NETHER_STAR, e.getName()));
        }

        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("§l§6Event")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR)) {
                e.getView().close();
                Event event = Event.getEvent(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                Script.startEvent(event, true);
            }
        }
    }

}
