package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Player.Selfstorage;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveSelfstorage implements CommandExecutor {

    public static final String PREFIX = "§8[§cSelfstorage§8] §c" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removeselfstorage [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getPlayer(args[0]);

        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Selfstorage.hasSelfstorage(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Selfstorage-Room.");
            return true;
        }

        Selfstorage.removeSelfstorageAdmin(tg);
        p.sendMessage(PREFIX + "Du hast den Selfstorage-Room von §6" + tg.getName() + "§7 entfernt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Selfstorage-Room von " + tg.getName() + " entfernt.", true);
        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + "Dein Selfstorage-Room wurde von §6" + Messages.RANK_PREFIX(p) + "§7 entfernt.");
        } else {
            Script.addOfflineMessage(tg, PREFIX + "Dein Selfstorage-Room wurde von §6" + Messages.RANK_PREFIX(p) + "§7 entfernt.");
        }


        return false;
    }

}
