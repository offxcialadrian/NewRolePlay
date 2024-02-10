package de.newrp.Administrator;

import de.newrp.API.Premium;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddPremiumToPlayer implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(cs instanceof ConsoleCommandSender)) return true;

        Player player = Script.getPlayer(args[0]);
        int days = Integer.parseInt(args[1].replace("d", ""));

        player.sendMessage(Premium.PREFIX + "§7Vielen Dank für deinen Einkauf.");
        Premium.addPremiumStorage(player, days, false);
        Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(player) + " hat " + days + " Tage im Shop Premium erworben.");

        return false;
    }
}
