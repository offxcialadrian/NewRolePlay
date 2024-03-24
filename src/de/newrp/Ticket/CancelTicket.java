package de.newrp.Ticket;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CancelTicket implements CommandExecutor {
    static final HashMap<Player, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (TicketCommand.isInQueue(p)) {
            TicketCommand.removeFromQueue(p);
            p.sendMessage(TicketCommand.PREFIX + "Dein Ticket wurde gelöscht.");
            for (Player supporter : Script.getNRPTeam()) {
                supporter.sendMessage(TicketCommand.PREFIX + "Das Ticket von " + Script.getName(p) + " wurde gelöscht.");
            }
            SDuty.updateScoreboard();
        } else if (TicketCommand.isInTicket(p)) {

            if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
                p.sendMessage(Messages.ERROR + "Du kannst dein Ticket nicht beenden.");
                return true;
            }

            Ticket t = TicketCommand.getTicket(p);
            if(t == null) {
                Script.sendBugReport(p, "ticket is null");
                return true;
            }
            int id = t.getID();
            Player tg = t.getTicketer();
            if(TicketCommand.getFarewell(p) != null) {
                p.sendMessage("§d§lTICKET §8× §d" + Script.getName(p) + ": " + TicketCommand.getFarewell(p));
                p.sendMessage(Messages.INFO + "Das Ticket wird in " + 2 + " Sekunden beendet.");
                if(p != tg) tg.sendMessage("§d§lTICKET §8× §d" + Script.getName(p) + ": " + TicketCommand.getFarewell(p));
                Script.executeAsyncUpdate("INSERT INTO ticket_conv (ticketID, sender, message, time) VALUES(" + t.getID() + ", " + Script.getNRPID(p) + ", '" + TicketCommand.getFarewell(p) + "', " + System.currentTimeMillis() + ")");
                p.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)");
                tg.sendMessage(TicketCommand.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat das Ticket beendet! §7(§6#" + id + "§7)");
                Script.sendTeamMessage(p, ChatColor.AQUA, "hat das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)", true);TicketCommand.close(t);
                Script.updateListname(p);
                Script.updateListname(tg);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                    }
                }.runTaskLater(main.getInstance(), 2 * 20L);
                return true;
            }
            p.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)");
            tg.sendMessage(TicketCommand.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat das Ticket beendet! §7(§6#" + id + "§7)");
            Script.sendTeamMessage(p, ChatColor.AQUA, "hat das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)", true);

            TicketCommand.close(t);

            Script.updateListname(p);
            Script.updateListname(tg);
            SDuty.updateScoreboard();
        } else {
            p.sendMessage(Messages.ERROR + "Du hast kein Ticket erstellt.");
        }
        return true;
    }
}
