package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Entertainment.Lotto;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class ForceLotto implements CommandExecutor {


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

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/forcelotto");
            return true;
        }

        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY & Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            p.sendMessage(Messages.ERROR + "Heute ist kein Lotto-Tag!");
            return true;
        }

        Lotto.start();
        p.sendMessage(Messages.INFO + "Du hast das Lotto wurde gestartet.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat das Lotto gestartet.", true);

        return false;
    }
}
