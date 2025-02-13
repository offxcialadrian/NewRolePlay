package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Shop.gasstations.GasStationBuyHandler;
import de.newrp.Vehicle.Car;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TankenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            Bukkit.getLogger().info("This command is only executable as a player");
            return false;
        }

        final Player player = (Player) commandSender;
        final Shops shop = Shops.getShopByLocation(player.getLocation(), 20.0f);
        if (shop == null || shop.getType() != ShopType.GAS_STATION) {
            player.sendMessage(Messages.ERROR + "Du bist bei keiner Tankstelle!");
            return false;
        }

        Car car = Car.getNearbyCar(player, 2);
        if (car == null) {
            player.sendMessage(Messages.ERROR + "Du bist bei keinem Auto!");
        } else {
            if (car.isStarted()) {
                player.sendMessage(Component.text(Car.PREFIX + "Du musst zuvor deinen Motor ausschalten!"));
                return true;
            }

            float max = 100 - car.getFuel();
            float amount = 0;
            if (args.length == 0) {
                amount = max;
            } else {
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Du musst eine Zahl angeben!");
                }
                if (amount == 0) return true;
                if (amount > max) amount = max;
            }

            if (amount > 0) {
                if (Script.getMoney(player, PaymentType.CASH) >= amount) {
                    player.sendMessage(Component.text(Car.PREFIX + "Du hast " + (double) Math.round(amount * 10) / 10 + " Liter getankt."));
                    GasStationBuyHandler.refuels.put(player, car);
                    GasStationBuyHandler.amount.put(player, amount);
                } else {
                    player.sendMessage(Component.text(Car.PREFIX + "Du hast nicht genug Bargeld!"));
                }
            } else {
                player.sendMessage(Component.text(Car.PREFIX + "Dein Tank ist voll!"));
            }
        }

        return false;
    }
}
