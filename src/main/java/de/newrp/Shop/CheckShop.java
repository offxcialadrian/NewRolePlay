package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CheckShop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p, Beruf.Berufe.GOVERNMENT) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT || Beruf.getAbteilung(p) != Abteilung.Abteilungen.FINANZAMT) {
            if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) && !SDuty.isSDuty(p)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
        }

        Shops shop = null;
        for (Shops shops : Shops.values()) {
            if (shops.getBuyLocation() != null) {
                if (p.getLocation().distance(shops.getBuyLocation()) < 5) {
                    shop = shops;
                }
            }
        }

        if (shop == null) {
            p.sendMessage(Messages.ERROR + "§cDu bist nicht in der nähe von einem Shop.");
            return true;
        }

        if(shop.getOwner() == 0) {
            p.sendMessage(Shop.PREFIX + "Dieser Shop wird derzeit von der Stadt verkauft.");
            return true;
        }

        if(shop.getOwner() == -1) {
            p.sendMessage(Shop.PREFIX + "Dieser Shop ist derzeit nicht verfügbar.");
            return true;
        }

        if(shop.getOwner() > 0) {
            p.sendMessage("§6=== " + shop.getPublicName() + " ===");
            p.sendMessage("§8» " + "§6Besitzer: " + Script.getOfflinePlayer(shop.getOwner()).getName());
            p.sendMessage("§8» " + "§6Grundpreis: " + shop.getPrice() + "€");
            p.sendMessage("§8» " + "§6Kasse: " + shop.getKasse() + "€");
            // p.sendMessage("§8» " + "§6Betriebskosten: " + shop.getRunningCost() + "€");
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


        return false;
    }
}
