package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Forum.Forum;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoteSupport implements CommandExecutor {

    private static final String PREFIX = "§8[§bSupport§8] §7";

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

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/demotesupport [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getPlayer(args[0]);

        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Script.getRank(tg) == Rank.SUPPORTER) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits Supporter.");
            return true;
        }

        demote(p, tg);

        return false;
    }

    private static void demote(Player p, OfflinePlayer tg) {
        Rank rank = Rank.values()[Script.getRank(tg).ordinal() - 1];
        p.sendMessage(PREFIX + "Du hast " + tg.getName() + " zu " + rank.getName() + " degradiert.");
        if(tg.isOnline())
            tg.getPlayer().sendMessage(PREFIX + "Du wurdest zu " + rank.getName() + " degradiert.");
        Log.HIGH.write(p, "hat " + tg.getName() + " zu " + rank.getName() + " degradiert.");
        Log.WARNING.write(tg, "wurde von " + Script.getName(p) + " zu " + rank.getName() + " degradiert.");
        Script.executeUpdate("UPDATE ranks SET rank_id=" + rank.getID() + " WHERE nrp_id=" + Script.getNRPID(tg));

        Forum.syncPermission(tg);
        TeamSpeak.sync(Script.getNRPID(tg));

    }

}
