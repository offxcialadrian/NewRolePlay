package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.OWNER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/setlevel [Spieler] [Level]");
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        int level = 0;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
            return true;
        }

        Script.setLevel(tg, level);
        p.sendMessage("§8[§eLevel§8] §e" + Messages.ARROW + " §7Du hast " + Script.getName(tg) + " Level auf " + level + " gesetzt.");
        tg.sendMessage("§8[§eLevel§8] §e" + Messages.ARROW + " §7Dein Level wurde von " + Script.getName(p) + " auf " + level + " gesetzt.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + "s Level auf " + level + " gesetzt.", true);

        return false;
    }
}
