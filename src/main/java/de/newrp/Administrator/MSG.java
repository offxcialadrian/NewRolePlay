package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MSG implements CommandExecutor {

    public static final String PREFIX = "§8[§eMSG§8] §e" + Messages.ARROW + " §7";
    public static HashMap<String, String> may_reply = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length < 2) {
            p.sendMessage(Messages.ERROR + "/msg [Spieler] [Nachricht]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String msg = sb.toString().trim();
        p.sendMessage(PREFIX + "Du §7» §e" + tg.getName() + "§7: §f" + msg);
        may_reply.put(tg.getName(), p.getName());
        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + "§e" + Script.getName(p) + " §7» §7Dir: §f" + msg);
            Script.sendSuggestMessage(tg.getPlayer(), "§9 "+ Messages.ARROW + " Klicke hier zum Antworten", "/reply ", "§7§oKlicke hier zum Antworten.");
        } else {
            Script.addOfflineMessage(tg, PREFIX + "§e" + Script.getName(p) + " §7» §7Dir: §f" + msg);
        }

        return false;
    }
}
