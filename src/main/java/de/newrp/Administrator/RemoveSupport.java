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

public class RemoveSupport implements CommandExecutor {

    private static final String PREFIX = "§8[§bSupport§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removesupport [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);

        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.hasRank(tg, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist kein Supporter.");
            return true;
        }

        remove(p, tg);

        return false;
    }

    private static void remove(Player p, OfflinePlayer tg) {
        p.sendMessage(PREFIX + "Du hast " + tg.getName() + " aus dem Support-Team entfernt.");
        Log.HIGH.write(p, "hat " + tg.getName() + " aus dem Support-Team entfernt.");
        Script.executeUpdate("DELETE FROM ranks WHERE nrp_id=" + Script.getNRPID(tg));
        Script.executeAsyncUpdate("DELETE FROM ticket_greeting WHERE nrp_id=" + Script.getNRPID(tg));
        Script.executeAsyncUpdate("DELETE FROM ticket_farewell WHERE nrp_id=" + Script.getNRPID(tg));
        Script.executeAsyncUpdate("DELETE from notifications WHERE nrp_id=" + Script.getNRPID(tg));

        Forum.syncPermission(tg);
        TeamSpeak.sync(Script.getNRPID(tg));

        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + "Du wurdest aus dem Support-Team entfernt.");
            tg.getPlayer().sendMessage(Messages.INFO + "Vielen Dank für deine Unterstützung!");
            SDuty.removeSDuty(tg.getPlayer());
        }
    }
}
