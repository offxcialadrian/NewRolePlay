package de.newrp.Runnable;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Gangwar.GangwarZones;
import de.newrp.Government.Loan;
import de.newrp.Government.Stadtkasse;
import de.newrp.House.House;
import de.newrp.Medic.FeuerwehrEinsatz;
import de.newrp.Organisationen.LabBreakIn;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Player.Hotel;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AsyncHour extends BukkitRunnable {

    @Override
    public void run() {
        //Aktie.update();
        Schwarzmarkt.spawnRandom();
        Hologram.reload();
        LabBreakIn.repairDoors(false);

        for(GangwarZones zone : GangwarZones.values()) {
            zone.getOwner().sendMessage(GangwarCommand.PREFIX + "Die Organisation hat 100€ durch die Zone §e" + zone.getName() + " §7erhalten.");
            zone.getOwner().addKasse(100);
        }

        if (Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() >= 2) {
            if (Script.getRandom(1, 7) == Script.getRandom(1, 7)) {
                if (Script.getRandom(1, 3) == Script.getRandom(1, 3)) {
                    Shops[] b = new Shops[]{Shops.GUNSHOP, Shops.ANGELLADEN, Shops.DOENER, Shops.SUPERMARKT, Shops.APOTHEKE, Shops.APOTHEKE_AEKI,
                            Shops.BLUMENLADEN, Shops.SHOE_MALL, Shops.JAGDHUETTE, Shops.HANKYS, Shops.CAFE, Shops.FLOWER,
                            Shops.GEMUESE, Shops.BAECKERI, Shops.IKEA};
                    new FeuerwehrEinsatz(null).start(b[Script.getRandom(0, (b.length - 1))]);
                } else {
                    House h = House.HOUSES.get(Script.getRandom(0, House.HOUSES.size() - 1));
                    new FeuerwehrEinsatz(h).start();
                }
            }
        }

        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM loans WHERE time>" + System.currentTimeMillis())) {
            while (rs.next()) {
                OfflinePlayer p = Script.getOfflinePlayer(rs.getInt("userID"));
                int amount = rs.getInt("amount");
                double interest = rs.getDouble("interest");
                int id = rs.getInt("id");

                int sum = (int) (amount + Script.getPercent(interest, amount));

                Stadtkasse.addStadtkasse(sum, "Kredit von " + Script.getNameInDB(p), null);
                Beruf.Berufe.GOVERNMENT.sendMessage(Loan.PREFIX + "Der Kredit von " + Script.getNameInDB(p) + " wurde abbezahlt.");
                Script.removeMoney(p, PaymentType.BANK, sum);

                Script.executeAsyncUpdate("DELETE FROM loans WHERE id=" + id);
                if(p.isOnline()) {
                    Player player = p.getPlayer();
                    player.sendMessage(Loan.PREFIX + "Dein Kredit wurde abbezahlt.");
                } else {
                    Script.addOfflineMessage(p, Loan.PREFIX + "Dein Kredit wurde abbezahlt.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Shops shop : Shops.values()) {
            if (shop.getOwner() == 0) continue;
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
                shop.removeKasse(runningcost);
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
            all.getInventory().getHelmet().setType(Material.AIR);
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
            if(Script.WORLD.getHighestBlockYAt(all.getLocation()) <= all.getLocation().getY()) {
                Krankheit.HUSTEN.add(Script.getNRPID(all));
            }
        }
    }
}
