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

public class UnwarnCommand implements CommandExecutor {

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
            p.sendMessage(Messages.ERROR + "/removewarn [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null && Script.getNRPID(args[0]) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        OfflinePlayer offtg = Script.getOfflinePlayer(Script.getNRPID(args[0]));
        if (tg != null) {
            if (Punish.getWarns(tg) == 0) {
                p.sendMessage(Messages.ERROR + "Der Spieler hat keine Warns.");
                return true;
            }

            tg.sendMessage(Punish.PREFIX + "Du hast einen Warn von " + p.getName() + " entfernt bekommen.");
            return true;
        }


        Punish.removeWarn(offtg);
        p.sendMessage(Punish.PREFIX + "Du hast " + offtg.getName() + " einen Warn entfernt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + offtg.getName() + " einen Warn entfernt.", true);

        if(Punish.getBanReason(offtg) == null) return true;
        String reason = Punish.getBanReason(offtg);
        if(reason.equalsIgnoreCase("maximale Anzahl an Warns überschritten")) {
            p.sendMessage(Messages.INFO + "Du hast " + offtg.getName() + " nur für seine 3/3 Warns automatisch entbannt, sollte er wegen noch einem Grund gebannt sein, ist dieser Bann noch aktiv.");
            Script.sendTeamMessage(Messages.INFO + offtg.getName() + " wurde automatisch entbannt, sofern 3/3 Warns der einzige aktive Bann war.");
            Punish.unban(offtg);
        }
        return false;
    }
}
