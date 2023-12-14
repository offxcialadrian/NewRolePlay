package de.newrp.Shop;

import de.newrp.API.Cashflow;
import de.newrp.API.Debug;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;

public class PayShop implements Listener {

    public static HashMap<Player, ShopItem> items = new HashMap<>();
    public static HashMap<Player, Shops> shops = new HashMap<>();


    public static void pay(Player p, PaymentType type, ShopItem si, Shops s) {
        int price = si.getPrice(s);
        int amount = si.getAmount(s);
        ItemStack i = si.getItemStack();
        i.setAmount(si.getAmount(s));
        ItemMeta meta = i.getItemMeta();
        meta.setLore(Collections.emptyList());
        i.setItemMeta(meta);

        if(Script.getMoney(p, type) < price) {
            BuyClick.sendMessage(p, "Die Zahlung ist fehlgeschlagen, hast du überhaupt genug Geld!?");
            return;
        }


        p.getInventory().addItem(i);

        if(type == PaymentType.BANK) {
            Cashflow.addEntry(p, -price, "Einkauf: " + si.getName());
        }

        BuyClick.sendMessage(p, "Vielen Dank für deinen Einkauf!");
        Script.removeMoney(p, type, price);
        s.removeLager(si.getSize());
        double mwst = Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
        Stadtkasse.addStadtkasse((int) Script.getPercent(mwst, price));
        int add = price - (int) Script.getPercent(mwst, price);
        s.addKasse(add);
        s.removeKasse(si.getBuyPrice());
        BuyClick.reopen(p);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(!e.getView().getTitle().equals("§8[§aZahlungsmethode§8]")) return;
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        e.setCancelled(true);
        e.getView().close();

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aBar")) {
            if(!items.containsKey(p)) return;
            if(!shops.containsKey(p)) return;
            pay(p, PaymentType.CASH, items.get(p), shops.get(p));
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aKarte")) {
            if(!items.containsKey(p)) return;
            if(!shops.containsKey(p)) return;
            pay(p, PaymentType.BANK, items.get(p), shops.get(p));
            return;
        }

    }


}
