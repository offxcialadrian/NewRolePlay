package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.Player.Annehmen;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Shop implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Shop§8] » §7";
    private static HashMap<Player, Integer> priceMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        Shops shop = null;
        for(Shops shops : Shops.values()) {
            if(shops.getBuyLocation() != null) {
                if(p.getLocation().distance(shops.getBuyLocation()) < 5) {
                    shop = shops;
                }
            }
        }


        if(shop == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe von einem Shop.");
            return true;
        }

        if(shop.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Dieser Shop gehört dir nicht.");
            return true;
        }


        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "Verwendung: /shop [kasse|sell|info|setprice|upgradelager|sortiment]");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("kasse")) {
            p.sendMessage(PREFIX + "§7Die Kasse von deinem Shop enthält §6" + shop.getKasse() + "€§7.");
            return true;
        }

        if(args.length == 3 && args[0].equalsIgnoreCase("kasse")) {
            if(args[1].equalsIgnoreCase("einzahlen")) {
                int amount = Integer.parseInt(args[2]);

                if(amount <= 0) {
                    p.sendMessage(Messages.ERROR + "Du musst einen Betrag angeben.");
                    return true;
                }

                if(Script.getMoney(p, PaymentType.CASH) < amount) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.CASH, amount);
                shop.addKasse(amount);
                p.sendMessage(PREFIX + "Du hast " + amount + "€ in die Kasse eingezahlt.");

                return true;
            }

            if(args[1].equalsIgnoreCase("auszahlen") || args[1].equalsIgnoreCase("abheben") || args[1].equalsIgnoreCase("entnehmen") || args[1].equalsIgnoreCase("abbuchen")) {
                int amount = Integer.parseInt(args[2]);

                if(amount <= 0) {
                    p.sendMessage(Messages.ERROR + "Du musst einen Betrag angeben.");
                    return true;
                }

                if(shop.getKasse() < amount) {
                    p.sendMessage(Messages.ERROR + "Die Kasse enthält nicht genug Geld.");
                    return true;
                }

                if(shop.getKasse() <= 0) {
                    p.sendMessage(Messages.INFO + "§c§lDein Shop hat derzeit Schulden. Bitte decke die Schulden bevor das Finanzamt dich besucht.");
                    return true;
                }

                Script.addMoney(p, PaymentType.CASH, amount);
                shop.removeKasse(amount);
                p.sendMessage(PREFIX + "Du hast " + amount + "€ aus der Kasse entnommen.");

                return true;
            }
        }

        if(args.length == 3 && args[0].equalsIgnoreCase("sell")) {
            Player buyer = Script.getPlayer(args[1]);
            if(buyer == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(buyer == p && !Script.isInTestMode()) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht dein eigener Käufer sein.");
                return true;
            }

            if(!Script.isInt(args[2])) {
                p.sendMessage(Messages.ERROR + "Du musst einen gültigen Preis angeben.");
                return true;
            }

            int price = Integer.parseInt(args[2]);
            if(price <= 0) {
                p.sendMessage(Messages.ERROR + "Du musst einen gültigen Preis angeben.");
                return true;
            }

            Annehmen.offer.put(buyer.getName() + ".shop.sell.seller", p.getName());
            Annehmen.offer.put(buyer.getName() + ".shop.sell.price", "" + price);
            Annehmen.offer.put(buyer.getName() + ".shop.sell.shop", "" + shop.getID());
            Annehmen.offer.put(buyer.getName() + ".shop.sell", p.getName());
            p.sendMessage(PREFIX + "Du hast " + Script.getName(buyer) + " angeboten deinen Shop " + shop.getPublicName() + " für " + price + "€ zu kaufen.");
            buyer.sendMessage(PREFIX + Script.getName(p) + " hat dir angeboten seinen Shop " + shop.getPublicName() + " für " + price + "€ zu kaufen.");

            buyer.sendMessage(Messages.INFO + "Miete (Gebäude): " + shop.getRent() + "€");
            buyer.sendMessage(Messages.INFO + "Lager: " + shop.getLager() + "/" + shop.getLagerSize());
            buyer.sendMessage(Messages.INFO + "Betriebskosten: " + shop.getRunningCost() + "€");
            buyer.sendMessage(Messages.INFO + "Kasse: " + shop.getKasse() + "€");
            if(shop.getKasse()<=0) {
                buyer.sendMessage(Messages.INFO + "§c§lBitte beachte, dass der Shop derzeit Schulden hat, welche du übernehmen würdest.");
            }

            Script.sendAcceptMessage(buyer);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(PREFIX + "=== " + shop.getPublicName() + " ===");
            p.sendMessage("§8» " + "§6Betriebskosten: " + shop.getRunningCost() + "€");
            p.sendMessage("§8» " + "§6Miete (Gebäude): " + shop.getRent() + "€");
            p.sendMessage("§8» " + "§6Lager: " + shop.getLager() + "/" + shop.getLagerSize());
            p.sendMessage("§8» " + "§6Preise:");
            HashMap<Integer, int[]> c = Shops.getShopItemData(shop);
            for (Map.Entry<Integer, int[]> n : c.entrySet()) {
                ShopItem bi = ShopItem.getItem(n.getKey());
                ItemStack i = bi.getItemStack();
                int[] a = n.getValue();
                i.setAmount(a[0]);
                p.sendMessage("  §8× " + "§6" + bi.getName() + "§8: §6" + a[1] + "€ §8[§6Einkaufspreis§8: §6" + bi.getBuyPrice() + "€§8]");
            }
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("setprice")) {
            HashMap<Integer, ItemStack> c = shop.getItems();
            int size = (c.size() > 9 ? 3 : 2) * 9;
            Inventory inv = p.getServer().createInventory(null, size, "§7Preis-Verwaltung " + shop.getPublicName());
            int i = 0;

            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Du musst einen gültigen Preis angeben.");
                return true;
            }

            int price = Integer.parseInt(args[1]);
            if(price <= 0) {
                p.sendMessage(Messages.ERROR + "Du musst einen gültigen Preis angeben.");
                return true;
            }

            priceMap.put(p, price);

            for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
                ItemStack is = n.getValue();
                inv.setItem(i++, is);
            }

            if(i == 0) {
                p.sendMessage(Messages.ERROR + "Dein Shop bietet derzeit nichts an.");
                return true;
            }

            p.openInventory(inv);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("setprice")) {
            p.sendMessage(Messages.ERROR + "Verwendung: /shop setprice [Preis]");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("upgradelager")) {
            int price = (int) (shop.getLagerSize() * 30F);
            p.sendMessage(PREFIX + "Du kannst dein Lager für " + price + "€ erweitern.");
            p.sendMessage(Messages.INFO + "Nutze /shop upgradelager confirm um dein Lager zu erweitern.");
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("upgradelager")) {
            if(!args[1].equalsIgnoreCase("confirm")) {
                p.sendMessage(Messages.ERROR + "Verwendung: /shop upgradelager confirm");
                return true;
            }

            int price = (int) (shop.getLagerSize() * 30F);

            if(shop.getKasse() < price) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                p.sendMessage(Messages.INFO + "Du benötigst " + price + "€.");
                return true;
            }

            shop.removeKasse(price);
            shop.addLager(100);
            p.sendMessage(PREFIX + "Du hast dein Lager um 100 Lagerplätze erweitert.");
            Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat sein Lager um 100 Lagerplätze erweitert. [Shop: " + shop.getPublicName() + "]");
            Stadtkasse.addStadtkasse((int) Script.getPercent(Steuern.Steuer.MEHRWERTSTEUER.getPercentage(), price));
            return true;
        }

        if(args[0].equalsIgnoreCase("sortiment")) {
            Inventory inv = Bukkit.createInventory(null, 9*3, "§7Sortiment " + shop.getPublicName());
            for(ShopItem si : ShopItem.values()) {
                ItemStack is = si.getItemStack();
                if(!containsType(si, shop)) continue;
                if(shop.getItems().get(si.getID()) == null) {
                    is = Script.setNameAndLore(is, si.getName(), "§8» §6Lizensierungsgebühr: §6" + si.getLicensePrice() + "€", "§8» §6Einkaufspreis: §6" + si.getBuyPrice() + "€");
                } else {
                    is = Script.setNameAndLore(is, si.getName(), "§8» §cKlicke um aus Shop zu entfernen (es erfolgt keine Gutschrift der Lizensierungsgebühr)");
                }
                inv.addItem(is);
            }
            p.openInventory(inv);
            return true;
        }

        p.sendMessage(Messages.ERROR + "Verwendung: /shop [kasse/sell/info/setprice/upgradelager/sortiment]");

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().startsWith("§7Preis-Verwaltung")) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if(e.getCurrentItem().getType() == Material.BARRIER) {
                p.closeInventory();
                return;
            }

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

            if(s == null) return;
            ShopItem si = null;
            ItemStack is = e.getCurrentItem();
            si = ShopItem.getShopItem(is);

            if(si == null) {
                p.sendMessage(Messages.ERROR + "Dieses Item ist nicht im Shop verfügbar.");
                return;
            }

            if(!priceMap.containsKey(p)) {
                p.sendMessage(Messages.ERROR + "Du musst einen Preis angeben.");
                return;
            }

            int price = priceMap.get(p);
            priceMap.remove(p);
            si.setPrice(s, price);
            p.sendMessage(PREFIX + "Du hast den Preis von " + si.getName() + " §7auf " + price + "€ gesetzt.");
            Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat den Preis von " + si.getName() + " §7auf " + price + "€ gesetzt. [Shop: " + s.getPublicName() + "]");
            p.closeInventory();
            return;
        }

        if(e.getView().getTitle().startsWith("§7Sortiment")) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

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

            if(s == null) return;
            ShopItem si = null;
            ItemStack is = e.getCurrentItem();


            si = ShopItem.getShopItem(is);

            if(si == null) {
                p.sendMessage(Messages.ERROR + "Dieses Item ist nicht verfügbar.");
                p.closeInventory();
                return;
            }

            if(s.getItems().get(si.getID()) == null) {
                if(s.getKasse() < si.getLicensePrice()) {
                    p.sendMessage(Messages.ERROR + "Dein Shop hat nicht genug Geld.");
                    p.closeInventory();
                    return;
                }

                s.removeKasse(si.getLicensePrice());
                p.sendMessage(PREFIX + "Du hast " + si.getName() + " §7in dein Shop-Sortiment aufgenommen.");
                Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat " + si.getName() + " §7in sein Shop-Sortiment aufgenommen. [Shop: " + s.getPublicName() + "]");
                Script.executeAsyncUpdate("INSERT INTO shopprice (amount, price, itemID, shopID) VALUES (" + 1 + ", " + si.getBuyPrice() + ", " + si.getID() + ", " + s.getID() + ")");
                p.closeInventory();
                return;
            }

            Script.executeAsyncUpdate("DELETE FROM shopprice WHERE shopID=" + s.getID() + " AND itemID=" + si.getID());
            p.sendMessage(PREFIX + "Du hast " + si.getName() + " §7aus deinem Shop-Sortiment entfernt.");
            Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat " + si.getName() + " §7aus sein Shop-Sortiment entfernt. [Shop: " + s.getPublicName() + "]");
            p.closeInventory();
            return;
        }
    }

    private static boolean containsType(ShopItem si, Shops shop) {
        for(ShopType type : si.getShopTypes()) {
            if(type != shop.getType()) continue;
            return true;
        }
        return false;
    }



}
