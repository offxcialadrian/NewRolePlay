package de.newrp.Gangwar;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Capture implements CommandExecutor, Listener {

    public static HashMap<String, Integer> progress = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/capture");
            return true;
        }

        GangwarZones zone = GangwarZones.getZoneByLocation(p.getLocation());
        if(zone == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in einer Gangwar-Zone.");
            return true;
        }

        Location[] locs = zone.getCapturePoints();
        for (Location loc : locs) {
            if (p.getLocation().distance(loc) <= 3) {
                if(GangwarCommand.cooldown.containsKey(loc) && GangwarCommand.cooldown.get(loc) > System.currentTimeMillis()) {
                    p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(GangwarCommand.cooldown.get(loc)) + " warten.");
                    return true;
                }

                for(Location l : GangwarCommand.captures.get(Organisation.getOrganisation(p))) {
                    if(l.equals(loc)) {
                        p.sendMessage(Messages.ERROR + "Du hast diesen Punkt bereits eingenommen.");
                        return true;
                    }
                }

                progress.put(p.getName(), 0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (p.getLocation().distance(loc) > 3) {
                            p.sendMessage(Messages.ERROR + "Du hast den Punkt verlassen.");
                            cancel();
                            return;
                        }

                        if (progress.get(p.getName()) >= 30) {

                            ArrayList<Location> o_locs = GangwarCommand.captures.get(GangwarCommand.getOpponent(Organisation.getOrganisation(p)));
                            o_locs.remove(loc);
                            GangwarCommand.captures.replace(GangwarCommand.getOpponent(Organisation.getOrganisation(p)), o_locs);

                            ArrayList<Location> locs = GangwarCommand.captures.get(Organisation.getOrganisation(p));
                            locs.add(loc);
                            GangwarCommand.captures.replace(Organisation.getOrganisation(p), locs);
                            GangwarCommand.cooldown.put(loc, System.currentTimeMillis() + 60000);
                            p.sendMessage(GangwarCommand.PREFIX + "Du hast den Punkt eingenommen.");
                            for(Organisation org : GangwarCommand.gangwar.get(zone)) {
                                org.sendMessage(GangwarCommand.PREFIX + Organisation.getOrganisation(p).getName() + " hat einen Capture-Punkt eingenommen.");
                            }
                            progress.remove(p.getName());
                            cancel();
                        } else {
                            progressBar(30, p);
                            progress.replace(p.getName(), progress.get(p.getName()) + 1);
                        }
                    }
                }.runTaskTimer(main.getInstance(), 0, 20);

            }
        }

        return false;
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = progress.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cPunkt einnehmen.. §8» §a" + sb.toString());
    }
}
