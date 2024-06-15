package de.newrp.API;

import de.newrp.Administrator.Notifications;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Gameboost implements CommandExecutor {

    public static LinkedHashMap<String, Boolean> await = new LinkedHashMap<>();
    private static final String PREFIX = "§8[§6Gameboost§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof ConsoleCommandSender)) return true;

        boolean isDouble = args[0].equalsIgnoreCase("double");
        OfflinePlayer player = Script.getOfflinePlayer(args[1]);

        if(NewRoleplayMain.event != null) {
            await.put(player.getName(), isDouble);
            if(player.isOnline()) {
                Player p = player.getPlayer();
                assert p != null;
                p.sendMessage(PREFIX + "§a§lDu hast ein " + (isDouble? "Double": "Triple") + "-EXP Event erworben. Es wird aktiviert, sobald das aktuelle Event beendet wurde.");
                return true;
            }
            return true;
        }

        startEvent(player.getName(), isDouble);

        if (player.isOnline()) {
            Player p = player.getPlayer();
            assert p != null;
            Script.addEXP(p, 50, true);

            p.sendMessage(PREFIX + "§a§lVielen Dank für deinen Einkauf.");
            Achievement.GOENNER.grant(player);
            Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(p) + " hat ein 30 Minütiges " + (isDouble? "Double": "Triple") + "-EXP Event erworben.");
            return true;
        }

        Script.addEXP(Script.getNRPID(player), 50);

        Script.addOfflineMessage(player, PREFIX + "§a§lVielen Dank für deinen Einkauf.");
        Achievement.GOENNER.grant(player);
        Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, player.getName() + " hat ein 30 Minütiges " + (isDouble? "Double": "Triple") + "-EXP Event erworben.");
        return true;
    }

    public static void startEvent(String playerName, boolean isDouble) {
        Bukkit.broadcastMessage("§8[§6Gameboost§8] §6" + Messages.ARROW + " §6" + playerName + " hat ein " + (isDouble? "Double": "Triple") + "-EXP Event freigeschaltet!");
        Script.startEvent((isDouble?Event.DOUBLE_XP:Event.TRIPPLE_XP), false);

        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            Script.startEvent(null, false);
            Bukkit.broadcastMessage("§8[§6Gameboost§8] §6" + Messages.ARROW + " §6Das " + (isDouble? "Double": "Triple") + "-EXP Event wurde beendet!");
            if(!await.isEmpty()) {
                String name = new ArrayList<>(await.keySet()).get(0);
                startEvent(name, await.get(name));
                await.remove(name);
            }
        }, 20*60*30);
    }
}
