package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ActivityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            OfflinePlayer target = player;
            if (args.length > 0) {
                if (SDuty.isSDuty(player)) {
                    try {
                        target = Script.getOfflinePlayer(args[0]);
                    } catch (Exception e) {
                        player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                        return true;
                    }
                } else if (Beruf.hasBeruf(player)) {
                    if (Beruf.getAbteilung(player, true).isLeader()) {
                        try {
                            target = Script.getOfflinePlayer(args[0]);
                        } catch (Exception e) {
                            player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                            return true;
                        }

                        if (!Beruf.hasBeruf(target) || Beruf.getBeruf(player) != Beruf.getBeruf(target)) {
                            player.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
                            return true;
                        }
                    }
                } else if (Organisation.hasOrganisation(player)) {
                    if (Organisation.getRank(player) >= 4) {
                        try {
                            target = Script.getOfflinePlayer(args[0]);
                        } catch (Exception e) {
                            player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                            return true;
                        }

                        if (!Organisation.hasOrganisation(target) || Organisation.getOrganisation(player) != Organisation.getOrganisation(target)) {
                            player.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deiner Organisation.");
                            return true;
                        }
                    }
                }
            }

            int id = Beruf.hasBeruf(target) ? Beruf.getBeruf(target).getID() : -Organisation.getOrganisation(target).getID();

            List<Activity> activities = Activity.getActivities(Script.getNRPID(target), Activity.getResetDate(id));
            if (activities != null) {
                if (!activities.isEmpty()) {
                    player.sendMessage(" §8======= §3§lAktivitäten §8======= ");
                    HashMap<String, Integer> activitymap = new HashMap<>();
                    HashMap<String, Float> points = new HashMap<>();
                    for (Activity activity : activities) {
                        if (Activities.isDisabled(id, activity)) continue;
                        if (!activitymap.containsKey(activity.getName())) {
                            activitymap.put(activity.getName(), 1);
                            points.put(activity.getName(), activity.getPoints());
                        } else {
                            activitymap.put(activity.getName(), activitymap.get(activity.getName()) + 1);
                            points.put(activity.getName(), points.get(activity.getName()) + activity.getPoints());
                        }
                    }

                    float p = 0;
                    for (String akti : activitymap.keySet()) {
                        p += points.get(akti);
                        player.sendMessage("     §8| §7" + activitymap.get(akti) + "x §l" + akti + "  §8(§3" + (Math.round(points.get(akti) * 100.0) / 100.0) + "∅§8)");
                    }
                    player.sendMessage(" §8------------------------------ ");
                    player.sendMessage("     §8|  §7§lGesamt:  §3§n" + (Math.round(p * 10.0) / 10.0) + "∅");
                    player.sendMessage(" §8======================= ");
                } else {
                    if (player == target) {
                        player.sendMessage(Messages.ERROR + "Du hast keine Aktivitäten.");
                    } else {
                        player.sendMessage(Messages.ERROR + "Der Spieler hat keine Aktivitäten.");
                    }
                    return true;
                }
            }
        }

        return true;
    }
}
