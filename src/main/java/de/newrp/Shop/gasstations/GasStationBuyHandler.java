package de.newrp.Shop.gasstations;

import de.newrp.API.Log;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import de.newrp.Shop.BuyClick;
import de.newrp.Shop.Shop;
import de.newrp.Shop.Shops;
import de.newrp.Shop.generic.GenericBuyHandler;
import de.newrp.Vehicle.Car;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class GasStationBuyHandler implements GenericBuyHandler {

    public static HashMap<Player, Car> refuels = new HashMap<>();
    public static HashMap<Player, Float> amount = new HashMap<>();

    @Override
    public boolean buyItem(Player player, Shops shop, final Object... args) {
        if (refuels.containsKey(player)) {
            int money = Math.round(amount.get(player) * 2.0F) + 1;
            if (Script.removeMoney(player, PaymentType.CASH, money)) {
                refuels.get(player).fill(amount.get(player));
                refuels.remove(player);
                BuyClick.sendMessage(player, "Vielen Dank fürs Tanken!", shop);
                int m = (int) Math.ceil(money * 0.6);
                if (shop.getOwner() > 0) {
                    shop.addKasse(m);
                    Log.NORMAL.write(player, "hat für " + money + "€ getankt.");
                    if (Objects.requireNonNull(Script.getOfflinePlayer(shop.getOwner())).isOnline()) {
                        Script.sendActionBar(Objects.requireNonNull(Script.getPlayer(shop.getOwner())), Shop.PREFIX + "Dein Shop §6" + shop.getPublicName() + " §7hat §6" + m + "€ §7Gewinn gemacht aus dem Verkauf von §6" + Math.ceil(amount.get(player)) + "L Kraftstoff §7(§6" + money + "€§7)");
                    }
                } else {
                    Stadtkasse.addStadtkasse(money, "Gewinn aus dem Verkauf von Kraftstoff (Shop: " + shop.getPublicName() + ")", null);
                    Stadtkasse.removeStadtkasse((money - m), "Einkauf von Kraftstoff (Shop: " + shop.getPublicName() + ")");
                    Log.NORMAL.write(player, "hat für " + money + "€ getankt.");
                }
                amount.remove(player);
            } else {
                player.sendMessage(Component.text(Car.PREFIX + "Du hast nicht genug Bargeld!"));
            }
            return true;
        }
        return false;
    }

}
