package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.newrp.API.Script.isNRPTeam;

public class RNRPChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/nrp [Nachricht]");
            return true;
        }

        String msg = String.join(" ", args);


        for (Player all : Bukkit.getOnlinePlayers()) {
            if (isNRPTeam(all) && Script.hasRank(all, Script.getRank(p), false)) {
                all.sendMessage("§c§lR§5§lNRP » §5" + Script.getName(p) + ": §3" + msg);
            }
        }

        return true;
    }
}
