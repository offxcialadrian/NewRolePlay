package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import de.newrp.Ticket.TicketCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerTeam implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/team");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            List<Player> nrps = Script.getSortedNRPTeam();
            nrps.removeIf(nrp -> !SDuty.isSDuty(nrp));
            nrps.removeIf(TicketCommand::isInTicket);
            nrps.removeIf(nrp -> Script.getRank(nrp) == Rank.DEVELOPER);

            if (nrps.isEmpty()) {
                p.sendMessage(Script.PREFIX + "Es ist derzeit kein Teammitglied im Supporter-Dienst.");
                return;
            }

            p.sendMessage(Script.PREFIX + "Folgende Teammitglieder sind derzeit verfügbar:");
            for (Player nrp : nrps) {
                String color = "§7";
                switch (Script.getRank(nrp)) {
                    case OWNER:
                        color = "§4";
                        break;
                    case ADMINISTRATOR:
                        color = "§c";
                        break;
                    case FRAKTIONSMANAGER:
                        color = "§6";
                        break;
                    case MODERATOR:
                        color = "§9";
                        break;
                    case SUPPORTER:
                        color = "§e";
                        break;
                }
                if (Spectate.isSpectating(nrp)) continue;
                p.sendMessage("§8" + Messages.ARROW + " §6" + Script.getName(nrp) + " §8× " + color + Script.getRank(nrp).getName(nrp) + (AFK.isAFK(nrp) ? " §8× §6AFK" : ""));
            }
        });

        return false;
    }
}
