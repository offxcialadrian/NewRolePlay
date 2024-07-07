package de.newrp.Chat;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.ChangeNameCommand;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.Punish;
import de.newrp.Player.Passwort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Whisper implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length < 1) {
            p.sendMessage(Messages.ERROR + "/flüstern [Nachricht]");
            return true;
        }
        String message = String.join(" ", args);

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return true;
        }

        if (Passwort.isLocked(p)) {
            p.sendMessage(Messages.ERROR + "Du musst dein Passwort eingeben!");
            return true;
        }

        if(ChangeNameCommand.isLocked(p)) {
            p.sendMessage(Messages.ERROR + "Du musst dein Name ändern, um den Chat wieder benutzen zu können!");
            return true;
        }

        for(String arg : args) {
            if(arg.startsWith("http://") || arg.startsWith("https://") || arg.startsWith("www.") || arg.endsWith(".de")  || arg.endsWith(".eu") || arg.startsWith("germanrp") || arg.startsWith("grp") || arg.startsWith("unicacity") || arg.startsWith("turniptales") || arg.toLowerCase().startsWith("turnip")) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Fremdwerbung bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + message);
                if(Script.getNRPTeam().isEmpty()) {
                    p.sendMessage(AntiCheatSystem.PREFIX + "Es liegt ein Verdacht auf Fremdwerbung vor oder deine Nachricht enthält einen Link. Die Nachricht wurde nicht gesendet. Wenn du denkst, dass es sich um einen Fehler handelt, melde ihn bitte im Forum.");
                }
                return true;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return true;
            }
        }

        Chat.handleChatFilter(p, message);

        Set<String> foundNames = Chat.getMentionedNames(message);
        Location pLoc = p.getLocation();
        String speakWord = "flüstert";
        Notifications.sendMessage(Notifications.NotificationType.CHAT, "§8[§c" + p.getLevel() + "§8] §7" + Script.getName(p) + " flüstert: §7" + message);
        for (Player online : Bukkit.getOnlinePlayers()) {
            double distance = pLoc.distance(online.getLocation());
            if (distance > 8.0D) {
                continue;
            }
            online.sendMessage(Chat.constructMessage(p, message, speakWord, foundNames, distance, Chat.ChatType.WHISPER));
        }

        Log.CHAT.write(p, "[Flüstern]" +  message);
        return false;
    }
}
