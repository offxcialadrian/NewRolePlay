package de.newrp.Ticket;

import de.newrp.API.Debug;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class TicketClick implements Listener {
    public static final HashMap<Player, Long> created = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§b§lTicket")) {
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                e.setCancelled(true);
                e.getView().close();
                TicketTopic topic = null;

                switch (e.getCurrentItem().getType()) {
                    case BUCKET:
                        topic = TicketTopic.BUG;
                        break;
                    case ACACIA_SIGN:
                        topic = TicketTopic.FRAGE;
                        break;
                    case SKELETON_SKULL:
                        topic = TicketTopic.SPIELER;
                        break;
                    case COMPASS:
                        topic = TicketTopic.ACCOUNT;
                        break;
                    case BOOK:
                        topic = TicketTopic.SONSTIGES;
                        break;
                    default:
                        Debug.debug("Item is null");
                        break;
                }
                if (topic != null) {
                    TicketCommand.addToQueue(p, topic);
                    TicketCommand.sendTicketMessage(p, topic);
                } else if (e.getCurrentItem().getType().equals(Material.QUARTZ_BRICKS)) {
                    e.setCancelled(true);
                    e.getView().close();
                    p.sendMessage("§8[§cF.A.Q§8]§6 In unserem FAQ sind viele Fragen bereits beantwortet.");
                    p.sendMessage("§8[§cF.A.Q§8]§6 » https://newrp.de/faq");
                } else {
                    e.setCancelled(true);
                    e.getView().close();
                }
            }
        }
    }
}
