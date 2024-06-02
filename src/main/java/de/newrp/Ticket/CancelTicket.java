package de.newrp.Ticket;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CancelTicket implements CommandExecutor, Listener {
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
            if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
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
                Script.updateListname(p);
                Script.updateListname(tg);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)");
                        tg.sendMessage(TicketCommand.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat das Ticket beendet! §7(§6#" + id + "§7)");
                        Script.sendTeamMessage(p, ChatColor.LIGHT_PURPLE, "hat das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)", true);
                        TicketCommand.close(t);
                        Script.updateListname(p);
                        Script.updateListname(tg);
                        sendRatingGUI(tg, t);
                        if(Script.isNRPTeam(p)) Script.addEXP(p, 5, true);
                    }
                }.runTaskLater(NewRoleplayMain.getInstance(), 2 * 20L);
                return true;
            }
            p.sendMessage(TicketCommand.PREFIX + "Du hast das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)");
            tg.sendMessage(TicketCommand.PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat das Ticket beendet! §7(§6#" + id + "§7)");
            Script.sendTeamMessage(p, ChatColor.LIGHT_PURPLE, "hat das Ticket mit " + Script.getName(tg) + " beendet! §7(§6#" + id + "§7)", true);
            if(Script.isNRPTeam(p)) Script.addEXP(p, 5, true);
            sendRatingGUI(tg, t);
            TicketCommand.close(t);
            Script.updateListname(p);
            Script.updateListname(tg);
            SDuty.updateScoreboard();
        } else {
            p.sendMessage(Messages.ERROR + "Du hast kein Ticket erstellt.");
        }
        return true;
    }

    public static void sendRatingGUI(Player p, Ticket t) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§dTicket bewerten [#" + t.getID() + "]");
        inv.setItem(0, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName("§dSehr gut").build());
        inv.setItem(1, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName("§aGut").build());
        inv.setItem(2, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("§eMittel").build());
        inv.setItem(3, new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setName("§6Schlecht").build());
        inv.setItem(4, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§cSehr schlecht").build());
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().contains("§dTicket bewerten")) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            int rating = 0;
            switch(e.getCurrentItem().getType()) {
                case PURPLE_STAINED_GLASS_PANE:
                    rating = 5;
                    break;
                case LIME_STAINED_GLASS_PANE:
                    rating = 4;
                    break;
                case YELLOW_STAINED_GLASS_PANE:
                    rating = 3;
                    break;
                case ORANGE_STAINED_GLASS_PANE:
                    rating = 2;
                    break;
                case RED_STAINED_GLASS_PANE:
                    rating = 1;
                    break;
            }
            int ticketID = e.getView().getTitle().contains("#") ? Integer.parseInt(e.getView().getTitle().split("#")[1].replace("]", "")) : -1;
            int supporterID = TicketCommand.getSupporterID(ticketID);
            Script.executeUpdate("INSERT INTO supporter_rating (id, ticketID, supporterID, rating, time) VALUES(NULL, " + ticketID + ", " + supporterID + ", " + rating + ", " + System.currentTimeMillis() + ")");
            p.sendMessage(TicketCommand.PREFIX + "Vielen Dank für deine Bewertung!");
            p.sendMessage(Messages.INFO + "Du hilfst uns damit, unseren Support zu verbessern.");
            if(rating < 3 && Script.getLevel(p) <= 1) {
                p.sendMessage(Script.PREFIX + "Es tut uns leid, dass du mit unserem Support nicht zufrieden warst.");
                p.sendMessage(Messages.INFO + "Bitte melde dich bei einem Administrator, damit wir das Problem klären können.");
                p.sendMessage(Messages.INFO + "Als Entschädigung erhältst du 3 Tage Premium.");
                Premium.addPremiumStorage(p, 3);
            }
            p.closeInventory();
        }
    }

}
