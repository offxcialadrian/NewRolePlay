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
            p.sendMessage(Messages.ERROR + "/rnrp [Nachricht]");
            return true;
        }

        String msg = String.join(" ", args);

        if(Script.hasRank(p, Rank.OWNER, false)) {
            if(args[0].equalsIgnoreCase("-a")) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (isNRPTeam(all) && Script.hasRank(all, Rank.ADMINISTRATOR, false)) {
                        all.sendMessage("§c§lR§5§lNRP » §5" + Messages.RANK_PREFIX(p) + ": §3" + msg.replace("-a", ""));
                    }
                }
                return true;
            }
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (isNRPTeam(all) && Script.hasRank(all, Script.getRank(p), false)) {
                all.sendMessage("§c§lR§5§lNRP » §5" + Messages.RANK_PREFIX(p) + ": §3" + msg);
            }
        }

        return true;
    }
}
