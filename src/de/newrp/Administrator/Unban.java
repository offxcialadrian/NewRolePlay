package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unban implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.MODERATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/unban [Spieler]");
            return true;
        }


        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (Punish.getBanUntil(tg) == 1 || (Punish.getBanUntil(tg) <= System.currentTimeMillis() && Punish.getBanUntil(tg) != 0)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht gebannt.");
            return true;
        }

        String reason = Punish.getBanReason(tg);
        Punish.unban(tg);
        p.sendMessage(Punish.PREFIX + "Du hast " + tg.getName() + " entbannt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " entbannt.", true);
        if(reason.equalsIgnoreCase("maximale Anzahl an Warns überschritten")) {
            p.sendMessage(Messages.INFO + "Bitte beachte, dass du den Spieler nur für seine 3/3 Warns entbannt hast, sollte er wegen noch einem Grund gebannt sein, ist dieser Bann noch aktiv.");
            Script.sendTeamMessage(Messages.INFO + tg.getName() + " wurde automatisch ein Warn entfernt.");
            Script.executeAsyncUpdate("DELETE FROM warns WHERE nrp_id = '" + Script.getNRPID(tg) + "' ORDER BY id DESC LIMIT 1;");
        }

        return false;
    }
}
