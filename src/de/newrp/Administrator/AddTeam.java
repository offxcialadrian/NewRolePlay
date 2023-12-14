package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Team;
import de.newrp.Player.Annehmen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTeam implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (Team.getTeam(p) == null) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Team.");
            return true;
        }

        if (Team.isTeamLeader(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Teamleiter.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/addteam [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (Team.getTeam(tg) != null) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits in einem Team.");
            return true;
        }

        String PREFIX = "§8[§e" + Team.getTeam(p).getName() + "§8] §e";
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " erfolgreich in dein Team eingeladen.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " in das Team " + Team.getTeam(p).getName() + " eingeladen.");
        Annehmen.offer.put(tg.getName() + ".jointeam", Team.getTeam(p).getName());
        Script.sendAcceptMessage(tg);


        return false;
    }
}
