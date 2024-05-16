package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.API.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Team.Teams team = Team.getTeam(player);

        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false) && team != Team.Teams.ENTWICKLUNG) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (double tps : Bukkit.getServer().getTPS()) {
            stringBuilder.append("§a" + (int) tps).append("§7, ");
        }
        stringBuilder.setLength(stringBuilder.length() - 4);
        player.sendMessage("§8[§bTPS§8] §7" + Messages.ARROW + " " + stringBuilder);
        return false;
    }
}
