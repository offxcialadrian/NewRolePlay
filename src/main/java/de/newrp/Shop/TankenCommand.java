package de.newrp.Shop;

import de.newrp.API.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TankenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            Bukkit.getLogger().info("This command is only executable as a player");
            return false;
        }

        final Player player = (Player) commandSender;
        final Shops shop = Shops.getShopByLocation(player.getLocation(), 10.0f);
        if(shop == null) {
            player.sendMessage(Messages.ERROR + "Du bist bei keiner Tankstelle.");
            return false;
        }

        if(shop.getType() != ShopType.GAS_STATION) {
            player.sendMessage(Messages.ERROR + "Du bist bei keiner Tankstelle.");
            return false;
        }


        return false;
    }
}
