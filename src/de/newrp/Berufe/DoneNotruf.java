package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Player.Notruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneNotruf implements CommandExecutor {

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
            p.sendMessage(Messages.ERROR + "/donenotruf");
            return true;
        }

        if(!AcceptNotruf.accept.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Notruf angenommen.");
            return true;
        }

        Player tg = AcceptNotruf.accept.get(p);
        for(Player player : AcceptNotruf.accept.keySet()) {
            Beruf.Berufe beruf = Beruf.getBeruf(player);
            beruf.sendMessage(Notruf.PREFIX + "Der Notruf von §6" + Script.getName(tg) + " §7wurde von §6" + Script.getName(p) + " §7als erledigt markiert.");
        }

        Notruf.call2.remove(tg);
        Notruf.call.remove(tg);

        for(Player all : AcceptNotruf.accept.keySet()) {
            if(AcceptNotruf.accept.get(all) == tg) {
                AcceptNotruf.accept.remove(all);
                Route.invalidate(all);
            }
        }

        return false;
    }
}
