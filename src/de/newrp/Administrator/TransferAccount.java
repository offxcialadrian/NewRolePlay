package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferAccount implements CommandExecutor {

    public static final String PREFIX = "§8[§aTransferAccount§8] §a" + de.newrp.API.Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 3) {
            p.sendMessage(PREFIX + "/transferaccount [Spieler 1] [Spieler 2] [BackupCode]");
            return true;
        }

        OfflinePlayer p1 = Script.getOfflinePlayer(args[0]);
        OfflinePlayer p2 = Script.getOfflinePlayer(args[1]);

        if(Script.getNRPID(p1) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Script.getNRPID(p2) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.getBackUpCode(p1).equals(args[2]) && !Script.getBackUpCode(p2).equals(args[2])) {
            p.sendMessage(PREFIX + "Der BackupCode ist falsch.");
            return true;
        }

        String UUID1 = p1.getUniqueId().toString();
        String UUID2 = p2.getUniqueId().toString();

        Script.executeAsyncUpdate("UPDATE nrp_id SET uuid = '" + UUID1 + "' WHERE nrp_id = '" + Script.getNRPID(p2) + "'");
        Script.executeAsyncUpdate("UPDATE nrp_id SET name = '" + p1.getName() + "' WHERE nrp_id = '" + Script.getNRPID(p2) + "'");
        Script.executeAsyncUpdate("UPDATE nrp_id SET uuid = '" + UUID2 + "' WHERE nrp_id = '" + Script.getNRPID(p1) + "'");
        Script.executeAsyncUpdate("UPDATE nrp_id SET name = '" + p2.getName() + "' WHERE nrp_id = '" + Script.getNRPID(p1) + "'");
        p.sendMessage(PREFIX + "Der Account von " + p1.getName() + " wurde auf " + p2.getName() + " übertragen.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Account von " + p1.getName() + " auf " + p2.getName() + " übertragen.", true);
        if(Script.getPlayer(Script.getNRPID(p1)) != null) {
            Script.getPlayer(Script.getNRPID(p1)).kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + "Account Übertragung");
        } else {
            Script.addOfflineMessage(p1, PREFIX + "Dein Account wurde auf " + p2.getName() + " übertragen.");
        }

        if(Script.getPlayer(Script.getNRPID(p2)) != null) {
            Script.getPlayer(Script.getNRPID(p2)).kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + "Account Übertragung");
        } else {
            Script.addOfflineMessage(p2, PREFIX + "Dein Account wurde auf " + p1.getName() + " übertragen.");
        }

        return false;
    }
}
