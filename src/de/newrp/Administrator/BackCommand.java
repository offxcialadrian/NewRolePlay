package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 0) {
            if(!Teleport.back.containsKey(p)) {
                p.sendMessage(Messages.ERROR + "Du hast dich noch nicht teleportiert.");
                return true;
            }

            p.teleport(Teleport.back.get(p));
            p.sendMessage(Teleport.PREFIX + "Du wurdest zu deinem letzten Standort teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sich zu seinem letzten Standort teleportiert.", true);
            Teleport.back.remove(p);
            return true;
        }

        if(args.length == 1) {
            Player tg = Script.getPlayer(args[0]);
            if(tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(!Teleport.back.containsKey(tg)) {
                p.sendMessage(Messages.ERROR + "Kein letzter Standort gefunden.");
                return true;
            }

            tg.teleport(Teleport.back.get(tg));
            p.sendMessage(Teleport.PREFIX + "Du hast " + Script.getName(tg) + " zu seinem letzten Standort teleportiert.");
            tg.sendMessage(Teleport.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat dich zu deinem letzten Standort teleportiert.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " zu seinem letzten Standort teleportiert.", true);
            Teleport.back.remove(tg);
            return true;
        }

        return false;
    }
}
