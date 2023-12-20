package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.API.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Flyspeed implements CommandExecutor {

    private static final String PREFIX = "§8[§eFlyspeed§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, true) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) && !Team.isTeamLeader(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 1 && args.length != 2) {
            p.sendMessage(Messages.ERROR + "/flyspeed <Spieler> [Geschwindigkeit]");
            return true;
        }


        if (args.length == 1) {
            if (!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "Die Geschwindigkeit muss eine Zahl sein.");
                return true;
            }

            int speed = Integer.parseInt(args[0]);

            if (speed < 1 || speed > 10) {
                p.sendMessage(Messages.ERROR + "Die Geschwindigkeit muss zwischen 1 und 10 liegen.");
                return true;
            }

            p.setFlySpeed(speed / 10f);
            p.sendMessage(PREFIX + "Dein Flyspeed wurde auf " + speed + " gesetzt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat sein Flyspeed auf " + speed + " gesetzt.", true);
            return true;
        }


        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Die Geschwindigkeit muss eine Zahl sein.");
            return true;
        }

        int speed = Integer.parseInt(args[1]);

        if (speed < 1 || speed > 10) {
            p.sendMessage(Messages.ERROR + "Die Geschwindigkeit muss zwischen 1 und 10 liegen.");
            return true;
        }

        tg.setFlySpeed(speed / 10f);
        p.sendMessage(PREFIX + "Der Flyspeed von " + Script.getName(tg) + " wurde auf " + speed + " gesetzt.");
        tg.sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat dein Flyspeed auf " + speed + " gesetzt.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den Flyspeed von " + Script.getName(tg) + " auf " + speed + " gesetzt.", true);
        return true;

    }
}
