package de.newrp.Shop;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Chat.Chat;
import de.newrp.Entertainment.Lotto;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.newrp.Chat.Chat.constructMessage;

public class BuyClick implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (!e.getView().getTitle().startsWith("§6")) return;

        ItemStack is = e.getCurrentItem();
        if (is == null || is.getType().equals(Material.AIR) || !is.hasItemMeta()) return;

        e.setCancelled(true);
        e.getView().close();

        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.PLAYER) return;

        Shops s = null;
        for (Shops shop : Shops.values()) {
            Location l = shop.getBuyLocation();
            if (l != null) {
                if (p.getLocation().distance(l) < 4) {
                    s = shop;
                    break;
                }
            }
        }

        if (s == null) return;

        ShopItem si = null;
        if (is.getType().equals(Material.BARRIER)) {
            sendMessage(p, "Auf Wiedersehen!");
            return;
        }

        boolean noRemove = false;
        HashMap<Integer, ItemStack> c = s.getItems();
        for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
            if (n.getValue().isSimilar(is) || n.getValue().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName())) {
                si = ShopItem.getItem(n.getKey());
                break;
            }
        }

        if (si == null) {
            p.sendMessage("§cDieses Item ist nicht im Shop verfügbar.");
            return;
        }


        if (s.getLager() >= si.getSize() || !s.hasLager()) {
            if(PayShop.items.containsKey(p)) {
                PayShop.items.replace(p, si);
            } else {
                PayShop.items.put(p, si);
            }

            if(PayShop.shops.containsKey(p)) {
                PayShop.shops.replace(p, s);
            } else {
                PayShop.shops.put(p, s);
            }


            if(!s.acceptCard()) {
                PayShop.pay(p, PaymentType.CASH, si, s);
                sendMessage(p, "Wir akzeptieren leider keine Kartenzahlung.");
                return;
            }
            Script.sendPaymentTypeGUI(p, si.getPrice(s));
            sendMessage(p, "Möchten Sie Bar oder mit Karte bezahlen?");

        } else {
            String[] sorry = new String[]{"Verzeihung", "Tut mir Leid", "Tut uns Leid"};
            sendMessage(p, sorry[Script.getRandom(0, sorry.length - 1)] + ", aber wir haben nicht mehr genug auf Lager.");
        }
    }

    public static void reopen(Player p) {
        Shops s = Buy.current.get(p.getName());
        if (s != null) {
            HashMap<Integer, ItemStack> c = s.getItems();
            int size = (c.size() > 9 ? 3 : 2) * 9;
            Inventory inv = p.getServer().createInventory(null, size, "§6" + s.getPublicName());
            int i = 0;

            for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
                ItemStack is = n.getValue();
                inv.setItem(i++, is);
            }
            inv.setItem(((size / 9) <= 2 ? 13 : 22), Script.setName(Material.BARRIER, "§cSchließen"));
            p.openInventory(inv);
        } else {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Laden.");
        }
    }

    public static void sendMessage(Player p, String msg) {
        Set<String> foundNames = Chat.getMentionedNames(msg);
        for (Player online : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distance(online.getLocation());
            if (distance > 30.0D) {
                continue;
            }
            online.sendMessage(constructMessage("Verkäufer", msg, "sagt", foundNames, distance, Chat.ChatType.NORMAL));
        }
    }

}
