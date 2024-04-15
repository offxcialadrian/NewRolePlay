package de.newrp.Police;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Staatsmeldung implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf!");
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.Berufe.POLICE) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist!");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "Bitte gib eine Nachricht an!");
            return true;
        }

        if (!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst!");
            return true;
        }

        if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.POLIZEIDIREKTOR && Beruf.getAbteilung(p) != Abteilung.Abteilungen.L_POLIZEIDIREKTOR && Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizeidirektor!");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String msg = sb.toString();
        Bukkit.broadcastMessage("§8=== §b§lSTAATSMELDUNG §8===");
        Bukkit.broadcastMessage("§7" + msg);
        Bukkit.broadcastMessage("§8===============");
        Script.sendTeamMessage("§7Diese Meldung wurde von §b" + Script.getName(p) + " §7gesendet.");
        Log.NORMAL.write(p, "hat eine Staatsmeldung gesendet (" + msg + ")");

        return false;
    }
}
