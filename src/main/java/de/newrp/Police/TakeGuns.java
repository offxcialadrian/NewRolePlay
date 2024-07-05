package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeGuns implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
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
            p.sendMessage(Messages.ERROR + "/takeguns [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.hasWeapons(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keine Waffen dabei.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
            return true;
        }

        Script.removeWeapons(tg);
        p.sendMessage(Messages.INFO + "Du hast " + tg.getName() + " entwaffnet.");
        tg.sendMessage(Messages.INFO + "Polizist " + p.getName() + " hat dich entwaffnet.");
        Beruf.Berufe.POLICE.sendMessage("§9Beamter " + Script.getName(p) + " hat " + Script.getName(tg) + " die Waffen abgenommen.");
        return true;

    }
}
