package de.newrp.Player;

import de.newrp.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Vertraege implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Verträge§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/verträge");
            return true;
        }

        if(VertragAPI.getVertraege(p).isEmpty()) {
            p.sendMessage(PREFIX + "Du hast keine Verträge.");
            return true;
        }

        p.sendMessage(PREFIX + "Dir werden nun alle Verträge angezeigt.");
        sendGUI(p, 1);

        return false;
    }

    public static void sendGUI(Player p, int page) {
        Inventory inv = Bukkit.createInventory(null, 4 * 9, "§6Verträge §7- §6Seite " + page);
        List<VertragAPI> list = VertragAPI.getVertraege(p);
        Iterator<VertragAPI> it = list.iterator();
        int i = 0;
        int count = 0;

        int startIndex = (page - 1) * 37;

        while (it.hasNext()) {
            VertragAPI vertrag = it.next();
            count++;

            if (count > startIndex && count <= startIndex + 35) {
                inv.setItem(i++, new ItemBuilder(Material.WRITTEN_BOOK).setName("§6Vertrag mit " + Script.getOfflinePlayer(vertrag.getTo()).getName()).setLore("§8× §7Bedingungen: §6" + vertrag.getBedingung(), "§8× §7ID: §6#" + vertrag.getID()).build());
            }
            if(list.size() > startIndex + 35) {
                inv.setItem(36, new ItemBuilder(Material.STONE_BUTTON).setName("§7» §6§lNächste Seite").build());
            }
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getView().getTitle().startsWith("§6Verträge §7- §6Seite")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            int page = Integer.parseInt(e.getView().getTitle().split(" ")[3]);
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if(e.getCurrentItem().getType() == Material.STONE_BUTTON) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§7» §6§lNächste Seite")) {
                    p.closeInventory();
                    sendGUI(p, page + 1);
                }
            }
        }
    }
}
