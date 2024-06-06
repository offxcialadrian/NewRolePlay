package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetJsonLocation implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        if(!Script.hasRankExact(player, Rank.ADMINISTRATOR, Rank.DEVELOPER, Rank.OWNER)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final String json = "{\"x\": " + player.getLocation().getX() + ", \"y\": " + player.getLocation().getY()+ ", \"z\": " + player.getLocation().getZ() + ", \"yaw\":" + (int) player.getLocation().getYaw() + ", \"pitch\": 0}";
        Script.sendCopyMessage(player, "§8[§eGetJsonLocation§8] §e" + Messages.ARROW + "§a§lKopieren", json, "§aKopieren");
        return false;
    }
}
