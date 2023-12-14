package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetHere implements CommandExecutor {

    private static final String PREFIX = "§8[§eTeleport§8] §e";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/tphere [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        tg.teleport(p.getLocation());
        p.sendMessage(PREFIX + " Du hast " + Script.getName(tg) + " zu dir teleportiert.");
        tg.sendMessage(PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat dich zu sich teleportiert.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " zu sich teleportiert.", true);

        return false;
    }

}
