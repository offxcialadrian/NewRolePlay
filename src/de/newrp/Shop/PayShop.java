package de.newrp.Shop;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Berufkasse;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Medic.Medikamente;
import de.newrp.Medic.Rezept;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;

import static de.newrp.Waffen.GetGun.haveGun;

public class PayShop implements Listener {

    public static HashMap<Player, ShopItem> items = new HashMap<>();
    public static HashMap<Player, Shops> shops = new HashMap<>();
    public static HashMap<String, HouseAddon> houseaddon = new HashMap<>();


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

        if(si.premiumNeeded() && !Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dieses Item nur mit Premium erwerben.");
            return;
        }

        switch (si) {
            case LOTTOSCHEIN:
                BuyClick.sendMessage(p, "Die Lottoziehung findet jeden Mittwoch und Sonntag um 18 Uhr statt.");
                break;
            case HAUSKASSE:
                houseaddon.put(p.getName(), HouseAddon.HAUSKASSE);
                p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r um das Hauskassen-Addon zu installieren.");
                break;
            case MIETERSLOT:
                houseaddon.put(p.getName(), HouseAddon.SLOT);
                p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r um das Hauskassen-Addon zu installieren.");
                break;
            case PISTOLE:
                if(!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                    BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                    return;
                }
                if(!haveGun(p, Weapon.PISTOLE)) {
                    Weapon w = Weapon.PISTOLE;
                    p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                    Weapon.PISTOLE.addToInventory(Script.getNRPID(p));
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast bereits eine Pistole.");
                    return;
                }
                break;
            case AK47:
                if(!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                    BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                    return;
                }
                if(!haveGun(p, Weapon.AK47)) {
                    Weapon w = Weapon.AK47;
                    p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                    Weapon.AK47.addToInventory(Script.getNRPID(p));
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast bereits eine AK-47.");
                    return;
                }
                break;
            case AMMO_9MM:
                if(!haveGun(p, Weapon.PISTOLE)) {
                    p.sendMessage(Messages.ERROR + "Du hast keine Pistole.");
                    return;
                }
                Weapon.PISTOLE.addMunition(Script.getNRPID(p), si.getAmount(s));
                break;
            case Zeitung:
                Beruf.Berufe.NEWS.addKasse(20);
                break;
            case SCHMERZMITTEL:
                Medikamente m = Medikamente.getMedikamentByShopItem(si);
                if(m == null) return;
                if(!Rezept.hasRezept(p, m) && m.isRezeptNeeded()) {
                    p.sendMessage(Messages.ERROR + "Du hast kein Rezept für " + si.getName() + "§c.");
                    return;
                }

                if(Rezept.hasRezept(p, m) && m.insurancePays()) {
                    Rezept.removeRezept(p, m);
                    p.sendMessage(Messages.INFO + "Deine Krankenversicherung hat die Kosten für das Medikament übernommen.");
                    Script.addMoney(p, PaymentType.BANK, price);
                    Stadtkasse.removeStadtkasse(price);
                }
                break;
        }


        if(si.addToInventory()) p.getInventory().addItem(i);

        if(type == PaymentType.BANK) {
            Cashflow.addEntry(p, -price, "Einkauf: " + si.getName());
        }

        BuyClick.sendMessage(p, "Vielen Dank für deinen Einkauf!");
        Script.removeMoney(p, type, price);
        s.removeLager(si.getSize());
        double mwst = Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
        Stadtkasse.addStadtkasse((int) Script.getPercent(mwst, price));
        int add = price - (int) Script.getPercent(mwst, price) + (type == PaymentType.BANK ? - (int) Script.getPercent(2, price):0);
        s.addKasse(add);
        s.removeKasse(si.getBuyPrice());
        Log.NORMAL.write(p, "hat " + si.getName() + " für " + price + "€ gekauft.");
        if(Script.getOfflinePlayer(s.getOwner()).isOnline())
            Script.sendActionBar(Script.getPlayer(s.getOwner()), Shop.PREFIX + "Dein Shop §6" + s.getPublicName() + " §7hat §6" + (add-si.getBuyPrice()) + "€ §7Gewinn gemacht aus dem Verkauf von §6" + si.getName() + " §7(§6" + price + "€§7)");



        if(si.isReopen()) BuyClick.reopen(p);
        SDuty.updateScoreboard();
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
