package de.newrp.Runnable;

import de.newrp.API.Script;
import de.newrp.API.Weather;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Shop.Shops;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncHour extends BukkitRunnable {

    @Override
    public void run() {
        Weather.updateWeather();
        for(Shops shop : Shops.values()) {
            if (shop.getOwner() == 0) return;
            int totalcost = shop.getRunningCost() + shop.getRent();
            if(shop.getKasse() >= totalcost) {
                shop.removeKasse(shop.getRunningCost());
                shop.removeKasse(shop.getRent());
                Stadtkasse.addStadtkasse(shop.getRent());
                if(Script.getPlayer(shop.getOwner()) != null) {
                    Script.getPlayer(shop.getOwner()).sendMessage("§8[§eSHOP§8] §7Dein Shop §e" + shop.getPublicName() + " §7hat §e" + shop.getRent() + "€ §7Miete und §e" + shop.getRunningCost() + "€ §7Betriebskosten verloren.");
                }
            } else {
                Beruf.Berufe.GOVERNMENT.sendMessage("§8[§eSHOP§8] §7Der Shop §e" + shop.getPublicName() + " §7hat nicht genug Geld für die Betriebskosten und Miete (Verdacht auf Steuerhinterziehung).");
                if(Script.getPlayer(shop.getOwner()) != null) {
                    Script.getPlayer(shop.getOwner()).sendMessage("§8[§eSHOP§8] §7Dein Shop §e" + shop.getPublicName() + " §7hat nicht genug Kapital um Miete und Betriebskosten zu bezahlen. Eine Meldung ans Finanzamt wurde abgesetzt.");
                    Script.getPlayer(shop.getOwner()).sendMessage("§8[§eSHOP§8] §7Beachte bitte, dass es eine Schließung deines Shops und eine Strafanzeige zur Folge haben kann.");
                }
            }

        }
    }
}
