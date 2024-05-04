package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.NewRoleplayMain;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Boot implements CommandExecutor {

    public static String PREFIX = "§8[§6Boot§8] §6» §7";
    public static HashMap<String, Integer> LEVEL = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(Script.getMoney(p, PaymentType.BANK) < 10) {
            p.sendMessage(Messages.ERROR + "Eine Fahrt mit dem Boot kostet 10€.");
            return true;
        }

        if(LEVEL.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du bist bereits auf dem Boot.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 997, 63, 568, 89.792786f, 7.7781887f)) > 5 && p.getLocation().distance(new Location(Script.WORLD, 791, 63, 549, 182.14655f, 9.560561f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht am Boot.");
            return true;
        }

        LEVEL.put(p.getName(), 0);
        Me.sendMessage(p, "setzt sich ins Boot.");
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!LEVEL.containsKey(p.getName())) {
                    cancel();
                    return;
                }

                if(LEVEL.get(p.getName()) >= 15) {
                    Me.sendMessage(p, "ist am Ziel angekommen.");
                    Script.removeMoney(p, PaymentType.BANK, 10);
                    if(p.getLocation().distance(new Location(Script.WORLD, 997, 62, 568, 89.792786f, 7.7781887f)) < 5) {
                        p.teleport(new Location(Script.WORLD, 791, 63, 549, 182.14655f, 9.560561f));
                    } else {
                        p.teleport(new Location(Script.WORLD, 997, 63, 568, 89.792786f, 7.7781887f));
                    }
                    LEVEL.remove(p.getName());
                    cancel();
                    return;
                }
                LEVEL.put(p.getName(), LEVEL.get(p.getName()) + 1);
                progressBar(16, p);
            }
        }.runTaskTimer(NewRoleplayMain.getInstance(), 0, 20);

        return false;
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cBoot fahren.. §8» §a" + sb.toString());
    }

}
