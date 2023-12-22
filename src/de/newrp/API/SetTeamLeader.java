package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeamLeader implements CommandExecutor {

    private static final String PREFIX = "§8[§eTL§8] §7";

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
            p.sendMessage(Messages.ERROR + "/setteamleader [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Team.getTeam(tg) == null) {
            p.sendMessage(Messages.ERROR + " Dieser Spieler ist in keinem Team.");
            return true;
        }

        if (Team.isTeamLeader(tg)) {
            p.sendMessage(Messages.ERROR + " Dieser Spieler ist bereits Leader.");
            return true;
        }

        Team.setTeamLeader(tg, true);
        p.sendMessage(PREFIX + " Du hast " + tg.getName() + " TL-Rechte gegeben.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " TL-Rechte gegeben.", true);
        if (tg.isOnline()) {
            tg.getPlayer().sendMessage(PREFIX + " Du hast nun TeamLeiter-Rechte.");
        }
        Achievement.TEAMLEADER.grant(tg);
        Log.HIGH.write(p, "hat " + tg.getName() + " TL-Rechte gegeben.");
        Log.HIGH.write(tg, "hat TL-Rechte bekommen.");
        return true;
    }
}
