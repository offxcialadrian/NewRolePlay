package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SchwarzmarktLocation implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.MODERATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/schwarzmarktlocation");
            return true;
        }

        p.sendMessage(Schwarzmarkt.PREFIX + "Der Schwarzmarkt befindet sich bei §6" + Schwarzmarkt.getSchwarzmarkt().getName() + "§7.");

        return true;
    }
}
