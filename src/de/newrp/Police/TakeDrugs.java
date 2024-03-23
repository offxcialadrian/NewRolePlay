package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeDrugs implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.POLICE) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/takedrugs [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.hasDrugs(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keine Drogen dabei.");
            return true;
        }

        Stadtkasse.addStadtkasse(2*Script.getDrugAmount(tg), "Drogenkonfiszierung", null);
        Script.removeDrugs(tg);
        p.sendMessage(Messages.INFO + "Du hast " + tg.getName() + " die Drogen abgenommen.");
        tg.sendMessage(Messages.INFO + "Polizist " + p.getName() + " hat dir die Drogen abgenommen");
        Beruf.Berufe.POLICE.sendMessage("§9Beamter " + Script.getName(p) + " hat " + Script.getName(tg) + " die Drogen abgenommen");
        return true;

    }
}
