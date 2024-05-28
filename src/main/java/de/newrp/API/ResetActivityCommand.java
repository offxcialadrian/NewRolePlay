package de.newrp.API;

import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class ResetActivityCommand implements CommandExecutor {

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
        }

        return true;
    }
}
