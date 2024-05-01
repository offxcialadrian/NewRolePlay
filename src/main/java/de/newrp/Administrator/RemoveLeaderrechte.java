package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveLeaderrechte implements CommandExecutor {

    private static final String PREFIX = "§8[§cLeaderrechte§8] §7";

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
            p.sendMessage(Messages.ERROR + "/removeleaderrechte [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Beruf.isLeader(tg, true)) {
            p.sendMessage(Messages.ERROR + " Dieser Spieler ist kein Leader.");
            return true;
        }

        Beruf.removeLeader(tg);
        p.sendMessage(PREFIX + " Du hast " + tg.getName() + " die Leaderrechte entzogen.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " die Leaderrechte entzogen.", true);
        if (tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + " Du hast die Leaderrechte entzogen bekommen.");
        }
        Script.removeEXP(tg, Script.getRandom(10, 20));
        Log.HIGH.write(p, "hat " + tg.getName() + " die Leaderrechte entzogen.");
        Log.HIGH.write(tg, "hat die Leaderrechte entzogen bekommen.");
        TeamSpeak.sync(Script.getNRPID(tg));

        return false;
    }
}
