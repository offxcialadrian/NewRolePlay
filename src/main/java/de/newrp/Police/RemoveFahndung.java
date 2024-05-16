package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
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

        if (!Beruf.hasBeruf(p) && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            if (!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !SDuty.isSDuty(p)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (!Beruf.hasAbteilung(p, Abteilung.Abteilungen.ABTEILUNGSLEITUNG)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removefahndung [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Fahndung.isFahnded(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht gefahndet.");
            return true;
        }

        Fahndung.removeFahndung(tg);
        p.sendMessage(Fahndung.PREFIX + "Du hast die Fahndung von §6" + Script.getName(tg) + " §7entfernt.");
        Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + "Die Fahndung von §6" + Script.getName(tg) + " §7wurde von §6" + Script.getName(p) + " §7entfernt.");
        Beruf.Berufe.GOVERNMENT.sendMessage(Fahndung.PREFIX + "Die Fahndung von §6" + Script.getName(tg) + " §7wurde von §6" + Script.getName(p) + " §7entfernt.");
        tg.sendMessage(Fahndung.PREFIX + "Deine Fahndung wurde von §6" + Script.getName(p) + " §7entfernt.");
        Script.updateFahndungSubtitle(tg);

        return false;
    }
}
