package de.newrp.Chat;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.Notications;
import de.newrp.Administrator.Punish;
import de.newrp.Call.Call;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Schreien implements CommandExecutor {

    private final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length < 1) {
            p.sendMessage(Messages.ERROR + "/schreien [Nachricht]");
            return true;
        }
        String message = String.join(" ", args);

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return true;
        }

        for(String arg : args) {
            if(arg.equalsIgnoreCase("http://") || arg.equalsIgnoreCase("https://") || arg.equalsIgnoreCase("www.") || arg.equalsIgnoreCase(".de")  || arg.equalsIgnoreCase(".eu") || arg.equalsIgnoreCase("germanrp") || arg.equalsIgnoreCase("grp") || arg.equalsIgnoreCase("unicacity") || arg.equalsIgnoreCase("turniptales") || arg.toLowerCase().startsWith("turnip")) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Fremdwerbung bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + message);

                p.sendMessage(AntiCheatSystem.PREFIX + "Es liegt ein Verdacht auf Fremdwerbung vor. Die Nachricht wurde nicht gesendet. Wenn du denkst, dass es sich um einen Fehler handelt, melde ihn bitte im Forum.");
                return true;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return true;
            }
        }

        long time = System.currentTimeMillis();
        Long lastUsage = cooldowns.get(p.getName());
        if (lastUsage != null && lastUsage + 5000L > time) {
            return true;
        }
        cooldowns.put(p.getName(), time);
        if (message.charAt(message.length() - 1) != '!')
            message = message + "!";
        Set<String> foundNames = Chat.getMentionedNames(message);
        Location pLoc = p.getLocation();
        String speakWord = "schreit";

        if(Call.isOnCall(p)) {
            Call.sendMessage(p, message);
        }

        Notications.sendMessage(Notications.NotificationType.CHAT, "§8[§c" + p.getLevel() + "§8] §7" + Script.getName(p) + " schreit: §7" + message);
        for (Player online : Bukkit.getOnlinePlayers()) {
            double distance = pLoc.distance(online.getLocation());
            if (distance > 35.0D) {
                continue;
            }
            online.sendMessage(Chat.constructMessage(p, message, speakWord, foundNames, distance, Chat.ChatType.SHOUT));
        }
        return true;
    }
}
