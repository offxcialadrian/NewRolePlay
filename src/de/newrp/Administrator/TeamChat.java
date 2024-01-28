package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (Team.getTeam(p) == null) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Team.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/team [Nachricht]");
            return true;
        }

        if(Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemuted.");
            return true;
        }

        String msg = String.join(" ", args);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if(Team.getTeam(all) == null) continue;
            if (Team.getTeam(all) == Team.getTeam(p))
                all.sendMessage("§8[§e" + Team.getTeam(p).getName() + "§8] §e" + Script.getName(p) + (Team.isTeamLeader(p)?" (TL)":"") + " §8» §7" + msg);
        }

        return false;
    }
}
