package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RegisterPlayer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/registerplayer [Name] [UUID]");
            return true;
        }

        String name = args[0];
        String uuid = args[1];

        if(!Script.isUUID(uuid)) {
            p.sendMessage(Messages.ERROR + "Die UUID ist ungültig.");
            return true;
        }

        UUID uuids = UUID.fromString(uuid);

        if(Script.getNRPID(name) != 0) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits registriert.");
            return true;
        }

        Script.registerPlayer(name, uuids);
        p.sendMessage(Script.PREFIX + "Der Spieler wurde erfolgreich registriert.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Spieler " + name + " registriert.", true);

        return false;
    }
}
