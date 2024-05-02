package de.newrp.Ticket;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Player.AFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddToConv implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(!TicketCommand.isInTicket(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Ticket.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/transferticket [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(AFK.isAFK(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
            return true;
        }

        if(TicketCommand.isInTicket(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist bereits in einem Ticket.");
            return true;
        }

        Ticket t = TicketCommand.getTicket(p);
        p.sendMessage(TicketCommand.PREFIX + "Du hast " + Script.getName(tg) + " zum Ticket hinzugefügt.");
        tg.sendMessage(TicketCommand.PREFIX + Messages.RANK_PREFIX(p) + " hat dich zu einem Ticket mit " + t.getTicketer().getName() + " hinzugefügt.");
        TicketCommand.addToConversation(t.getID(), tg);

        return false;
    }
}
