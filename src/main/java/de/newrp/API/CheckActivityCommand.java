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

import java.util.List;
import java.util.Objects;

public class CheckActivityCommand implements CommandExecutor {

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
                    if (Beruf.getAbteilung(player).isLeader()) {
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

            List<Activity> activities = Activity.getActivities(Script.getNRPID(target), Activity.getResetDate(Beruf.hasBeruf(target) ? Beruf.getBeruf(target).getID() : -Organisation.getOrganisation(target).getID()));
            if (activities != null) {
                if (!activities.isEmpty()) {
                    player.sendMessage(Activity.PREFIX + "Aktivitäten von " + target.getName() + ":");
                    for (Activity activity : activities) {
                        player.sendMessage("     §8" + Messages.ARROW + " §7§l" + activity.getName() + "§8 (§3" + activity.getPoints() + "∅§8) §8| §7§o" + Activity.formatTime(activity.getTime()) + (activity.getGiver() == 0 ? "" : " §8[" + Objects.requireNonNull(Script.getOfflinePlayer(activity.getGiver())).getName() + "]"));
                    }
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
