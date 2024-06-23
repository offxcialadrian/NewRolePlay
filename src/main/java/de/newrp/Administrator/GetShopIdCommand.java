package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Shop.Shops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetShopIdCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;

        if(!Script.hasRankExact(player, Rank.DEVELOPER, Rank.ADMINISTRATOR, Rank.OWNER)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final Shops shops = Shops.getShopByLocation(player.getLocation(), 10f);
        if(shops != null) {
            player.sendMessage("Id: "  + shops.getID() + " (" + shops.getPublicName() + ")");
        }
        return false;
    }
}
