package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Player.AFK;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetAfkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        final Player player = (Player) commandSender;

        if(!Script.hasRank(player, Rank.DEVELOPER, false)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/setafk [Spieler]");
            return false;
        }

        final Player target = Script.getPlayer(args[0]);
        if(target == null) {
            player.sendMessage(Messages.ERROR + "Der Spieler " + args[0] + " ist nicht online!");
            return false;
        }

        AFK.setAFK(player, !AFK.isAFK(target));
        if(AFK.isAFK(target)) {
            target.sendMessage(AFK.PREFIX + "Du bist nun im AFK-Modus.");
            Script.sendTeamMessage(player, ChatColor.RED, "hat " + target.getName() + " in den AFK-Modus versetzt!", false);
        } else {
            target.sendMessage(AFK.PREFIX + "Du bist nun nicht mehr im AFK-Modus.");
            Script.sendTeamMessage(player, ChatColor.RED, "hat " + target.getName() + " aus den AFK-Modus gesetzt!", false);
        }

        return false;
    }
}
