package de.newrp.Berufe;

import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RequeueNotruf implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && !Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!AcceptNotruf.accept.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Notruf angenommen.");
            return true;
        }

        AcceptNotruf.reOpenNotruf(p);

        return false;
    }
}
