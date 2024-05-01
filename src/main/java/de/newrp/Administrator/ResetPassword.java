package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Player.Passwort;
import org.apache.logging.log4j.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetPassword implements CommandExecutor {

    private static final String PREFIX = "§8[§6ResetPassword§8] §8» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.ADMINISTRATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/resetpassword [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler existiert nicht.");
            return true;
        }

        Passwort.removePasswort(tg);
        p.sendMessage(PREFIX + "Du hast das Passwort von §6" + tg.getName() + " §7zurückgesetzt.");
        Script.sendTeamMessage(p, ChatColor.GOLD, "hat das Passwort von " + tg.getName() + " zurückgesetzt.", true);
        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + "Dein Passwort wurde zurückgesetzt.");
        } else {
            Script.addOfflineMessage(tg, PREFIX + "Dein Passwort wurde zurückgesetzt.");
        }

        return false;
    }
}
