package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayTime implements CommandExecutor {

    private static final String PREFIX = "§8[§eDayTime§8] §4" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 1 && args.length != 2) {
            p.sendMessage(Messages.ERROR + "/daytime [Tageszeit]");
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                p.resetPlayerTime();
                p.sendMessage(PREFIX + "Du hast deine DayTime zurückgesetzt.");
            } else {
                if (!Script.isLong(args[0])) {
                    p.sendMessage(Messages.ERROR + "/daytime [Tageszeit]");
                    return true;
                }
                p.setPlayerTime(Long.parseLong(args[0]), true);
                p.sendMessage(PREFIX + "Deine DayTime wurde auf " + args[0] + " gesetzt.");
            }

        } else {
            Player tg = Script.getPlayer(args[0]);
            if (tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if (args[1].equalsIgnoreCase("reset")) {
                tg.resetPlayerTime();
                p.sendMessage(PREFIX + "Du hast die DayTime von " + Script.getName(tg) + " zurückgesetzt.");
                tg.sendMessage(PREFIX + "Deine DayTime wurde von " + Script.getName(p) + " zurückgesetzt.");
            } else {
                if (Script.isLong(args[1])) {
                    tg.setPlayerTime(Long.parseLong(args[1]), true);
                    p.sendMessage(PREFIX + "Du hast die DayTime von " + Script.getName(tg) + " auf " + args[1] + " gesetzt.");
                    tg.sendMessage(PREFIX + "Deine DayTime wurde von " + Script.getName(p) + " auf " + args[1] + " gesetzt.");
                } else {
                    p.sendMessage(Messages.ERROR + "/daytime [Spieler] [Tageszeit]");
                }
            }
        }
        return true;
    }
}
