package de.newrp.Administrator;

import de.newrp.API.Hologram;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetHologram implements CommandExecutor {

    private static final String PREFIX = "§8[§bHologram§8] §b» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Hologram.reload();
        p.sendMessage(PREFIX + "Die Hologramme wurden zurückgesetzt.");
        Script.sendTeamMessage(p, ChatColor.AQUA, "hat die Hologramme zurückgesetzt.", true);

        return false;
    }
}
