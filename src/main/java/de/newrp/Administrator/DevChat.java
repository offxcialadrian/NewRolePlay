package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DevChat implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/dev [Nachricht]");
            return true;
        }


        String msg = String.join(" ", args);
        for (Player all : Script.getNRPTeam()) {
            all.sendMessage("§b§lDEV » §b" + Messages.RANK_PREFIX(p) + ": §3" + msg);
        }


        return false;
    }
}

