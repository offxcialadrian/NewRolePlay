package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AimBot implements CommandExecutor {

    public static String PREFIX = "§8[§cAimBot§8] §c" + Messages.ARROW + " §7";
    public static ArrayList<Player> aimbot = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.OWNER, false)) {
            return true;
        }

        if(aimbot.contains(p)) {
            aimbot.remove(p);
            p.sendMessage(PREFIX + "Du hast den AimBot-Modus §cdeaktiviert§7.");
            return true;
        }

        aimbot.add(p);
        p.sendMessage(PREFIX + "Du hast den AimBot-Modus §aaktiviert§7.");

        return false;
    }
}
