package de.newrp.Commands;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Random;

public class Test implements CommandExecutor, Listener {

    private static final boolean block = true;
    public static int smarktID;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        int id;
        if (cs instanceof Player) {
            Player p = (Player) cs;

            if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (!SDuty.isSDuty(p)) {
                p.sendMessage(Messages.NO_SDUTY);
                return true;
            }

            double r = 1.99 * new Random().nextFloat() - 0.995;
            int b = (int) Math.round(((200 * Math.log(0.1 * (Script.getLevel(p) + 10))) * ((0.25 * ((Math.log(1 + r) - Math.log(1 - r))) / 2) + 1)));
            p.sendMessage(String.valueOf(r));
            p.sendMessage(String.valueOf(b));

            if (args.length == 0) {
                id = Script.getNRPID(p);
            } else {
                id = Integer.parseInt(args[0]);
            }

            if (Script.getPlayer(id) != null) {
                PayDay.setPayDayPay(Script.getPlayer(id), 59);
            }
        } else {
            id = Integer.parseInt(args[0]);
        }

        Script.executeAsyncUpdate("UPDATE payday SET time=" + 59 + " WHERE nrp_id=" + id);
        if (args.length > 1) {
            Script.executeAsyncUpdate("UPDATE level SET level=" + Integer.parseInt(args[1]) + " WHERE nrp_id=" + id);
        }

        return false;
    }
}
