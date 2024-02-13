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
import de.newrp.Player.Mobile;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
            BuyClick.sendMessage(p, "Die Zahlung ist fehlgeschlagen, haben Sie überhaupt genug Geld!?");
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
                if(houseaddon.containsKey(p.getName())) {
                    p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                    return;
                }
                houseaddon.put(p.getName(), HouseAddon.HAUSKASSE);
                p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r um das Hauskassen-Addon zu installieren.");
                break;
            case MIETERSLOT:
                if(houseaddon.containsKey(p.getName())) {
                    p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                    return;
                }
                houseaddon.put(p.getName(), HouseAddon.SLOT);
                p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r um das Mieterslot-Addon zu installieren.");
                break;
            case WAFFENSCHRANK:
                if(houseaddon.containsKey(p.getName())) {
                    p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                    return;
                }
                houseaddon.put(p.getName(), HouseAddon.WAFFENSCHRANK);
                p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r um das Waffenschrank-Addon zu installieren.");
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
            case AMMO_762MM:
                if(!haveGun(p, Weapon.AK47)) {
                    p.sendMessage(Messages.ERROR + "Du hast keine AK-47.");
                    return;
                }
                Weapon.AK47.addMunition(Script.getNRPID(p), si.getAmount(s));
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
            case ENTZUENDUNGSHEMMENDE_SALBE:
            case SCHMERZMITTEL_HIGH:
            case ANTIBIOTIKA:
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
                    Stadtkasse.removeStadtkasse(price, "Kostenübernahme durch Krankenversicherung an " + Script.getName(p));
                }
                break;

            case WATER_BUCKET:
                p.getInventory().addItem(Script.setNameAndLore(new ItemStack(Material.WATER_BUCKET), "§9Wasser", "§65/5"));
                break;
            case DUENGER:
                p.getInventory().addItem(Script.setNameAndLore(new ItemStack(Material.INK_SAC, 1, (short) 15), "§7Dünger", "§65/5"));
                break;
        }

        if(Mobile.isPhone(i) && Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast bereits ein Handy.");
            return;
        }

        if(Mobile.isPhone(i) && !Mobile.hasCloud(p)) {
            p.sendMessage(Messages.ERROR + "Du hast deine Daten nicht in der Cloud gespeichert.");
            Script.executeAsyncUpdate("DELETE FROM handy_settings WHERE nrp_id = " + Script.getNRPID(p));
            Script.executeAsyncUpdate("DELETE FROM call_history WHERE nrp_id = " + Script.getNRPID(p));
            Script.executeAsyncUpdate("DELETE FROM messages WHERE nrp_id = " + Script.getNRPID(p) + " OR sender = " + Script.getNRPID(p));
        }

        if(si.addToInventory()) p.getInventory().addItem(i);

        if(Mobile.isPhone(i)) {
            Mobile.hasCloud(p);
            Mobile.getPhone(p).setAkku(p, Mobile.getPhone(p).getMaxAkku());
        }

        if(type == PaymentType.BANK) {
            Cashflow.addEntry(p, -price, "Einkauf: " + si.getName());
        }

        BuyClick.sendMessage(p, "Vielen Dank für Ihren Einkauf!");
        Script.removeMoney(p, type, price);
        s.removeLager(si.getSize());
        double mwst = Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
        Stadtkasse.addStadtkasse((int) Script.getPercent(mwst, price), "Mehrwertsteuer aus dem Verkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")", Steuern.Steuer.MEHRWERTSTEUER);
        int add = price - (int) Script.getPercent(mwst, price) + (type == PaymentType.BANK ? - (int) Script.getPercent(2, price):0);
        if(s.getOwner() > 0) {
            s.addKasse(add);
            s.removeKasse(si.getBuyPrice());
            Log.NORMAL.write(p, "hat " + si.getName() + " für " + price + "€ gekauft.");
            if (Script.getOfflinePlayer(s.getOwner()).isOnline())
                Script.sendActionBar(Script.getPlayer(s.getOwner()), Shop.PREFIX + "Dein Shop §6" + s.getPublicName() + " §7hat §6" + (add - si.getBuyPrice()) + "€ §7Gewinn gemacht aus dem Verkauf von §6" + si.getName() + " §7(§6" + price + "€§7)");
        } else {
            Stadtkasse.addStadtkasse(add, "Gewinn aus dem Verkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")", null);
            Stadtkasse.removeStadtkasse(si.getBuyPrice(), "Einkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")");
            Log.NORMAL.write(p, "hat " + si.getName() + " für " + price + "€ gekauft.");
        }

        Achievement.EINKAUFEN.grant(p);


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
