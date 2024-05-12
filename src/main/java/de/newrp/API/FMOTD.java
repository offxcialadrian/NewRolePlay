package de.newrp.API;

import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FMOTD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/motd [Nachricht]");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }

        String nachricht = sb.toString().replace("&", "§");
        if(Beruf.getBeruf(p) != null) {
            Beruf.getBeruf(p).setMOTD(nachricht);
            Beruf.getBeruf(p).sendMessage(Beruf.PREFIX + Script.getName(p) + " hat die Nachricht des Tages geändert: §f" + nachricht);
        } else {
            Organisation.getOrganisation(p).setMOTD(nachricht);
            Organisation.getOrganisation(p).sendMessage(Organisation.PREFIX + Script.getName(p) + " hat die Nachricht des Tages geändert: §7" + nachricht);
        }

        return false;
    }
}
