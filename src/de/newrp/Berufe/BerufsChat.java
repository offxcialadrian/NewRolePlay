package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BerufsChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/b [Nachricht]");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }

        String nachricht = sb.toString();
        String prefix = "§9" + Beruf.getBeruf(p).getName() + " " + Messages.ARROW + " ";
        for (Player all : Beruf.getPlayersFromBeruf(Beruf.getBeruf(p))) {
            all.sendMessage(prefix + Script.getName(p) + "§8: §9" + nachricht);
        }

        return false;
    }
}
