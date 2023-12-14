package de.newrp.Ticket;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Map;

public class Tickets implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        int i = 1;
        for (Map.Entry<Integer, Ticket.Queue> ent : TicketCommand.queue.entrySet()) {
            Ticket.Queue q = ent.getValue();

            String d = new SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(q.getCreateTime());
            p.sendMessage("§6§l#" + i + " §r§7|§6 " + q.getReporter().getName() + " §7|§6 Thema§7:§6 " + q.getTicketTopic().getName() + " §7|§6 " + d);
            i++;
        }

        if (i == 1) p.sendMessage(TicketCommand.PREFIX + "Es sind keine Tickets offen.");
        return true;
    }
}
