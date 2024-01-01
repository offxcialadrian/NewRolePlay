package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class BlockCommand implements CommandExecutor, Listener {

    public static ArrayList<String> blocked = new ArrayList<>();
    public static String PREFIX = "§8[§cBlockCommand§8] §c» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/blockcommand [Befehl]");
            return true;
        }

        if(blocked.contains(args[0])) {
            blocked.remove(args[0]);
            p.sendMessage(PREFIX + "Der Befehl §6" + args[0] + " §7wurde §centblockt§7.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat die Sperre des Befehls §6" + args[0] + " §aaufgehoben§7.", true);
            return true;
        }

        blocked.add(args[0]);
        p.sendMessage(PREFIX + "Der Befehl §6" + args[0] + " §7wurde §aerfolgreich §7blockiert.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Befehl §6" + args[0] + " §7gesperrt.", true);

        return false;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if(blocked.contains(e.getMessage().split(" ")[0])) {
            e.setCancelled(!SDuty.isSDuty(p));
            if(SDuty.isSDuty(p)) {
                p.sendMessage(Messages.INFO + "Dieser Befehl ist derzeit gesperrt. Du konntest ihn nur ausführen, weil du im Supporter-Dienst bist.");
                return;
            }
            p.sendMessage(Messages.ERROR + "Dieser Befehl ist derzeit gesperrt.");
        }
    }
}
