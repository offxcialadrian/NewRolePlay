package de.newrp.Ticket;

import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class TicketListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Ticket t = TicketCommand.getTicket(p);
        if (t == null) return;
        TicketCommand.close(t);
        t.getSupporter().sendMessage(TicketCommand.PREFIX + "Der Spieler hat das Ticket verlassen (Quit)!");
        Script.updateListname(t.getSupporter());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (TicketCommand.getTicket(p) != null) {
            Ticket t = TicketCommand.getTicket(p);
            assert t != null;
            ArrayList<Player> conv = TicketCommand.getConversation(t);
            if (conv.size() < 2 && !Script.isInTestMode()) {
                TicketCommand.close(t);
                p.sendMessage(TicketCommand.PREFIX + "Der Spieler hat das Ticket verlassen (Quit)!");
                Script.updateListname(t.getSupporter());
                return;
            }

            for (Player players : conv) {
                if(Script.hasRank(players, Rank.SUPPORTER, false)) {
                    if(players != p)
                        players.sendMessage("§d§lTICKET §8× §d" + Script.getName(p) + " hat einen Befehl ausgeführt§8: §6" + e.getMessage());
                }
            }
        }
    }

}
