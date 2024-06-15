package de.newrp.API;

import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResetActivityCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            int id = 0;

            if (Beruf.hasBeruf(player)) {
                if (!Beruf.isLeader(player, true)) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                id = Beruf.getBeruf(player).getID();
            }

            if (Organisation.hasOrganisation(player)) {
                if (!Organisation.isLeader(player, true)) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                id = -Organisation.getOrganisation(player).getID();
            }

            long time = System.currentTimeMillis();
            if (args.length > 0) {
                try {
                    time = Long.parseLong(args[0]);
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Ungültiger Unix-Zeitstempel.");
                    return true;
                }
            }

            Activity.setResetDate(id, time);
            if (Beruf.hasBeruf(player)) {
                Beruf.getBeruf(player).sendMessage(Activity.PREFIX + player.getName() + " §7hat den Aktivitätszeitraum aktualisiert.");
            } else if (Organisation.hasOrganisation(player)) {
                Organisation.getOrganisation(player).sendMessage(Activity.PREFIX + player.getName() + " §7hat den Aktivitätszeitraum aktualisiert.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arg = List.of(String.valueOf(System.currentTimeMillis()));
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String string : arg) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        }
        return completions;
    }
}
