package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteStraftat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Regierung.");
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Regierung.");
            return true;
        }

        if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.JUSTIZMINISTERIUM || Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Justizministerium.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/deletefahndungsgrund [ID]");
            return true;
        }

        if (!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "Die ID muss eine Zahl sein.");
            return true;
        }

        int id = Integer.parseInt(args[0]);
        if (id < 1) {
            p.sendMessage(Messages.ERROR + "Die ID muss mindestens 1 sein.");
            return true;
        }

        if (!Straftat.isStraftat(id)) {
            p.sendMessage(Messages.ERROR + "Diese Straftat existiert nicht.");
            return true;
        }

        String reason = Straftat.getReason(id);
        p.sendMessage(Straftat.PREFIX + "Die Straftat §e" + reason + " §7(ID: §e" + id + "§7) wurde gelöscht.");
        Beruf.Berufe.NEWS.sendMessage(Straftat.PREFIX + "Die Straftat §e" + reason + " §7(ID: §e" + id + "§7) wurde von §e" + Script.getName(p) + " §7gelöscht.");
        Beruf.Berufe.POLICE.sendMessage(Straftat.PREFIX + "Die Straftat §e" + reason + " §7(ID: §e" + id + "§7) wurde von §e" + Script.getName(p) + " §7gelöscht.");
        Script.sendTeamMessage(Straftat.PREFIX + "Die Straftat §e" + reason + " §7(ID: §e" + id + "§7) wurde von §e" + Script.getName(p) + " §7gelöscht.");
        Straftat.delete(id);
        return false;
    }
}
