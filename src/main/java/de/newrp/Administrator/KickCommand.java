package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(Messages.ERROR + "/kick [Spieler] [Grund]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst kicken.");
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }

        if (Script.isNRPTeam(tg) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        p.sendMessage("§8[§cKick§8] §cDu hast " + Script.getName(tg) + " gekickt.");
        tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + reason);
        Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " für §c§l" + reason + "§r§cgekickt.");

        return false;
    }
}
