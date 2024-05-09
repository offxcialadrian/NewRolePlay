package de.newrp.Shop.gasstations;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Shop.*;
import de.newrp.Shop.generic.GenericBuyHandler;
import de.newrp.Vehicle.Car;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class GasStationBuyHandler implements GenericBuyHandler {

    public static HashMap<Player, Car> refuels = new HashMap<>();
    public static HashMap<Player, Float> amount = new HashMap<>();

    @Override
    public boolean buyItem(Player player, Shops shop, final Object... args) {
        if (refuels.containsKey(player)) {
            if (Script.removeMoney(player, PaymentType.CASH, Math.round(amount.get(player) * 2.0F) + 1)) {
                refuels.get(player).fill(amount.get(player));
                refuels.remove(player);
                amount.remove(player);
                BuyClick.sendMessage(player, "Vielen Dank fürs Tanken!");
            } else {
                player.sendMessage(Component.text(Car.PREFIX + "Du hast nicht genug Bargeld!"));

            }
            return true;
        } else {
            player.sendMessage(Messages.INFO + "Tankstelle :3"); // UwU ?

            player.getInventory().addItem(ShopItem.KANISTER.getItemStack()); // pls
            player.getInventory().addItem(ShopItem.TOOLS.getItemStack()); // pls
        }
        return false;
    }

}
