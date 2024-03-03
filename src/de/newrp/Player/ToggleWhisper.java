package de.newrp.Player;

import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ToggleWhisper implements CommandExecutor {

    public static ArrayList<String> whisper = new ArrayList<>();
    public static String PREFIX = "§8[§cChat§8] §c" + Messages.ARROW + " §7";

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if(whisper.contains(p.getName())) {
            p.sendMessage(PREFIX + "Du hast das automatische Flüstern deaktiviert.");
            whisper.remove(p.getName());
        } else {
            p.sendMessage(PREFIX + "Du hast das automatische Flüstern aktiviert.");
            whisper.add(p.getName());
        }

        return false;
    }

}
