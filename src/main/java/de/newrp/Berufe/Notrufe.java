package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.Player.Mobile;
import de.newrp.Player.Notruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Notrufe implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/notrufe");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
            return true;
        }

        if(Notruf.call.isEmpty()) {
            p.sendMessage(Notruf.PREFIX + "Es gibt keine Notrufe.");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        p.sendMessage(Notruf.PREFIX + "Es sind " + Notruf.call.size() + " Notrufe offen.");

        for(Player all : Notruf.call2.keySet()) {
            List<Beruf.Berufe> berufe = Notruf.call2.get(all);
            if(berufe.contains(Beruf.getBeruf(p))) {
                p.sendMessage(Notruf.PREFIX + "§8× §6" + all.getName());
            }
        }

        return false;
    }
}
