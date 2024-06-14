package de.newrp.API;

import de.newrp.GFB.*;
import de.newrp.Player.NaviCommand;
import de.newrp.Player.UBahn;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
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
        e.getView().getTitle();
        if (e.getView().getTitle().equals("§e§lNavi")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack c = e.getCurrentItem();
                e.getView().close();
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                switch (c.getItemMeta().getDisplayName()) {
                    case "§6Berufe": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.GHAST_TEAR, "§6" + Navi.KRANKENHAUS.getName()));
                        inv.setItem(1, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.NEWS.getName()));
                        inv.setItem(2, Script.setName(Material.LEAD, "§6" + Navi.POLIZEIREVIER.getName()));
                        inv.setItem(3, Script.setNameAndLore(Script.getGermanyFlag(), "§6" + Navi.STADTHALLE.getName()));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;

                    }
                    case "§6Freizeit": {
                        Inventory inv = Bukkit.createInventory(null, 9*3, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.REDSTONE_LAMP, "§6" + Navi.ARCADE.getName()));
                        inv.setItem(1, Script.setName(Material.DIAMOND, "§6" + Navi.CASINO.getName()));
                        inv.setItem(2, Script.setName(Material.FIREWORK_ROCKET, "§6" + Navi.FREIZEITPARK.getName()));
                        inv.setItem(3, Script.setName(Material.RED_BED, "§6" + Navi.MOTEL.getName()));
                        inv.setItem(4, Script.setName(Material.BLUE_BED, "§6" + Navi.HOTEL.getName()));
                        inv.setItem(5, Script.setName(Material.SAND, "§6" + Navi.STRAND.getName()));
                        inv.setItem(6, Script.setName(Material.IRON_BLOCK, "§6" + Navi.GYM.getName()));
                        inv.setItem(7, Script.setName(Material.STICK, "§6" + Navi.SHISHABAR.getName()));
                        inv.setItem(8, Script.setName(Material.OAK_LEAVES, "§6" + Navi.PARK.getName()));
                        inv.setItem(9, Script.setName(Material.HONEY_BOTTLE, "§6" + Navi.BAR.getName()));
                        inv.setItem(10, Script.setName(Material.POTION, "§6" + Navi.CLUB.getName()));
                        inv.setItem(11, Script.setName(Material.NAUTILUS_SHELL, "§6" + Navi.ZOO.getName()));
                        inv.setItem(22, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;

                    }
                    case "§6GFB-Jobs": {
                        Inventory inv = Bukkit.createInventory(null, 9*3, "§e§lNavi");
                        inv.setItem(0, Script.setNameAndLore(Material.COOKED_BEEF, "§6" + Navi.BURGERBRATER.getName(), (BurgerFryer.cooldown.containsKey(p.getName()) && BurgerFryer.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(BurgerFryer.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(1, Script.setNameAndLore(Material.ICE, "§6" + Navi.EISHALLE.getName(), (Eishalle.cooldown.containsKey(p.getName()) && Eishalle.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Eishalle.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(2, Script.setNameAndLore(Material.GLASS_BOTTLE, "§6" + Navi.KELLNER.getName(), (Kellner.cooldown.containsKey(p.getName()) && Kellner.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Kellner.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(3, Script.setNameAndLore(Material.CHEST, "§6" + Navi.LAGERARBEITER.getName(), (Lagerarbeiter.cooldown.containsKey(p.getName()) && Lagerarbeiter.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Lagerarbeiter.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(4, Script.setNameAndLore(Material.PUMPKIN_PIE, "§6" + Navi.PIZZALIEFERANT.getName(), (Pizza.cooldown.containsKey(p.getName()) && Pizza.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Pizza.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(5, Script.setNameAndLore(Material.ANDESITE_SLAB, "§6" + Navi.STRAßENWARTUNG.getName(), (Strassenwartung.cooldown.containsKey(p.getName()) && Strassenwartung.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Strassenwartung.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(6, Script.setNameAndLore(Material.BOWL, "§6" + Navi.TELLERWÄSCHER.getName(), (Dishwasher.cooldown.containsKey(p.getName()) && Dishwasher.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Dishwasher.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(7, Script.setNameAndLore(Material.SADDLE, "§6" + Navi.TRANSPORT.getName(), (Transport.cooldown.containsKey(p.getName()) && Transport.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Transport.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(8, Script.setNameAndLore(Material.BEEHIVE, "§6" + Navi.IMKER.getName(), (Imker.cooldown.containsKey(p.getName()) && Imker.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Imker.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(9, Script.setNameAndLore(Material.COOKED_COD, "§6" + Navi.DOENERBUDE.getName(), (Kebap.cooldown.containsKey(p.getName()) && Kebap.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Kebap.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(10, Script.setNameAndLore(Material.STICK, "§6Tabakplantage", (Tabakplantage.cooldown.containsKey(p.getName()) && Tabakplantage.cooldown.get(p.getName())>System.currentTimeMillis() ? "§cDu musst noch " + Script.getRemainingTime(Tabakplantage.cooldown.get(p.getName())) + " warten." :"§aDu hast derzeit keinen Cooldown")));
                        inv.setItem(22, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;


                    }
                    case "§6Illegale Organisationen": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.GRASS, "§6" + Navi.GROVE.getName()));
                        inv.setItem(1, Script.setName(Material.OAK_BOAT, "§6" + Navi.HITMEN.getName()));
                        inv.setItem(2, Script.setName(Material.WITHER_ROSE, "§6" + Navi.CORLEONE.getName()));
                        inv.setItem(3, Script.setName(Material.WITHER_SKELETON_SKULL, "§6" + Navi.FALCONE.getName()));
                        inv.setItem(4, Script.setName(Material.DEAD_BUSH, "§6" + Navi.KARTELL.getName()));
                        inv.setItem(5, Script.setName(Material.DIAMOND_HORSE_ARMOR, "§6" + Navi.SINALOA.getName()));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;
                    }
                    case "§6Öffentliche Gebäude": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.WARPED_PLANKS, "§6" + Navi.AEKI.getName()));
                        inv.setItem(1, Script.setName(Material.QUARTZ_PILLAR, "§6" + Navi.GERICHT.getName()));
                        inv.setItem(2, Script.setName(Material.WRITTEN_BOOK, "§6" + Navi.SCHULE.getName()));
                        inv.setItem(3, Script.setName(Material.CHEST, "§6" + Navi.SELFSTORAGE.getName()));
                        inv.setItem(4, Script.setNameAndLore(Script.getGermanyFlag(), "§6" + Navi.STADTHALLE.getName()));
                        inv.setItem(5, Script.setName(Material.GOLD_INGOT, "§6" + Navi.STAATSBANK.getName()));
                        inv.setItem(6, Script.setName(Material.OAK_BOAT, "§6" + Navi.TAXI.getName()));
                        inv.setItem(7, Script.setName(Material.ACACIA_BOAT, "§6" + Navi.FAHRSCHULE.getName()));
                        inv.setItem(8, Script.setName(Material.TURTLE_EGG, "§6" + Navi.PET.getName()));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;


                    }
                    case "§6Sehenswürdigkeiten": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.PAINTING, "§6" + Navi.MUSEUM.getName()));
                        inv.setItem(1, Script.setName(Material.PRISMARINE, "§6" + Navi.STATUE.getName()));
                        inv.setItem(2, Script.setName(Material.NAUTILUS_SHELL, "§6" + Navi.ZOO.getName()));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;


                    }
                    case "§6Shops": {
                        Inventory inv = Bukkit.createInventory(null, 9*4, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.GLASS, "§6" + Navi.MALL.getName()));
                        inv.setItem(1, Script.setName(Material.BREAD, "§6" + Navi.SUPERMARKT.getName()));
                        inv.setItem(2, Script.setName(Material.IRON_INGOT, "§6" + Navi.ELEKTROLADEN.getName()));
                        inv.setItem(3, Script.setName(Material.DIAMOND_HORSE_ARMOR, "§6" + Navi.WAFFENLADEN.getName()));
                        inv.setItem(4, Script.setName(Material.GHAST_TEAR, "§6" + Navi.APOTHEKE_KH.getName()));
                        inv.setItem(5, Script.setName(Material.GHAST_TEAR, "§6" + Navi.APOTHEKE_X3.getName()));
                        inv.setItem(6, Script.setName(Material.TROPICAL_FISH, "§6" + Navi.FISCH.getName()));
                        inv.setItem(7, Script.setName(Material.LEATHER_BOOTS, "§6" + Navi.SCHUHLADEN_MALL.getName()));
                        inv.setItem(8, Script.setName(Material.MUSIC_DISC_13, "§6" + Navi.MUSIKLADEN_HAFEN.getName()));
                        inv.setItem(9, Script.setName(Material.GOLDEN_HOE, "§6" + Navi.WAFFENLADEN_GANG.getName()));
                        inv.setItem(10, Script.setName(Material.CARROT, "§6" + Navi.GEMUESELADEN.getName()));
                        inv.setItem(11, Script.setName(Material.IRON_INGOT, "§6" + Navi.HANKYS_GANG.getName()));
                        inv.setItem(12, Script.setName(Material.OAK_BOAT, "§6" + Navi.FAHRZEUGHAENDLER.getName()));
                        inv.setItem(13, Script.setName(Material.DANDELION, "§6" + Navi.BLUMENLADEN_STRAND.getName()));
                        inv.setItem(14, Script.setName(Material.IRON_INGOT, "§6" + Navi.HANKYS.getName()));
                        inv.setItem(15, Script.setName(Material.DIAMOND_HOE, "§6" + Navi.JAGDHUETTE.getName()));
                        inv.setItem(16, Script.setName(Material.FISHING_ROD, "§6" + Navi.ANGELLADEN.getName()));
                        inv.setItem(17, Script.setName(Material.CAKE, "§6" + Navi.BAEKEREI.getName()));
                        inv.setItem(18, Script.setName(Material.POPPY, "§6" + Navi.BLUMENLADEN_GANG.getName()));
                        inv.setItem(19, Script.setName(Material.WRITABLE_BOOK, "§6" + Navi.BUCHLADEN.getName()));
                        inv.setItem(20, Script.setName(Material.TOTEM_OF_UNDYING, "§6" + Navi.MERCHANDISE.getName()));
                        // inv.setItem(21, Script.setName(Material.ENDER_EYE, "§6" + Navi.MERCH.getName()));
                        inv.setItem(31, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;

                    }
                    case "§6Tankstellen": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.COAL, "§6" + Navi.TANKSTELLE_GANG.getName()));
                        inv.setItem(1, Script.setName(Material.CHARCOAL, "§6" + Navi.TANKSTELLE_KH.getName()));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;

                    }

                    case "§6Finde..": {
                        Inventory inv = Bukkit.createInventory(null, 18, "§e§lNavi");
                        inv.setItem(0, Script.setName(Material.GOLD_INGOT, "§6Nächster Bankautomat"));
                        inv.setItem(1, Script.setName(Material.GHAST_TEAR, "§6Nächste Apotheke"));
                        inv.setItem(2, Script.setName(Material.RAIL, "§6Nächste U-Bahn"));
                        inv.setItem(13, Script.setName(Material.REDSTONE, "§cZurück"));
                        Script.fillInv(inv);
                        p.openInventory(inv);
                        break;
                    }

                    case "§6Nächster Bankautomat": {
                        ATM atm = ATM.getNearestATM(p.getLocation());
                        p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zum Punkt §6§lGeldautomat " + atm.getID() + "§r§6 angezeigt.");
                        p.sendMessage(Messages.INFO + "Mit /navistop oder erneut /navi wird die Route gelöscht.");
                        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), atm.getLocation()).start();
                        break;
                    }

                    case "§6Nächste Apotheke": {
                        Shops nearest = Shops.APOTHEKE;
                        for(Shops shop : Shops.values()) {
                            if(shop.getType() != ShopType.PHARMACY) continue;
                            if(shop.getBuyLocation().distance(p.getLocation())<nearest.getBuyLocation().distance(p.getLocation())) nearest = shop;
                        }

                        p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zur nächsten §6§lApotheke§r§6 angezeigt.");
                        p.sendMessage(Messages.INFO + "Mit /navistop oder erneut /navi wird die Route gelöscht.");
                        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), nearest.getBuyLocation()).start();
                        break;
                    }
                    case "§6Nächste U-Bahn": {
                        UBahn.Stops stop = UBahn.getNearestStop(p);
                        p.sendMessage(Navi.PREFIX + "Dir wird nun die Route zur nächsten §6§lU-Bahn-Haltestelle (" + stop.getName() + ")§r§6 angezeigt.");
                        p.sendMessage(Messages.INFO + "Mit /navistop oder erneut /navi wird die Route gelöscht.");
                        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), stop.getLocation()).start();
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
