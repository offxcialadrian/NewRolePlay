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

public class TransferTicket implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {

        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.DEVELOPER, false)) {
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
        Player reporter = t.getTicketer();
        t.setSupporter(tg);
        p.sendMessage(TicketCommand.PREFIX + "Du hast " + Script.getName(tg) + " das Ticket übergeben.");
        tg.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket von " + Script.getName(p) + " mit " + Script.getName(reporter) + " übernommen.");
        reporter.sendMessage(TicketCommand.PREFIX + "Dein Ticket wurde an " + Messages.RANK_PREFIX(tg) + " übergeben.");
        TicketCommand.removeFromConversation(t.getID(), p);
        TicketCommand.addToConversation(t.getID(), tg);
        if(TicketCommand.getGreeting(p) != null) {
            tg.sendMessage("§d§lTICKET §8× §d" + Script.getName(tg) + ": " + TicketCommand.getGreeting(tg).replace("{name}", Script.getName(reporter)));
            if(p != reporter) reporter.sendMessage("§d§lTICKET §8× §d" + Script.getName(tg) + ": " + TicketCommand.getGreeting(tg).replace("{name}", Script.getName(reporter)));
        }
        SDuty.updateScoreboard();
        Script.updateListname(p);
        Script.updateListname(tg);
        Script.updateListname(reporter);


        return false;
    }
}
