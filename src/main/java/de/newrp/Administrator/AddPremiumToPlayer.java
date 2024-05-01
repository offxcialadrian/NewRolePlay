package de.newrp.Administrator;

import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPremiumToPlayer implements CommandExecutor {

    public static HashMap<String, Integer> awaitFeedback = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs,Command cmd,String s, String[] args) {
        if (!(cs instanceof ConsoleCommandSender)) return true;

        OfflinePlayer player = Script.getOfflinePlayer(args[0]);

        if(player.isOnline()) {
            Player p = player.getPlayer();
            assert p != null;
            int days = Integer.parseInt(args[1]);
            Script.addEXP(p, 50);

            if(days>=30) awaitFeedback.put(player.getName(), days);
            p.sendMessage(Premium.PREFIX + "§a§lVielen Dank für deinen Einkauf.");
            Achievement.PREMIUM.grant(player);
            Premium.addPremiumStorage(p, days);
            Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(p) + " hat " + days + " Tage Premium im Shop erworben.");
            return true;
        }

        int days = Integer.parseInt(args[1]);
        Script.addEXP(Script.getNRPID(player), 50);

        Script.addOfflineMessage(player, Premium.PREFIX + "§a§lVielen Dank für deinen Einkauf.");
        Script.addOfflineMessage(player, Messages.INFO + "Du kannst dein Premium mit /premium aktivieren.");
        Achievement.PREMIUM.grant(player);
        Premium.addPremiumStorage(player, days);
        Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, player.getName() + " hat " + days + " Tage Premium im Shop erworben.");

        return false;
    }
}
