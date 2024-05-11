package de.newrp.Administrator;

import de.newrp.API.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
        final String msg = String.join(" ", Arrays.copyOfRange(args, args[0].equalsIgnoreCase("confirm") ? 1 : 0, args.length));
        Debug.debug("BC msg " + msg);

        if(args[0].equalsIgnoreCase("confirm")) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                all.sendMessage(" ");
                all.sendMessage(" §7§m--------- §8[§c§lANKÜNDIGUNG§8] §7§m---------");
                all.sendMessage("   §c" + Script.getName(p) + " §8» §c" + msg);
                all.sendMessage(" §7§m-------------------------------------");
                all.sendMessage(" ");
            }
            Log.HIGH.write(p, "hat einen Broadcast gesendet (" + msg + ")");
            return true;
        }

        Script.sendClickableMessage(p, "§8" + Messages.ARROW + " §cKlicke hier um deine Broadcast abzuschicken!", "/bc confirm " + msg, "§cKlicke hier um die Broadcast abzuschicken!");
        return false;
    }
}
