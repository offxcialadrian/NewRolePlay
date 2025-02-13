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
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Player.Hotel;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.config.BizWarConfig;
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
import java.util.Random;

public class AsyncHour extends BukkitRunnable {

    @Override
    public void run() {
        try {
            //Aktie.update();
            Debug.debug("Async Hour tick");

            for (GangwarZones zone : GangwarZones.values()) {
                if (zone.getOwner() != null) {
                    zone.getOwner().sendMessage(GangwarCommand.PREFIX + "Die Organisation hat 100€ durch die Zone §e" + zone.getName() + " §7erhalten.");
                    zone.getOwner().addKasse(100);
                }
            }

            for (Beruf.Berufe berufe : Beruf.Berufe.values()) {
                Stadtkasse.removeStadtkasse(berufe.getLeasedAmount() * (berufe.getCarType().getTax()), "Leasinggebühren " + berufe.getLeasedAmount() + "x " + berufe.getName());
            }

            if (Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() + Abteilung.Abteilungen.OBERARZT.getOnlineMembers().size() + Abteilung.Abteilungen.CHEFARZT.getOnlineMembers().size() + Abteilung.Abteilungen.DIREKTOR.getOnlineMembers().size() > 2) {
                if (new Random().nextInt(4) == 0) {
                    if (new Random().nextInt(3) == 0) {
                        Shops[] b = new Shops[]{Shops.GUNSHOP, Shops.ANGELLADEN, Shops.DOENER, Shops.SUPERMARKT, Shops.APOTHEKE, Shops.APOTHEKE_AEKI,
                                Shops.BLUMENLADEN, Shops.SHOE_MALL, Shops.JAGDHUETTE, Shops.ELEKTRO_GANG, Shops.CAFE, Shops.FLOWER,
                                Shops.GEMUESE, Shops.BAECKEREI, Shops.IKEA};
                        new FeuerwehrEinsatz(null).start(b[Script.getRandom(0, (b.length - 1))]);
                    } else {
                        House h = House.HOUSES.get(Script.getRandom(0, House.HOUSES.size() - 1));
                        new FeuerwehrEinsatz(h).start();
                    }
                }
            }

            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT userID, amount, zins, id FROM loans WHERE time < " + System.currentTimeMillis())) {
                while (rs.next()) {
                    OfflinePlayer p = Script.getOfflinePlayer(rs.getInt("userID"));
                    int amount = rs.getInt("amount");
                    double interest = rs.getDouble("zins");
                    int id = rs.getInt("id");

                    int sum = (int) (amount + Script.getPercent(interest, amount));

                    Stadtkasse.addStadtkasse(sum, "Kredit von " + Script.getNameInDB(p), null);
                    Beruf.Berufe.GOVERNMENT.sendMessage(Loan.PREFIX + "Der Kredit von " + Script.getNameInDB(p) + " wurde abbezahlt.");
                    Script.removeMoney(p, PaymentType.BANK, sum);

                    Script.executeAsyncUpdate("DELETE FROM loans WHERE id=" + id);
                    if (p.isOnline()) {
                        Player player = p.getPlayer();
                        player.sendMessage(Loan.PREFIX + "Dein Kredit wurde abbezahlt.");
                    } else {
                        Script.addOfflineMessage(p, Loan.PREFIX + "Dein Kredit wurde abbezahlt.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (Shops shop : Shops.values()) {
                if (shop.getOwner() == 0) continue;
                if (shop.isLocked()) continue;
                int runningcost = shop.getRunningCost();
                int totalcost = runningcost + shop.getRent();
                if (shop.getKasse() >= totalcost) {
                    shop.removeKasse(runningcost);
                    shop.removeKasse(shop.getRent());
                    Stadtkasse.addStadtkasse(shop.getRent(), "Miete von " + shop.getPublicName(), null);
                    if (Script.getPlayer(shop.getOwner()) != null) {
                        Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Dein Shop §e" + shop.getPublicName() + " §7hat §e" + shop.getRent() + "€ §7Miete und §e" + runningcost + "€ §7Betriebskosten bezahlt.");
                    }
                } else {
                    Abteilung.Abteilungen.FINANZAMT.sendMessage(Shop.PREFIX + "Der Shop §e" + shop.getPublicName() + " §7hat nicht genug Geld für die Betriebskosten und Miete (Verdacht auf Steuerhinterziehung).");
                    if (Script.getPlayer(shop.getOwner()) != null) {
                        Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Dein Shop §e" + shop.getPublicName() + " §7hat nicht genug Kapital um Miete und Betriebskosten zu bezahlen. Eine Meldung ans Finanzamt wurde abgesetzt.");
                        Script.getPlayer(shop.getOwner()).sendMessage(Shop.PREFIX + "Beachte bitte, dass es eine Schließung deines Shops und eine Strafanzeige zur Folge haben kann.");
                    }
                }
            }

            final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);
            final BizWarConfig bizWarConfig = DependencyContainer.getContainer().getDependency(BizWarConfig.class);
            for (Organisation o : Organisation.values()) {
                int shopExp = 0;
                for (Shops shops : bizWarService.getShopsOfFaction(o)) {
                    final int profitPerHour = bizWarConfig.getShopConfigs().stream().filter(e -> e.getShopId() == shops.getID()).findFirst().get().getProfitPerHour();
                    o.addKasse(profitPerHour);
                    o.sendMessage("§8[§eOrganisation§8] §e" + Messages.ARROW + " §7Deine Organisation hat §e" + profitPerHour + "€ und 2 Exp §7durch den Shop §e" + shops.getPublicName() + " §7erhalten.");
                    shopExp += 2;
                }

                if(shopExp != 0) {
                    o.addExp(shopExp, false);
                    o.sendMessage("§8[§eOrganisation§8] §e" + Messages.ARROW + " §7Deine Organisation hat §e" + shopExp + " Exp §7durch die Shops erhalten.");
                }

                int i = 0;
                for (Player all : o.getMembers()) {
                    if (!AFK.isAFK(all)) {
                        i += 10;
                    }
                }
                if (i == 0) continue;
                o.addExp(i, false);
                o.sendMessage("§8[§eOrganisation§8] §e" + Messages.ARROW + " §7Deine Organisation hat §e" + i + " §7Erfahrungspunkte durch §e" + (i / 10) + " aktive Mitglieder §7erhalten.");
             }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!BuildMode.isInBuildMode(all)) all.getInventory().remove(Material.PLAYER_HEAD);
                if (all.getInventory().getHelmet() != null) all.getInventory().getHelmet().setType(Material.AIR);
                if (!Script.WORLD.hasStorm()) return;
                if (AFK.isAFK(all)) continue;
                if (SDuty.isSDuty(all)) continue;
                if (Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(all))) {
                    all.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 1));
                    all.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
                    all.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
                    all.sendMessage(Messages.INFO + "Du hast Entzugserscheinungen. Lasse dich von einem Arzt behandeln.");
                }
                if (Script.getRandom(1, 100) > 3) continue;
                if (Script.WORLD.getHighestBlockYAt(all.getLocation()) <= all.getLocation().getY()) {
                    Krankheit.HUSTEN.add(Script.getNRPID(all));
                }
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }
}
