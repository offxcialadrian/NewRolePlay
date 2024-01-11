package de.newrp.Administrator;

import de.newrp.API.*;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.security.auth.callback.CallbackHandler;

public class RemoveCheckpoints implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removecheckpoints [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getPlayer(args[0]);
        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Checkpoints.hasCheckpoints(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keine Checkpoints.");
            return true;
        }

        Checkpoints.clear(tg);
        p.sendMessage(Checkpoints.PREFIX + "Du hast die Checkpoints von §6" + tg.getName() + " §7erfolgreich entfernt.");
        Log.WARNING.write(p, "hat die Checkpoints von " + tg.getName() + " entfernt.");
        Log.HIGH.write(tg, "wurde von " + p.getName() + " die Checkpoints entfernt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat die Checkpoints von " + tg.getName() + " entfernt.", true);
        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(Checkpoints.PREFIX + "Deine Checkpoints wurden von §6" + Messages.RANK_PREFIX(p) + " §7entfernt.");
            Cache.loadScoreboard(tg.getPlayer());
        } else {
            Script.addOfflineMessage(tg, Checkpoints.PREFIX + "Deine Checkpoints wurden von §6" + Messages.RANK_PREFIX(p) + " §7entfernt.");
        }


        return false;
    }
}
