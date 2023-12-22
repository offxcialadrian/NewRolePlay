package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.API.Sperre;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TragenSperre implements CommandExecutor {

    private static String PREFIX = "§8[§aTragensperre§8] §a" + Messages.ARROW + " ";

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

        if(args.length != 1) {
            p.sendMessage("/tragensperre [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);

        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Sperre.TRAGENSPERRE.isActive(Script.getNRPID(tg))) {
            Sperre.TRAGENSPERRE.remove(Script.getNRPID(tg));
            p.sendMessage(PREFIX + "Du hast die Tragensperre von " + tg.getName() + " aufgehoben.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat die Tragensperre von " + tg.getName() + " aufgehoben.", true);
            if(tg.isOnline()) {
                tg.getPlayer().sendMessage(PREFIX + "Deine Tragensperre wurde von " + Script.getName(p) + " aufgehoben.");
            } else {
                Script.addOfflineMessage(tg, PREFIX + "Deine Tragensperre wurde von " + Script.getName(p) + " aufgehoben.");
            }
            return true;
        }


        return false;
    }
}
