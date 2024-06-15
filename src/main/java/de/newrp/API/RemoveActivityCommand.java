package de.newrp.API;

import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RemoveActivityCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Beruf.hasBeruf(player)) {
                if (!Beruf.getAbteilung(player).isLeader()) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
            } else if (Organisation.hasOrganisation(player)) {
                if (Organisation.getRank(player) < 4 && !Organisation.isLeader(player, true)) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (args.length > 1) {
                String activity = args[1];
                if (args[0].equalsIgnoreCase("all")) {
                    List<UUID> targets = new ArrayList<>();

                    if (Beruf.hasBeruf(player)) {
                        targets = Beruf.getBeruf(player).getMember();
                    } else if (Organisation.hasOrganisation(player)) {
                        targets = Organisation.getOrganisation(player).getMember();
                    }
                    for (UUID target : targets) {
                        Player tg = Bukkit.getPlayer(target);
                        if (tg != null) if (!AFK.isAFK(target)) Activity.deleteActivity(Script.getNRPID(tg), Script.getNRPID(player), activity);
                    }
                } else {
                    try {
                        Bukkit.getOfflinePlayer(args[0]);
                    } catch (Exception e) {
                        player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                        return true;
                    }

                    OfflinePlayer target = Script.getOfflinePlayer(args[0]);
                    int id;
                    if (Beruf.hasBeruf(player) && Beruf.hasBeruf(target) && Beruf.getBeruf(player) == Beruf.getBeruf(target)) {
                        id = Beruf.getBeruf(player).getID();
                    } else if (Beruf.hasBeruf(player)) {
                        player.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
                        return true;
                    } else if (Organisation.hasOrganisation(player) && Organisation.hasOrganisation(target) && Organisation.getOrganisation(player) == Organisation.getOrganisation(target)) {
                        id = -Organisation.getOrganisation(player).getID();
                    } else {
                        player.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deiner Organisation.");
                        return true;
                    }

                    if (id == 0) {
                        player.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }

                    Activity.deleteActivity(Script.getNRPID(target), Script.getNRPID(player), activity);
                }
            } else {
                player.sendMessage(Messages.ERROR + "/addactivity [name] [activity]");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return new ArrayList<>();
        if (!Beruf.hasBeruf((Player) sender) && !Organisation.hasOrganisation((Player) sender)) return new ArrayList<>();
        List<String> args1 = new ArrayList<>();
        args1.add("all");
        List<String> args2 = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (Beruf.hasBeruf((Player) sender)) for (UUID m : Beruf.getBeruf((Player) sender).getMember()) args1.add(Bukkit.getOfflinePlayer(m).getName());
            if (Organisation.hasOrganisation((Player) sender)) for (UUID m : Organisation.getOrganisation((Player) sender).getMember()) args1.add(Bukkit.getOfflinePlayer(m).getName());
            for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        } else if (args.length == 2) {
            int id = 0;
            if (Beruf.hasBeruf((Player) sender)) {
                id = Beruf.getBeruf((Player) sender).getID();
            } else if (Organisation.hasOrganisation((Player) sender)) {
                id = -Organisation.getOrganisation((Player) sender).getID();
            }
            if (id == 0) return new ArrayList<>();
            args2.addAll(Activities.getCompletions(id));
            for (String string : args2) if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
        }
        return completions;
    }
}
