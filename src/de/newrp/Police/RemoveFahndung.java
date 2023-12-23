package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveFahndung implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT) && !Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getAbteilung(p) != Abteilung.Abteilungen.JUSTIZMINISTERIUM && Beruf.getAbteilung(p) != Abteilung.Abteilungen.STREIFENDIENST && !Beruf.isLeader(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removefahndung [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Fahndung.isFahnded(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht gefahndet.");
            return true;
        }

        Fahndung.removeFahndung(tg);
        p.sendMessage(Fahndung.PREFIX + "Du hast die Fahndung von §6" + Script.getName(tg) + " §7entfernt.");
        Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + "Die Fahndung von §6" + Script.getName(tg) + " §7wurde von §6" + Script.getName(p) + " §7entfernt.");
        Beruf.Berufe.GOVERNMENT.sendMessage(Fahndung.PREFIX + "Die Fahndung von §6" + Script.getName(tg) + " §7wurde von §6" + Script.getName(p) + " §7entfernt.");
        tg.sendMessage(Fahndung.PREFIX + "Deine Fahndung wurde von §6" + Script.getName(p) + " §7entfernt.");

        return false;
    }
}
