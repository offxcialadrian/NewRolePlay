package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuDoCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§4SuDo§8] §4" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.OWNER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(Messages.ERROR + "/sudo [Spieler] [Befehl]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        StringBuilder command = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            command.append(args[i]).append(" ");
        }

        if (command.toString().startsWith("/"))
            command = new StringBuilder(command.substring(1));


        tg.performCommand(command.toString());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " den Befehl §e/" + command + "§7ausführen lassen.");
        Log.HIGH.write(p, "hat " + Script.getName(tg) + " den Befehl §e/" + command + "§7ausgeführen lassen.");
        Log.WARNING.write(tg, "wurde von " + Script.getName(p) + " den Befehl §e/" + command + "§7ausgeführen lassen.");

        return false;
    }
}
