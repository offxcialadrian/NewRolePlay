package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveStadtkasse implements CommandExecutor {

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
            p.sendMessage(Messages.ERROR + "/removestadtkasse [Summe]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "Du musst eine Zahl angeben.");
            return true;
        }

        int summe = Integer.parseInt(args[0]);
        if(summe < 0) {
            p.sendMessage(Messages.ERROR + "Du musst eine positive Zahl angeben.");
            return true;
        }

        Stadtkasse.removeStadtkasse(summe, "Entnahme durch " + Messages.RANK_PREFIX(p));
        p.sendMessage(Stadtkasse.PREFIX + "Du hast §6" + summe + "€ §7aus der Stadtkasse entfernt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat §6" + summe + "€ §7aus der Stadtkasse entfernt.", true);
        Beruf.Berufe.GOVERNMENT.sendMessage(Stadtkasse.PREFIX + "§6" + Messages.RANK_PREFIX(p) + " §7hat §6" + summe + "€ §7aus der Stadtkasse entfernt.");

        return false;
    }
}
