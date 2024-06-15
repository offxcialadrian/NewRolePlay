package de.newrp.Berufe;

import de.newrp.API.FrakChatColor;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class AbteilungsChat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String arg : args) message.append(arg).append(" ");

            if (Beruf.hasBeruf(player)) {
                Beruf.Berufe beruf = Beruf.getBeruf(player);
                Abteilung.Abteilungen abteilung = Beruf.getAbteilung(player);
                if (abteilung.getID() == 0) {
                    player.sendMessage(Messages.ERROR + "Du bist in keiner Abteilung.");
                    return true;
                }

                if (args.length == 0) {
                    player.sendMessage(Messages.ERROR + "/abteilungschat [Nachricht]");
                    return true;
                }

                for (UUID p : beruf.getMember()) {
                    if (Bukkit.getPlayer(p) != null) {
                        Player target = Bukkit.getPlayer(p);
                        Abteilung.Abteilungen abteilungen = Beruf.getAbteilung(target);
                        if (abteilungen == abteilung || (abteilung.isLeader() && abteilungen.isLeader())) {
                            Objects.requireNonNull(target).sendMessage("§" + FrakChatColor.getNameColor(beruf) + "§o" + Beruf.getAbteilung(player).getName() + " " + Script.getName(player) + "§8: §" + FrakChatColor.getTextColor(beruf) + message);
                        }
                    }
                }
            }

            if (Organisation.hasOrganisation(player)) {
                Organisation orga = Organisation.getOrganisation(player);

                if (Organisation.getRank(player) < 4 && !orga.isLeader(Script.getNRPID(player), true)) {
                    player.sendMessage(Messages.ERROR + "Du bist nicht im Leaderteam.");
                    return true;
                }

                for (UUID p : orga.getMember()) {
                    if (Bukkit.getPlayer(p) != null) {
                        Player target = Bukkit.getPlayer(p);
                        if (Organisation.getRank(target) >= 4 || orga.isLeader(Script.getNRPID(target), true)) {
                            Objects.requireNonNull(target).sendMessage("§" + FrakChatColor.getNameColor(orga) + "§o" + Organisation.getRankName(player) + " " + Script.getName(player) + "§8: §" + FrakChatColor.getTextColor(orga) + message);
                        }
                    }
                }
            }
        }

        return true;
    }
}
