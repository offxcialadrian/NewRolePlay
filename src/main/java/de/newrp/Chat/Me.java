package de.newrp.Chat;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Notifications;
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
            if(arg.equalsIgnoreCase("http://") || arg.equalsIgnoreCase("https://") || arg.equalsIgnoreCase("www.") || arg.equalsIgnoreCase(".de")  || arg.equalsIgnoreCase(".eu") || arg.equalsIgnoreCase("germanrp") || arg.equalsIgnoreCase("grp") || arg.equalsIgnoreCase("unicacity") || arg.equalsIgnoreCase("turniptales") || arg.toLowerCase().startsWith("turnip")) {
                p.sendMessage(Messages.ERROR + "Du darfst keine Links in den RolePlay-Chat senden!");
                return true;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return true;
            }

            if(arg.equalsIgnoreCase("schwanz") || arg.equalsIgnoreCase("penis") || arg.equalsIgnoreCase("vagina") ||
                    arg.equalsIgnoreCase("stöhnt") || arg.equalsIgnoreCase("pussy") || arg.equalsIgnoreCase("fickt") ||
                    arg.equalsIgnoreCase("vergewaltigt") || arg.equalsIgnoreCase("wichst") || arg.equalsIgnoreCase("wixxt") ||
                    arg.equalsIgnoreCase("steckt zwei Finger") || arg.equalsIgnoreCase("penetriert") || arg.equalsIgnoreCase("sperma") ||
                    arg.equalsIgnoreCase("stößt") || arg.equalsIgnoreCase("melken") || arg.equalsIgnoreCase("dringt ein") ||
                    arg.equalsIgnoreCase("sein glied") || arg.equalsIgnoreCase("lustpunkt") || arg.equalsIgnoreCase("eichel") ||
                    arg.equalsIgnoreCase("bläst ihm") || arg.equalsIgnoreCase("spritzt") ||
                    arg.equalsIgnoreCase("sex") || arg.equalsIgnoreCase("futterluke") || arg.equalsIgnoreCase("orgasmus")) {
                Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, "Verdacht auf Missachtung der Erotik-RolePlay-Regel bei " + Script.getName(p) + " " + Messages.ARROW + " " + message);
            }

        }

        Script.sendLocalMessage(7, p, "§a§o* " + Script.getName(p) + " " + message);
        Log.CHAT.write(p, "[ME]" +  message);
        return false;
    }

    public static void sendMessage(Player p, String msg) {
        Script.sendLocalMessage(7, p, "§a§o* " + Script.getName(p) + " " + msg);
    }
}

