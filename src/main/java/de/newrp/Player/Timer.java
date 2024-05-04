package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Timer implements CommandExecutor {

    public static String PREFIX = "§8[§bTimer§8] §b» §7";
    public static HashMap<String, Integer> timers = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            p.sendMessage(PREFIX + "/timer [start / stop / reset] [Dauer]");
            return true;
        }

        if(!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um den Timer zu benutzen.");
            p.sendMessage(Messages.INFO + "Du kannst Premium im Shop unter https://shop.newrp.de erwerben.");
            return true;
        }

        if(args[0].equalsIgnoreCase("start")) {
            if(args.length != 2) {
                p.sendMessage(PREFIX + "/timer start [Dauer]");
                return true;
            }

            long time;
            if(args[1].endsWith("h")) {
                time = Long.parseLong(args[1].replace("h", "")) * 60 * 60 * 1000;
            } else if(args[1].endsWith("m")) {
                time = Long.parseLong(args[1].replace("m", "")) * 60 * 1000;
            } else if(args[1].endsWith("s")) {
                time = Long.parseLong(args[1].replace("s", "")) * 1000;
            } else {
                p.sendMessage(PREFIX + "/timer start [Dauer]");
                return true;
            }

            p.sendMessage(PREFIX + "Timer gestartet.");

            final int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                p.sendMessage(PREFIX + "Timer abgelaufen.");
                for(int i = 0; i < 10; i++) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
                timers.remove(p.getName());
            }, time / 50);

            timers.put(p.getName(), taskId);
            return true;

        } else if(args[0].equalsIgnoreCase("stop")) {
            if(args.length != 1) {
                p.sendMessage(PREFIX + "/timer stop");
                return true;
            }

            if(!timers.containsKey(p.getName())) {
                p.sendMessage(PREFIX + "Du hast keinen Timer laufen.");
                return true;
            }

            int taskId = timers.get(p.getName());
            try {
                Bukkit.getScheduler().cancelTask(taskId);
                p.sendMessage(PREFIX + "Timer gestoppt.");
                timers.remove(p.getName());
            } catch (Exception e) {
                p.sendMessage(PREFIX + "Timer konnte nicht gestoppt werden.");
            }

        }

        return false;
    }
}
