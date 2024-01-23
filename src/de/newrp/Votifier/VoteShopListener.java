package de.newrp.Votifier;

import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VoteShopListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§l§6Voteshop")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                e.getView().close();
                if (is.getType().equals(Material.NETHER_STAR)) {
                    p.sendMessage(VoteShop.PREFIX + "Du hast " + VoteListener.getVotepoints(Script.getNRPID(p)) + " Votepunkte.");
                } else if (is.getType().equals(Material.CHEST)) {
                    switch (is.getItemMeta().getDisplayName()) {
                        case "§6Votekiste §7[§3§lNormal§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.NORMAL.getPrice()) {
                                Votekiste.NORMAL.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.NORMAL.getPrice() + " Votepunkte!");
                            }
                            break;
                        case "§6Votekiste §7[§3§lSpecial§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.SPECIAL.getPrice()) {
                                Votekiste.SPECIAL.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.SPECIAL.getPrice() + " Votepunkte!");
                            }
                            break;
                        case "§6Votekiste §7[§3§lUltimate§7]":
                            if (VoteListener.getVotepoints(Script.getNRPID(p)) >= Votekiste.ULTIMATE.getPrice()) {
                                Votekiste.ULTIMATE.open(p);
                            } else {
                                p.sendMessage(VoteShop.PREFIX + "Diese Votekiste kostet " + Votekiste.ULTIMATE.getPrice() + " Votepunkte!");
                            }
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("§6Votekiste")) {
            Player p = (Player) e.getPlayer();
            if (Votekiste.tasks.containsKey(p.getName())) {
                Integer[] tasks = Votekiste.tasks.get(p.getName());
                Bukkit.getScheduler().cancelTask(tasks[0]);
                Bukkit.getScheduler().cancelTask(tasks[1]);
            }
        }
    }

    @EventHandler
    public void onClickKiste(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (e.getView().getTitle().equals("§6Votekiste")) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().equals(Material.AIR) || !item.hasItemMeta()) return;

        if (!Votekiste.tasks.containsKey(e.getWhoClicked().getName())) {
            e.getView().close();
        }
    }
}
