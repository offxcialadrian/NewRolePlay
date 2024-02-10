package de.newrp.API;

import de.newrp.Player.NaviCommand;
import org.bukkit.Bukkit;
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
                e.getView().close();
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                switch (c.getItemMeta().getDisplayName()) {
                    case "§6Berufe": {
                        Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.ANVIL, "§6" + Navi.KRANKENHAUS.getName()));
                        inv.setItem(1, Script.setName(Material.ANVIL, "§6" + Navi.NEWS.getName()));
                        inv.setItem(2, Script.setName(Material.ANVIL, "§6" + Navi.POLIZEIREVIER.getName()));
                        inv.setItem(3, Script.setName(Material.ANVIL, "§6" + Navi.STADTHALLE.getName()));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;

                    } case "§6Freizeit": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.ARCADE.getName()));
                            inv.setItem(1, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.CASINO.getName()));
                            inv.setItem(2, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.CASINO.getName()));
                            inv.setItem(3, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.FREIZEITPARK.getName()));
                            inv.setItem(4, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.MALL.getName()));
                            inv.setItem(5, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.MOTEL.getName()));
                            inv.setItem(6, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.STRAND.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;

                    } case "§6GFB-JOBS": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.BURGERBRATER.getName()));
                            inv.setItem(1, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.EISHALLE.getName()));
                            inv.setItem(2, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.KELLNER.getName()));
                            inv.setItem(3, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.LAGERARBEITER.getName()));
                            inv.setItem(4, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.PIZZALIEFERANT.getName()));
                            inv.setItem(5, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.STRAßENWARTUNG.getName()));
                            inv.setItem(6, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.TELLERWÄSCHER.getName()));
                            inv.setItem(7, Script.setName(Material.IRON_PICKAXE, "§6" + Navi.TRANSPORT.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;


                    } case "§6ILLEGALE ORGANISATION": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            p.sendMessage(Messages.ERROR + "Soon..");
                            /*
                            inv.setItem(0, Script.setName(Material.IRON_HORSE_ARMOR, "§6" + Navi.BLOODS.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);*/
                            break;


                    } case "§6Öffentliche Gebäude": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.AEKI.getName()));
                            inv.setItem(1, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.GERICHT.getName()));
                            inv.setItem(2, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.SCHULE.getName()));
                            inv.setItem(3, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.SELFSTORAGE.getName()));
                            inv.setItem(4, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.STADTHALLE.getName()));
                            inv.setItem(5, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.STAATSBANK.getName()));
                            inv.setItem(6, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.TAXI.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;


                    } case "§6Sehenswürdigkeiten": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.PAINTING, "§6" + Navi.MUSEUM.getName()));
                            inv.setItem(1, Script.setName(Material.PAINTING, "§6" + Navi.STATUE.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;


                    } case "§6Shops": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.CARROT, "§6" + Navi.MALL.getName()));
                            inv.setItem(1, Script.setName(Material.CARROT, "§6" + Navi.SUPERMARKT.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;

                    } case "§6Tankstelle": {
                            Inventory inv = Bukkit.createInventory(null, 3 * 9, "§e§lNavi");
                            inv.setItem(0, Script.setName(Material.WATER_BUCKET, "§6" + Navi.TANKSTELLE_GANG.getName()));
                            inv.setItem(1, Script.setName(Material.WATER_BUCKET, "§6" + Navi.TANKSTELLE_KH.getName()));
                            Script.fillInv(inv);
                            p.openInventory(inv);
                            break;

                } default:
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
