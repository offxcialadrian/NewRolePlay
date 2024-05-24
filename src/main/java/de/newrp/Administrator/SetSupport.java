package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Forum.Forum;
import de.newrp.Player.Passwort;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSupport implements CommandExecutor {

    private static final String PREFIX = "§8[§c§lSupport§8] §c§l» §c§l";

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
            p.sendMessage(Messages.ERROR + "/setsupport [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);

        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Passwort.hasPasswort(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat kein Passwort!");
            return true;
        }

        switch (Script.getRank(tg)) {
            case PLAYER:
                setSupport(p, tg, Rank.SUPPORTER);
                break;
            case SUPPORTER:
                setSupport(p, tg, Rank.MODERATOR);
                break;
            case MODERATOR:
                setSupport(p, tg, Rank.FRAKTIONSMANAGER);
                break;
            case FRAKTIONSMANAGER:
                setSupport(p, tg, Rank.ADMINISTRATOR);
                break;
            case ADMINISTRATOR:
                p.sendMessage(Messages.ERROR + "Der Spieler ist bereits Administrator!");
                return true;
        }

        return false;
    }

    private static void setSupport(Player p, Player tg, Rank rank) {
        p.sendMessage(PREFIX + "Du hast " + tg.getName() + " zum " + rank.getName(tg) + " ernannt.");
        tg.sendMessage(PREFIX + "Herzlichen Glückwunsch! Du wurdest zum " + rank.getName(tg) + " ernannt");
        Bukkit.broadcastMessage(PREFIX + Script.getName(tg) + " wurde zum " + rank.getName(tg) + " ernannt.");
        Log.WARNING.write(p, "hat " + Script.getName(tg) + " zum " + rank.getName(tg) + " ernannt.");
        if (Script.isNRPTeam(tg)) {
            Script.executeUpdate("UPDATE ranks SET rank_id=" + rank.getID() + " WHERE nrp_id=" + Script.getNRPID(tg));
        } else {
            Script.executeUpdate("INSERT INTO ranks (nrp_id, rank_id, since) VALUES (" + Script.getNRPID(tg) + ", " + rank.getID() + ", NOW())");
        }
        TeamSpeak.sync(Script.getNRPID(tg));
        Forum.syncPermission(tg);
        Achievement.SERVER_TEAM.grant(tg);
    }
}
