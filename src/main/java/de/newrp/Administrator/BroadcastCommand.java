package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/broadcast [Nachricht]");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }

        String msg = sb.toString().trim();
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(" ");
            all.sendMessage(" §7§m--------- §8[§c§lANKÜNDIGUNG§8] §7§m---------");
            all.sendMessage("   §c" + Script.getName(p) + " §8» §c" + msg);
            all.sendMessage(" §7§m-------------------------------------");
            all.sendMessage(" ");
        }
        Log.HIGH.write(p, "hat einen Broadcast gesendet (" + msg + ")");


        return false;
    }
}
