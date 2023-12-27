package de.newrp.Chat;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Punish;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Me implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length < 1) {
            p.sendMessage(Messages.ERROR + "/me [Nachricht]");
            return true;
        }
        String message = String.join(" ", args);

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return true;
        }

        for(String arg : args) {
            if(arg.contains("http://") || arg.contains("https://") || arg.contains("www.") || arg.contains(".de")) {
                p.sendMessage(Messages.ERROR + "Du darfst keine Links in den RolePlay-Chat senden!");
                return true;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return true;
            }
        }

        Script.sendLocalMessage(7, p, "§a§o* " + Script.getName(p) + " " + message);
        return false;
    }

    public static void sendMessage(Player p, String msg) {
        Script.sendLocalMessage(7, p, "§a§o* " + Script.getName(p) + " " + msg);
    }
}

