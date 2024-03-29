package de.newrp.Runnable;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Player.Hotel;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AsyncHour extends BukkitRunnable {

    @Override
    public void run() {
        Aktie.update();
        Schwarzmarkt.spawnRandom();
        Hologram.reload();
        for(Shops shop : Shops.values()) {
            if (shop.getOwner() == 0) return;
            int runningcost = 0;
            HashMap<Integer, ItemStack> c = shop.getItems();
            if(shop.getType() != ShopType.HOTEL) {
                for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
                    ItemStack is = n.getValue();
                    if (is == null) {
                        continue;
                    }
                    runningcost += 10;
                }
                if (shop.acceptCard()) runningcost += 20;
            } else {
                Hotel.Hotels hotel = Hotel.Hotels.getHotelByShop(shop);
                assert hotel != null;
                for(Hotel.Rooms room : hotel.getRentedRooms()) {
                    runningcost += room.getPrice()/2;
                }
            }
            int totalcost = runningcost + shop.getRent();
            if(shop.getKasse() >= totalcost) {
                shop.removeKasse(shop.getRunningCost());
                shop.removeKasse(shop.getRent());
                Stadtkasse.addStadtkasse(shop.getRent(), "Miete von " + shop.getPublicName(), null);
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

        for(Organisation o : Organisation.values()) {
            int i = 0;
            for(Player all : o.getMembers()) {
                if(!AFK.isAFK(all)) {
                    i += 10;
                }
            }
            if(i == 0) continue;
            o.addExp(i);
        }


        for(Player all : Bukkit.getOnlinePlayers()) {
            if(!BuildMode.isInBuildMode(all)) all.getInventory().remove(Material.PLAYER_HEAD);
            if(!Script.WORLD.hasStorm()) return;
            if(AFK.isAFK(all)) continue;
            if(SDuty.isSDuty(all)) continue;
            if(Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(all))) {
                all.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 1));
                all.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
                all.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
                all.sendMessage(Messages.INFO + "Du hast Entzugserscheinungen. Lasse dich von einem Arzt behandeln.");
            }
            if(Script.getRandom(1, 100) > 3) continue;
            if(Script.WORLD.getHighestBlockYAt(all.getLocation()) < all.getLocation().getY()) {
                Krankheit.HUSTEN.add(Script.getNRPID(all));
            }
        }
    }
}
