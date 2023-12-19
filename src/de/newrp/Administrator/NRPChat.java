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

import static de.newrp.API.Script.isInTestMode;
import static de.newrp.API.Script.isNRPTeam;

public class NRPChat implements CommandExecutor {

    private static boolean ONLY_RANK;

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

        ONLY_RANK = args[0].equalsIgnoreCase("-r");

        String msg = String.join(" ", args);

        if (ONLY_RANK) {
            msg = msg.replace("-r ", "");
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    all.sendMessage("§5§lRNRP » §5" + Script.getName(p) + ": §3" + msg);
                }
            }
        } else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    all.sendMessage("§5§lNRP » §5" + Script.getName(p) + ": §3" + msg);

                }
            }
        }

        return false;
    }
}
