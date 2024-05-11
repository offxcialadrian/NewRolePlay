package de.newrp.Shop;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Medic.Medikamente;
import de.newrp.Medic.Rezept;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.Banken;
import de.newrp.Player.Mobile;
import de.newrp.Vehicle.Car;
import de.newrp.Vehicle.CarType;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static de.newrp.News.Zeitung.getLatestZeitungID;
import static de.newrp.Waffen.GetGun.haveGun;

public class PayShop implements Listener {

    public static HashMap<Player, ShopItem> items = new HashMap<>();
    public static HashMap<Player, Shops> shops = new HashMap<>();
    public static HashMap<String, HouseAddon> houseaddon = new HashMap<>();


    public static void pay(Player p, PaymentType type, ShopItem si, Shops s) {
        final int price = (Buy.amount.containsKey(p.getName()) ? si.getPrice(s) * Buy.amount.get(p.getName()) : si.getPrice(s));
        final int singlePrice = si.getPrice(s);

        if (type == PaymentType.BANK && !s.acceptCard()) {
            BuyClick.sendMessage(p, "Wir akzeptieren leider keine Kartenzahlung.");
            return;
        }

        if (!Banken.hasBank(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Bankkonto.");
            p.sendMessage(Messages.INFO + "Erstelle ein Bankkonto mit §8/§6bankkonto §rin der Zentralbank");
            return;
        }

        if (Script.getMoney(p, type) < price) {
            BuyClick.sendMessage(p, "Die Zahlung ist fehlgeschlagen, haben Sie überhaupt genug Geld!?");
            return;
        }

        if (si.premiumNeeded() && !Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dieses Item nur mit Premium erwerben.");
            return;
        }

        final HouseAddon addon = HouseAddon.getHausAddonByName(ChatColor.stripColor(si.getName()));
        if (addon != null && !House.hasHouse(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du hast kein Haus.");
            return;
        }

        final ItemStack itemStack = si.getItemStack();

        final int buyAmount = Buy.amount.getOrDefault(p.getName(), 1);
        for (int j = 0; j < buyAmount; j++) {
            switch (si) {
                case LOTTOSCHEIN:
                    BuyClick.sendMessage(p, "Die Lottoziehung findet jeden Mittwoch und Sonntag um 18 Uhr statt.");
                    break;
                case HAUSKASSE:
                    if (houseaddon.containsKey(p.getName())) {
                        p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                        return;
                    }
                    houseaddon.put(p.getName(), HouseAddon.HAUSKASSE);
                    p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r, um das Hauskassen-Addon zu installieren.");
                    break;
                case KUEHLSCHRANK:
                    if (houseaddon.containsKey(p.getName())) {
                        p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                        return;
                    }
                    houseaddon.put(p.getName(), HouseAddon.KUEHLSCHRANK);
                    p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r, um das Waffenschrank-Addon zu installieren.");
                    break;
                case MIETERSLOT:
                    if (houseaddon.containsKey(p.getName())) {
                        p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                        return;
                    }
                    houseaddon.put(p.getName(), HouseAddon.SLOT);
                    p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r, um das Mieterslot-Addon zu installieren.");
                    break;
                case WAFFENSCHRANK:
                    if (houseaddon.containsKey(p.getName())) {
                        p.sendMessage(Messages.ERROR + "Installiere zuerst das " + houseaddon.get(p.getName()).getName() + "-Addon.");
                        return;
                    }
                    houseaddon.put(p.getName(), HouseAddon.WAFFENSCHRANK);
                    p.sendMessage(Messages.INFO + "Gehe zu deinem Haus und nutze §8/§6installaddon§r, um das Waffenschrank-Addon zu installieren.");
                    break;
                case PISTOLE:
                    if (!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                        BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                        return;
                    }
                    if (!haveGun(p, Weapon.PISTOLE)) {
                        Weapon w = Weapon.PISTOLE;
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                        Weapon.PISTOLE.addToInventory(Script.getNRPID(p));
                    } else {
                        p.sendMessage(Messages.ERROR + "Du hast bereits eine Glory.");
                        return;
                    }
                    break;
                case AK47:
                    if (!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                        BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                        return;
                    }
                    if (!haveGun(p, Weapon.AK47)) {
                        Weapon w = Weapon.AK47;
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                        Weapon.AK47.addToInventory(Script.getNRPID(p));
                    } else {
                        p.sendMessage(Messages.ERROR + "Du hast bereits eine Peacekeeper.");
                        return;
                    }
                    break;
                case DEAGLE:
                    if (!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                        BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                        return;
                    }
                    if (!haveGun(p, Weapon.DESERT_EAGLE)) {
                        Weapon w = Weapon.DESERT_EAGLE;
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                        Weapon.DESERT_EAGLE.addToInventory(Script.getNRPID(p));
                    } else {
                        p.sendMessage(Messages.ERROR + "Du hast bereits eine Ivory.");
                        return;
                    }
                    break;
                case AMMO_50AE:
                    if (!haveGun(p, Weapon.DESERT_EAGLE)) {
                        p.sendMessage(Messages.ERROR + "Du hast keine Ivory.");
                        return;
                    }
                    Weapon.DESERT_EAGLE.addMunition(Script.getNRPID(p), Weapon.DESERT_EAGLE.getMagazineSize());
                    break;
                case JAGDFLINTE:
                    if (!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(p))) {
                        BuyClick.sendMessage(p, "Du hast keinen Waffenschein.");
                        return;
                    }
                    if (!haveGun(p, Weapon.JAGDFLINTE)) {
                        Weapon w = Weapon.JAGDFLINTE;
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                        Weapon.JAGDFLINTE.addToInventory(Script.getNRPID(p));
                    } else {
                        p.sendMessage(Messages.ERROR + "Du hast bereits eine Guardian.");
                        return;
                    }
                    break;
                case SCHROT:
                    if (!haveGun(p, Weapon.JAGDFLINTE)) {
                        p.sendMessage(Messages.ERROR + "Du hast keine Guardian.");
                        return;
                    }
                    Weapon.JAGDFLINTE.addMunition(Script.getNRPID(p), Weapon.JAGDFLINTE.getMagazineSize());
                    break;
                case AMMO_762MM:
                    if (!haveGun(p, Weapon.AK47)) {
                        p.sendMessage(Messages.ERROR + "Du hast keine Peacekeeper.");
                        return;
                    }
                    Weapon.AK47.addMunition(Script.getNRPID(p), Weapon.AK47.getMagazineSize());
                    break;
                case AMMO_9MM:
                    if (!haveGun(p, Weapon.PISTOLE)) {
                        p.sendMessage(Messages.ERROR + "Du hast keine Glory.");
                        return;
                    }
                    Weapon.PISTOLE.addMunition(Script.getNRPID(p), Weapon.PISTOLE.getMagazineSize());
                    break;
                case Zeitung:
                    //p.getInventory().addItem(Zeitung.zeitung);
                    Zeitung(p);
                    Beruf.Berufe.NEWS.addKasse(si.getBuyPrice());
                    break;
                case HANDY_REPAIR:
                    if (!Mobile.hasPhone(p)) {
                        p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
                        return;
                    }

                    if(!Mobile.getPhone(p).isDestroyed(p)) {
                        p.sendMessage(Messages.ERROR + "Dein Handy ist nicht kaputt.");
                        return;
                    }

                    Mobile.getPhone(p).setDestroyed(p, false);
                    Mobile.getPhone(p).setAkku(p, Mobile.getPhone(p).getMaxAkku());
                    break;
                case SAMSUNG_HANDY:
                case APPLE_HANDY:
                case HUAWEI_HANDY:
                    if (Mobile.hasPhone(p)) {
                        p.sendMessage(Messages.ERROR + "Du hast bereits ein Handy.");
                        return;
                    }
                    if (si == ShopItem.SAMSUNG_HANDY) {
                        Mobile.addPhone(p, Mobile.Phones.SAMSUNG);
                    } else if (si == ShopItem.APPLE_HANDY) {
                        Mobile.addPhone(p, Mobile.Phones.APPLE);
                    } else {
                        Mobile.addPhone(p, Mobile.Phones.HUAWEI);
                    }
                    Mobile.getPhone(p).setAkku(p, Mobile.getPhone(p).getMaxAkku());
                    Mobile.getPhone(p).setDestroyed(p, false);
                    break;
                case SCHMERZMITTEL:
                case ENTZUENDUNGSHEMMENDE_SALBE:
                case SCHMERZMITTEL_HIGH:
                case ANTIBIOTIKA:
                    Medikamente m = Medikamente.getMedikament(ChatColor.stripColor(si.getName()));
                    if (m == null) {
                        Script.sendBugReport(p, "medikament is null in PayShop.java and si = " + si.getName());
                        break;
                    }

                    final int amountOfRecipes = Rezept.getAmountOfRecipes(p, m);
                    if(amountOfRecipes < buyAmount && m.isRezeptNeeded()) {
                        p.sendMessage(Messages.ERROR + "Du hast nicht genügend Rezepte!");
                        return;
                    }

                    if (m.insurancePays()) {
                        Rezept.removeRezept(p, m);
                        p.sendMessage(Messages.INFO + "Deine Krankenversicherung hat die Kosten für das Medikament übernommen.");
                        Script.addMoney(p, PaymentType.BANK, singlePrice);
                        Stadtkasse.removeStadtkasse(singlePrice, "Kostenübernahme durch Krankenversicherung an " + Script.getName(p));
                    }
                    break;
                case EINZELFAHRASUSWEIS:
                    p.getInventory().addItem(new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [Einzelfahrausweis]").setLore("Verbleibende Fahrten: 1").build());
                    break;
                case WOCHENFAHRASUSWEIS:
                    p.getInventory().addItem(new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [7 Fahrten]").setLore("Verbleibende Fahrten: 7").build());
                    break;
                case MONATSFAHRASUSWEIS:
                    p.getInventory().addItem(new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [30 Fahrten]").setLore("Verbleibende Fahrten: 30").build());
                    break;
                case WATER_BUCKET:
                    p.getInventory().addItem(Script.setNameAndLore(new ItemStack(Material.WATER_BUCKET), "§9Wasser", "§65/5"));
                    break;
                case DUENGER:
                    p.getInventory().addItem(Script.setNameAndLore(new ItemStack(Material.INK_SAC), "§7Dünger", "§65/5"));
                    break;
                case OPPEL:
                case VOLTSWAGEN:
                case NMW:
                case AWDI:
                case MERCADAS:
                case PAWSCHE:
                    if (!Licenses.FUEHRERSCHEIN.hasLicense(Script.getNRPID(p))) {
                        p.sendMessage(Component.text(Car.PREFIX + "Du hast keinen Führerschein!"));
                        return;
                    }

                    if (Car.getCars(p).size() >= SlotLimit.VEHICLE.get(Script.getNRPID(p))) {
                        p.sendMessage(Component.text(Messages.ERROR + "Du hast Fahrzeug-Slots übrig!"));
                        return;
                    }

                    CarType carType = CarType.getCarTypeByName(si.getName());
                    assert carType != null;
                    Car car = Car.createCar(carType, new Location(p.getWorld(), 393 + new Random().nextInt(3), 76.5, 1090), p);
                    assert car != null;
                    car.setActivated(true);
                    p.sendMessage(Component.text(Car.PREFIX + "Du hast dir einen neuen " + si.getName() + " gekauft."));
                    BuyClick.sendMessage(p, "Hier sind die Schlüssel, dann viel Spaß!");
                    break;
            }

            if (Mobile.isPhone(itemStack) && !Mobile.hasCloud(p)) {
                p.sendMessage(Messages.ERROR + "Du hast deine Daten nicht in der Cloud gespeichert.");
                Script.executeAsyncUpdate("DELETE FROM handy_settings WHERE nrp_id = " + Script.getNRPID(p));
                Script.executeAsyncUpdate("DELETE FROM call_history WHERE nrp_id = " + Script.getNRPID(p));
                Script.executeAsyncUpdate("DELETE FROM messages WHERE nrp_id = " + Script.getNRPID(p) + " OR sender = " + Script.getNRPID(p));
                Script.executeUpdate("DELETE FROM missed_calls WHERE toID = " + Script.getNRPID(p));
            }

            if (si.addToInventory()) {
                final ItemStack clonedItemStack = itemStack.clone();
                Debug.debug("Adding item " + (clonedItemStack.hasItemMeta() ? ChatColor.stripColor(clonedItemStack.getItemMeta().getDisplayName()) : clonedItemStack.getType()) + " x" + clonedItemStack.getAmount() + " to " + Script.getName(p));
                if(clonedItemStack.hasItemMeta() && !clonedItemStack.getItemMeta().getLore().isEmpty()) {
                    Debug.debug("Item lore is " + String.join("\n", clonedItemStack.getItemMeta().getLore()));
                }
                p.getInventory().addItem(clonedItemStack);
            }

            if (type == PaymentType.BANK) {
                Cashflow.addEntry(p, -price, "Einkauf: " + si.getName());
            }

            s.removeLager(si.getSize());
        }

        Script.removeMoney(p, type, price);
        final double mwst = Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
        final int bankTransferFee = (int) Script.getPercent(2, price);
        final int buyPrice = si.getBuyPrice() * buyAmount;
        Debug.debug("MWST = " + mwst + ", " +
                "bankTransferFee = " + bankTransferFee + ", " +
                "paid price = " + price + ", " +
                "buyPriceSingle = " + si.getBuyPrice() + ", " +
                "buyPriceTimes" + buyAmount + " = " + buyPrice);

        Debug.debug("Price before mwst (" + mwst + ") is " + (price - buyPrice));

        int shopMoney = (price - buyPrice) - (int) Script.getPercent(mwst, price - buyPrice);

        Debug.debug("Price after mwst is " + shopMoney);
        if(type == PaymentType.BANK) {
            shopMoney -= bankTransferFee;
        }

        if (s.getOwner() > 0) {
            s.addKasse(shopMoney);
            Log.NORMAL.write(p, "hat " + si.getName() + " für " + price + "€ gekauft.");
            if (Script.getOfflinePlayer(s.getOwner()).isOnline()) {
                Script.sendActionBar(Script.getPlayer(s.getOwner()), Shop.PREFIX + "Dein Shop §6" + s.getPublicName() + " §7hat §6" + shopMoney + "€ §7Gewinn gemacht aus dem Verkauf von §6" + buyAmount + "x " + si.getName() + " §7(§6" + price + "€§7)");
            }
        } else {
            Stadtkasse.addStadtkasse(shopMoney, "Gewinn aus dem Verkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")", null);
            Stadtkasse.removeStadtkasse(si.getBuyPrice(), "Einkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")");
            Log.NORMAL.write(p, "hat " + si.getName() + " für " + price + "€ gekauft.");
        }
        BuyClick.sendMessage(p, "Vielen Dank für Ihren Einkauf!");
        Stadtkasse.addStadtkasse((int) Script.getPercent(mwst, price - si.getBuyPrice() * buyAmount), "Mehrwertsteuer aus dem Verkauf von " + si.getName() + " (Shop: " + s.getPublicName() + ")", Steuern.Steuer.MEHRWERTSTEUER);

        Achievement.EINKAUFEN.grant(p);
        Buy.amount.remove(p.getName());

        if (si.isReopen()) BuyClick.reopen(p);
        SDuty.updateScoreboard();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().equals("§8[§aZahlungsmethode§8]")) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        e.setCancelled(true);
        e.getView().close();

        if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§aBar")) {
            if (!items.containsKey(p)) return;
            if (!shops.containsKey(p)) return;
            pay(p, PaymentType.CASH, items.get(p), shops.get(p));
            return;
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§aKarte")) {
            if (!items.containsKey(p)) return;
            if (!shops.containsKey(p)) return;
            pay(p, PaymentType.BANK, items.get(p), shops.get(p));
            return;
        }

    }

    private static void Zeitung(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM zeitung WHERE id=" + getLatestZeitungID())) {
            if (rs.next()) {
                String[] pages = rs.getString("content").split("/\\{new_page}/");
                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bm = (BookMeta) book.getItemMeta();
                bm.setGeneration(null);
                bm.setAuthor("News Redaktion");
                bm.setDisplayName("Zeitung [" + getLatestZeitungID() + ". Auflage]");
                bm.setTitle("Zeitung [" + getLatestZeitungID() + ". Auflage]");
                bm.setPages(pages);
                book.setItemMeta(bm);
                p.getInventory().addItem(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
    }


}
