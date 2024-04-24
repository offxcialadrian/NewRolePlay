package de.newrp.API;

import de.newrp.Administrator.Notifications;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HouseSlot implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(cs instanceof ConsoleCommandSender)) return true;

        OfflinePlayer player = Script.getOfflinePlayer(args[0]);

        if(player.isOnline()) {
            Player p = player.getPlayer();
            assert p != null;
            p.sendMessage(Messages.INFO + "Du hast ein Haus-Slot-Token erhalten.");
            p.sendMessage(Messages.INFO + "Vielen Dank für deinen Einkauf.");
            Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(p) + " hat einen HausSlot-Token erworben.");
            Script.addEXP(p, 50);
            SlotLimit.HOUSE.add(Script.getNRPID(p));
            return true;
        }

        Script.addEXP(Script.getNRPID(player), 50);
        Script.addOfflineMessage(player, Messages.INFO + "Du hast ein Haus-Slot-Token erhalten.");
        SlotLimit.HOUSE.add(Script.getNRPID(player));

        return false;
    }
}
