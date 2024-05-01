package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckInvCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;


        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkinv [Spieler]");
            return true;
        }

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }


        Player tg = Bukkit.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (BuildMode.isInBuildMode(p) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.ERROR + "Du kannst im BuildMode nicht das Inventar von " + Script.getName(tg) + " überprüfen.");
            return true;
        }

        p.openInventory(tg.getInventory());
        p.sendMessage("§8[§cCheckInv§8] §c" + Messages.ARROW + " Du überprüfst das Inventar von " + Script.getName(tg));
        Script.sendTeamMessage(p, ChatColor.RED, "überprüft nun das Inventar von " + Script.getName(tg), true);
        Log.NORMAL.write(p, "überprüft das Inventar von " + Script.getName(tg));


        return false;
    }
}
