package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unarrest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(SDuty.isSDuty(p) && Script.hasRank(p, Rank.MODERATOR, false)) {
            if(args.length != 1) {
                p.sendMessage(Messages.ERROR + "/unarrest [Spieler]");
                return true;
            }

            Player tg = Script.getPlayer(args[0]);
            if(tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(!Jail.isInJail(tg)) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht im Gefängnis.");
                return true;
            }

            Jail.unarrest(tg);
            p.sendMessage(Jail.PREFIX + "Du hast §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");
            Beruf.Berufe.POLICE.sendMessage(Jail.PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");
            Beruf.Berufe.GOVERNMENT.sendMessage(Jail.PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");
        }

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getAbteilung(p).equals(Abteilung.Abteilungen.JUSTIZMINISTERIUM)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.isLeader(p, true) && !Beruf.getAbteilung(p).equals(Abteilung.Abteilungen.JUSTIZMINISTERIUM)) {
            p.sendMessage(Messages.ERROR + "Nur der Polizeipräsident oder das Justizministerium können Spieler aus dem Gefängnis entlassen.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/unarrest [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Jail.isInJail(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht im Gefängnis.");
            return true;
        }

        Jail.unarrest(tg);
        p.sendMessage(Jail.PREFIX + "Du hast §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");
        Beruf.Berufe.POLICE.sendMessage(Jail.PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");
        Beruf.Berufe.GOVERNMENT.sendMessage(Jail.PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7aus dem Gefängnis entlassen.");

        return false;
    }
}
