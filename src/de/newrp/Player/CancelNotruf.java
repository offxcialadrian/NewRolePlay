package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.Berufe.AcceptNotruf;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelNotruf implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/cancelnotruf");
            return true;
        }

        if(!Notruf.call.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Notruf abgesetzt.");
            return true;
        }

        for(Player all : AcceptNotruf.accept.keySet()) {
            Beruf.getBeruf(all).sendMessage(Notruf.PREFIX + "Der Notruf von " + p.getName() + " wurde abgebrochen.");
            AcceptNotruf.accept.remove(all);
            Route.invalidate(all);
        }

        for(Beruf.Berufe beruf : Notruf.call2.get(p)) {
            beruf.sendMessage(Notruf.PREFIX + "Der Notruf von " + p.getName() + " wurde abgebrochen.");
        }

        Notruf.call.remove(p);
        Notruf.call2.remove(p);
        p.sendMessage(Notruf.PREFIX + "Du hast deinen Notruf abgebrochen.");

        return false;
    }
}
