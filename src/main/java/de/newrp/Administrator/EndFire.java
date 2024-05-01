package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Medic.FeuerwehrEinsatz;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndFire implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length > 0) {
            p.sendMessage(Messages.ERROR + "/endfire");
            return true;
        }

        FeuerwehrEinsatz.removeTotalFire();
        p.sendMessage(Beruf.PREFIX + "§cDu hast das Feuer gelöscht!");
        Script.sendTeamMessage(p, ChatColor.DARK_RED, "hat das Feuer gelöscht.", true);



        return false;
    }
}
