package de.newrp.Administrator;

import de.newrp.API.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetSchwarzmarkt implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Schwarzmarkt.spawnRandom();
        Dealer.respawn();
        p.sendMessage(Schwarzmarkt.PREFIX + "Der Schwarzmarkt wurde zurückgesetzt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Schwarzmarkt zurückgesetzt.", true);

        return false;
    }
}
