package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderChat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/leaderchat [Nachricht]");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        if(Beruf.isLeader(p, false)) {
            Beruf.getBeruf(p).sendLeaderMessage("§3§l" + Beruf.getAbteilung(p).getName() + " " + Script.getName(p) + "§8: §b§l" + message.toString());
        } else {
            Organisation.getOrganisation(p).sendLeaderMessage("§3§l" + Organisation.getRankName(p) + " " + Script.getName(p) + "§8: §b§l" + message.toString());
        }

        return false;
    }
}
