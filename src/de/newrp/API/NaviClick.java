package de.newrp.API;

import de.newrp.Player.NaviCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NaviClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle() != null && e.getView().getTitle().equals("§e§lNavi")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack c = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                switch (c.getItemMeta().getDisplayName()) {
                    case "§6Allgemein": {
                        Inventory inv = p.getServer().createInventory(null, 3 * 9, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.FISHING_ROD, "§6" + Navi.STADTHALLE.getName()));
                        inv.setItem(22, Script.setName(Material.REDSTONE, "§cZurück"));
                        p.openInventory(inv);
                        break;
                    }
                    default:
                        if (c.getItemMeta().getDisplayName().startsWith("§c")) {
                            NaviCommand.openDefault(p);
                        } else {
                            Navi navi = null;
                            for (Navi n : Navi.values()) {
                                if (ChatColor.stripColor(c.getItemMeta().getDisplayName()).equalsIgnoreCase(n.getName())) {
                                    navi = n;
                                    break;
                                }
                            }
                            if (navi != null) {
                                e.getView().close();
                                p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zum Punkt §6§l" + navi.getName() + "§r§6 angezeigt.");
                                p.sendMessage(Messages.INFO + "Mit /navistop oder erneut /navi wird die Route gelöscht.");
                                new Route(p.getName(), Script.getNRPID(p), p.getLocation(), navi.getLocation()).start();
                            } else {
                                e.setCancelled(true);
                            }
                        }
                        break;
                }
            }
        }
    }
}
