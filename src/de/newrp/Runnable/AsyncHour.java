package de.newrp.Runnable;

import de.newrp.API.Aktie;
import de.newrp.API.Krankheit;
import de.newrp.API.Script;
import de.newrp.API.Weather;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.Shops;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AsyncHour extends BukkitRunnable {

    @Override
    public void run() {
        for(Shops shop : Shops.values()) {
            if (shop.getOwner() == 0) return;
            int runningcost = 0;
            HashMap<Integer, ItemStack> c = shop.getItems();
            for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
                ItemStack is = n.getValue();
                if (is == null) {
                    continue;
                }
                runningcost += 10;
            }
            if(shop.acceptCard()) runningcost += 20;
            int totalcost = runningcost + shop.getRent();
            if(shop.getKasse() >= totalcost) {
                shop.removeKasse(shop.getRunningCost());
                shop.removeKasse(shop.getRent());
                Stadtkasse.addStadtkasse(shop.getRent());
                if(Script.getPlayer(shop.getOwner()) != null) {
                    Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Dein Shop §e" + shop.getPublicName() + " §7hat §e" + shop.getRent() + "€ §7Miete und §e" + shop.getRunningCost() + "€ §7Betriebskosten verloren.");
                }
            } else {
                Abteilung.Abteilungen.FINANZAMT.sendMessage(Shop.PREFIX + "Der Shop §e" + shop.getPublicName() + " §7hat nicht genug Geld für die Betriebskosten und Miete (Verdacht auf Steuerhinterziehung).");
                if(Script.getPlayer(shop.getOwner()) != null) {
                    Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Dein Shop §e" + shop.getPublicName() + " §7hat nicht genug Kapital um Miete und Betriebskosten zu bezahlen. Eine Meldung ans Finanzamt wurde abgesetzt.");
                    Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Beachte bitte, dass es eine Schließung deines Shops und eine Strafanzeige zur Folge haben kann.");
                }
            }
        }

        Aktie.update();

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(!Script.WORLD.hasStorm()) return;
            if(AFK.isAFK(all)) continue;
            if(SDuty.isSDuty(all)) continue;
            if(Script.getRandom(1, 100) > 3) continue;
            if(Script.WORLD.getHighestBlockYAt(all.getLocation()) < all.getLocation().getY()) {
                Krankheit.HUSTEN.add(Script.getNRPID(all));
            }
        }
    }
}
