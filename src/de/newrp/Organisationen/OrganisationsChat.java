package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Punish;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrganisationsChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/o [Nachricht]");
            return true;
        }

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
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
        for (Player all : Organisation.getPlayersFromOrganisation(Organisation.getOrganisation(p))) {
            all.sendMessage("§b" + Organisation.getRankName(p) + " " + Script.getName(p) + "§8: §3" + nachricht);
        }

        return false;
    }

}
