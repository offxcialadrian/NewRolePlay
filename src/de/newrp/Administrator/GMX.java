package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMX implements CommandExecutor {

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

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/gmx");
            return true;
        }

        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 10 Sekunden neu!");
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet jetzt neu!");
            Bukkit.getServer().shutdown();
        }, 20 * 10);

        return false;
    }
}
