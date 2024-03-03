package de.newrp.Shop;

import de.newrp.API.*;
import de.newrp.Chat.Chat;
import de.newrp.Entertainment.Lotto;
import de.newrp.Player.Hotel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            return;
        }

        if(s.getType() == ShopType.HOTEL) {
            if(Hotel.hasHotelRoom(p)) {
                sendMessage(p, "Alles Klar, sie haben den check-out vollzogen.");
                Script.executeUpdate("DELETE FROM hotel WHERE nrp_id=" + Script.getNRPID(p));
                return;
            }

            Hotel.RoomType rt = Hotel.RoomType.getRoomByName(ChatColor.stripColor(si.getName()));
            Hotel.Hotels hotel = Hotel.Hotels.getHotelByName(ChatColor.stripColor(e.getView().getTitle()));
            if(hotel == null) {
                Script.sendBugReport(p, "hotel is null -> " + ChatColor.stripColor(e.getView().getTitle()));
                return;
            }
            if(rt == null) {
                Script.sendBugReport(p, "rt is null -> " + ChatColor.stripColor(si.getName()));
                return;
            }
            Hotel.Rooms room = hotel.getFreeRoom(rt);
            if(room == null) {
                sendMessage(p, "Es tut uns leid, aber wir haben keine freien Zimmer mehr.");
                return;
            }
            Achievement.HOTEL.grant(p);
            p.sendMessage(Hotel.PREFIX + "Willkommen im " + Hotel.Hotels.getHotelByName(ChatColor.stripColor(e.getView().getTitle())).getName() + "!");
            p.sendMessage(Hotel.PREFIX + "Du hast das Zimmer " + room.getName() + " für " + room.getPrice() + "€ gebucht.");
            Script.executeAsyncUpdate("INSERT INTO hotel (nrp_id, room) VALUES (" + Script.getNRPID(p) + ", " + room.getID() + ")");
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
                sendMessage(p, "Wir akzeptieren leider keine Kartenzahlung.");
                PayShop.pay(p, PaymentType.CASH, si, s);
                return;
            }
            sendMessage(p, "Möchten Sie Bar oder mit Karte bezahlen?");
            int price = si.getPrice(s);
            Inventory gui = p.getServer().createInventory(null, InventoryType.HOPPER, "§8[§aZahlungsmethode§8]");
            ItemStack cash = new ItemBuilder(Material.CHEST).setName("§aBar").setLore("§8» §c" + price + "€").build();
            ItemStack bank = new ItemBuilder(Material.CHEST).setName("§aKarte").setLore("§8» §c" + price + "€").build();
            gui.setItem(1, cash);
            gui.setItem(3, bank);
            p.openInventory(gui);


        } else {
            String[] sorry = new String[]{"Verzeihung", "Tut mir Leid", "Tut uns Leid"};
            sendMessage(p, sorry[Script.getRandom(0, sorry.length - 1)] + ", aber wir haben nicht mehr genug "+ si.getName() + "§r auf Lager.");
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
