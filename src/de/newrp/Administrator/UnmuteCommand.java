package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {

        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/unmute [Spieler]");
            return true;
        }


        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Punish.isMuted(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht gemutet.");
            return true;
        }

        Script.executeAsyncUpdate("DELETE FROM mute WHERE nrp_id = '" + Script.getNRPID(tg) + "'");
        p.sendMessage(Punish.PREFIX + "Du hast " + tg.getName() + " entmutet.");
        tg.sendMessage(Punish.PREFIX + "Du wurdest von " + p.getName() + " entmutet.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " entmutet.", true);


        return false;
    }
}
