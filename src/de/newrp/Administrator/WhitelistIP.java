package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhitelistIP implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return false;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/whitelistip [IP]");
            return false;
        }

        String ip = args[0];
        if(!ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            p.sendMessage(Messages.ERROR + "Die IP ist ungültig.");
            return false;
        }

        if(Script.isWhitelistedIP(ip)) {
            p.sendMessage(Messages.ERROR + "Die IP ist bereits auf der Whitelist.");
            return false;
        }

        Script.addWhitelistedIP(ip);
        p.sendMessage(Script.PREFIX + "Die IP " + ip + " wurde auf die Whitelist gesetzt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat eine IP auf die Whitelist gesetzt.", true);

        return false;
    }
}
