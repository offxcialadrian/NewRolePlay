package de.newrp.Ticket;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Player.AFK;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class AcceptTicket implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (TicketCommand.isInTicket(p)) {
            p.sendMessage(Messages.ERROR + "Du bist bereits in einem Ticket.");
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        int ticket = 0;
        if (args.length == 1) {
            if (Script.isInt(args[0])) {
                ticket = Integer.parseInt(args[0]);
            }
        }

        Ticket.Queue q = null;
        Iterator<Map.Entry<Integer, Ticket.Queue>> it = TicketCommand.queue.entrySet().iterator();

        if (ticket < 0) ticket = 0;

        if (ticket == 0) {
            while (it.hasNext()) {
                Map.Entry<Integer, Ticket.Queue> ent = it.next();
                q = ent.getValue();
                it.remove();
                break;
            }
        } else {
            for (int i = 0; i < ticket - 1; i++) {
                if (it.hasNext()) it.next();
            }

            q = it.hasNext() ? it.next().getValue() : null;
        }
        if (q != null) {
            Player tg = q.getReporter();
            if (tg != null) {
                TicketTopic tt = q.getTicketTopic();
                SDuty.updateScoreboard();
                TicketClick.created.remove(tg);
                int seconds = (int) ((System.currentTimeMillis() - q.getCreateTime()) / 1000);
                p.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket von " + Script.getName(tg) + " §8[§6Level " + tg.getLevel() + "§8]§b angenommen! Thema: " + tt.getName());
                tg.sendMessage(TicketCommand.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat dein Ticket angenommen! Thema: " + tt.getName());
                Script.sendTeamMessage(p, ChatColor.AQUA, "hat das Ticket von " + Script.getName(tg) + " angenommen! Thema: " + tt.getName(), true);
                Script.sendTeamMessage(Messages.INFO + "Angenommen nach " + seconds + " Sekunden.");
                if(TicketCommand.getGreeting(p) != null) {
                    p.sendMessage("§b§lTICKET §8× §b" + Script.getName(p) + ": " + TicketCommand.getGreeting(p));
                    if(p != tg) tg.sendMessage("§b§lTICKET §8× §b" + Script.getName(p) + ": " + TicketCommand.getGreeting(p));
                }
                tg.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                TicketCommand.sendTicketTitle(tg);
                TicketCommand.create(tg, p, q);
                SDuty.updateScoreboard();
                Script.updateListname(p);
                Script.updateListname(tg);

                Ticket t = TicketCommand.getTicket(tg);
                if(TicketCommand.getGreeting(p) != null)
                    Script.executeAsyncUpdate("INSERT INTO ticket_conv (ticketID, sender, message, time) VALUES(" + t.getID() + ", " + Script.getNRPID(p) + ", '" + TicketCommand.getGreeting(p) + "', " + System.currentTimeMillis() + ")");

                if (AFK.isAFK(tg))
                    p.sendMessage(Messages.INFO + "Der Spieler ist seit " + AFK.getAFKTime(tg) + " AFK.");
            } else {
                Ticket.Queue.clear(q);
                if (ticket == 0) {
                    p.sendMessage(Messages.ERROR + "Es ist kein Ticket offen.");
                } else {
                    p.sendMessage(Messages.ERROR + "Es ist kein Ticket mit der ID " + ticket + " offen.");
                }
            }
        } else {
            if (ticket == 0) {
                p.sendMessage(Messages.ERROR + "Es ist kein Ticket offen.");
            } else {
                p.sendMessage(Messages.ERROR + "Es ist kein Ticket mit der ID " + ticket + " offen.");
            }
        }
        return true;
    }
}
