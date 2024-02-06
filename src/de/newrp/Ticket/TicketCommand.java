package de.newrp.Ticket;

import de.newrp.API.*;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.Notications;
import de.newrp.Administrator.SDuty;
import de.newrp.Player.AFK;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class TicketCommand implements CommandExecutor {
    public static final ArrayList<Ticket> tickets = new ArrayList<>();
    public static final LinkedHashMap<Integer, Ticket.Queue> queue = new LinkedHashMap<>();
    public static final HashMap<Integer, ArrayList<Player>> conversation = new HashMap<>();
    public static final HashMap<Integer, Integer> added_player = new HashMap<>();

    public static final String PREFIX = "§8[§bTicket§8] §b" + Messages.ARROW + " ";

    public static void openTicket(Player p) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§b§lTicket");
        inv.setItem(0, Script.setNameAndLore(Material.BUCKET, "§6Bug", "§bFehler melden"));
        inv.setItem(1, Script.setNameAndLore(Material.ACACIA_SIGN, "§6Frage", "§bAllgemeine Frage"));
        inv.setItem(2, Script.setNameAndLore(Material.SKELETON_SKULL, "§6Spieler", "§bEinen Spieler melden"));
        inv.setItem(3, Script.setNameAndLore(Material.COMPASS, "§6Account", "§bProbleme mit deinem Account auf der Plattform"));
        inv.setItem(4, Script.setNameAndLore(Material.BOOK, "§6Sonstiges", "§bMelde ein sonstiges Problem"));
        p.openInventory(inv);
    }

    public static void addToQueue(Player p, TicketTopic topic) {
        queue.put(Script.getNRPID(p), new Ticket.Queue(p, topic, System.currentTimeMillis()));
        p.sendMessage(PREFIX + "Dein Ticket wurde abgesendet und wird schnellstmöglich bearbeitet.");
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 8 || calendar.get(Calendar.HOUR_OF_DAY) > 22) {
            p.sendMessage(PREFIX + "§cBitte beachte, dass es aufgrund der Uhrzeit zu längeren Wartezeiten kommen kann.");
        }
        SDuty.updateScoreboard();
        int[] amount_time = getQueueTimeWithAmount(topic);
        int time = (amount_time[0]) / 60;

        if (time < 0) {
            p.sendMessage(PREFIX + "Es sind noch " + amount_time[1] + " Tickets vor dir.");
        } else {
            p.sendMessage(PREFIX + "Es sind noch " + amount_time[1] + " Tickets vor dir.");
        }
    }

    public static void removeFromQueue(Player p) {
        if (isInQueue(p)) {
            queue.remove(Script.getNRPID(p));
        }
    }

    public static Ticket getTicket(Player p) {
        for (Ticket t : tickets) {
            if (t.getTicketer().equals(p) || t.getSupporter().equals(p)) return t;
        }
        int id = Script.getNRPID(p);
        if (isAddedPlayer(p)) {
            int ticketID = added_player.get(id);
            for (Ticket t : tickets) {
                if (t.getID() == ticketID) return t;
            }
        }
        return null;
    }

    public static boolean isAddedPlayer(Player p) {
        return added_player.containsKey(Script.getNRPID(p));
    }

    public static void sendTicketMessage(Player sender, TicketTopic topic) {

        int i = queue.size();

        for (Player team : Script.getNRPTeam()) {
            team.playSound(team.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            Script.sendClickableMessage(team, PREFIX + "Es liegt ein neues Ticket §8[§6#" + i + "§8]§b von §6" + sender.getName() + " §bvor! Thema: §6" + topic.getName(), "/acceptticket " + i, "§6Ticket annehmen.");
        }
    }

    public static void create(Player p, Player supporter, Ticket.Queue q) {
        int userID = Script.getNRPID(p);
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM ticket ORDER BY id DESC LIMIT 1")) {
            if (rs.next()) {
                int id = rs.getInt("id") + 1;
                Ticket r = new Ticket(id, p, supporter, q.getTicketTopic(), q.getCreateTime());
                tickets.add(r);
                addToConversation(id, p);
                addToConversation(id, supporter);
                queue.remove(userID);
                Script.executeAsyncUpdate("INSERT INTO ticket (userID, supporterID, topic, created, accepted) VALUES (" + userID + ", " + Script.getNRPID(supporter) + ", '" + q.getTicketTopic().getName().toLowerCase() + "', " + q.getCreateTime() + ", " + System.currentTimeMillis() + ");");
            }
        } catch (SQLException e) {
            Debug.debug("ERROR IN SQL [TICKET]");
            e.printStackTrace();
        }
    }

    public static ArrayList<Player> getConversation(Ticket r) {
        ArrayList<Player> conversations = conversation.get(r.getID());
        if (conversations != null) {
            return conversations;
        }

        ArrayList<Player> a = new ArrayList<>();
        if (r.getTicketer() != null) a.add(r.getTicketer());
        if (r.getSupporter() != null) a.add(r.getSupporter());

        conversation.put(r.getID(), a);
        return a;
    }

    public static void close(Ticket r) {
        Script.executeAsyncUpdate("UPDATE ticket SET closed=" + System.currentTimeMillis() + " WHERE id=" + r.getID());

        ArrayList<Player> list = getConversation(r);

        tickets.removeIf(n -> r.getID() == n.getID());
        conversation.keySet().removeIf(n -> r.getID() == n);

        for (Player p : list) {
            p.setPlayerListName(Script.getName(p));
        }
    }

    public static void addToConversation(int ticketID, Player p) {
        ArrayList<Player> conv;
        if (conversation.containsKey(ticketID)) {
            conv = conversation.get(ticketID);
            if (!conv.contains(p)) conv.add(p);
        } else {
            conv = new ArrayList<>();
            conv.add(p);

            Ticket foundTicket = null;
            for (Ticket ticket : tickets) {
                if (ticket.getID() == ticketID) {
                    foundTicket = ticket;
                    break;
                }
            }

            if (foundTicket == null) throw new IllegalArgumentException("ticket not found");

            if (foundTicket.getTicketer() != null && !conv.contains(foundTicket.getTicketer()))
                conv.add(foundTicket.getTicketer());
            if (foundTicket.getSupporter() != null && !conv.contains(foundTicket.getSupporter()))
                conv.add(foundTicket.getSupporter());

        }
        conversation.put(ticketID, conv);
        Script.updateListname(p);
    }

    public static void removeFromConversation(int ticketID, Player p) {
        ArrayList<Player> conv = conversation.get(ticketID);

        if (conv != null) {
            conv.remove(p);
            conversation.put(ticketID, conv);
        }
    }

    public static int getQueueTime(TicketTopic ticket) {
        double time = 0;
        for (Map.Entry<Integer, Ticket.Queue> ent : queue.entrySet()) {
            Ticket.Queue t = ent.getValue();
        }
        int active = 0;
        if (ticket.getID() == 0) {
            for (Player p : Script.getNRPTeam()) {
                if (!AFK.isAFK(p) && !isInTicket(p)) {
                    active++;
                }
            }
            if (active != 0) {
                time = (int) (time / active);
            } else {
                time = 0;
            }
        }
        return (int) time;
    }

    public static int[] getQueueTimeWithAmount(TicketTopic ticket) {
        double time = 0;
        int amount = 0;
        for (Map.Entry<Integer, Ticket.Queue> ent : queue.entrySet()) {
            Ticket.Queue t = ent.getValue();
            if (t.getTicketTopic().equals(ticket)) {
                amount++;
            }
        }
        int active = 0;
        for (Player p : Script.getNRPTeam()) {
            if (!AFK.isAFK(p) && !isInTicket(p)) {
                active++;
            }
        }

        if (active != 0) {
            time = (int) (time / active);
        } else {
            time = (int) time;
        }
        return new int[]{(int) time, amount};
    }

    public static void sendTicketTitle(Player p) {
        Title.sendTitle(p, 20, 50, 20, "§bDein Ticket wurde angenommen");
    }

    public static void reset() {
        queue.clear();
        SDuty.updateScoreboard();
    }

    public static HashMap<TicketTopic, Integer> getTicketAmount() {
        HashMap<TicketTopic, Integer> map = new HashMap<>();
        for (TicketTopic t : TicketTopic.values()) map.put(t, 0);
        for (Map.Entry<Integer, Ticket.Queue> ent : queue.entrySet()) {
            Ticket.Queue r = ent.getValue();
            map.put(r.getTicketTopic(), (map.get(r.getTicketTopic()) + 1));
        }
        return map;
    }

    public static boolean isInTicket(Player p) {
        for (Ticket t : tickets) {
            if (t.getTicketer().equals(p) || t.getSupporter().equals(p)) return true;
        }
        for (ArrayList<Player> conv : conversation.values()) {
            if (conv.contains(p)) return true;
        }
        return false;
    }

    public static String getGreeting(Player p) {
        return Script.getString(p, "ticket_greeting", "greeting");
    }

    public static String getFarewell(Player p) {
        return Script.getString(p, "ticket_farewell", "farewell");
    }

    public static boolean isInQueue(Player p) {
        return queue.containsKey(Script.getNRPID(p));
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (args.length >= 2 && args[0].equalsIgnoreCase("greeting") && Script.hasRank(p, Rank.SUPPORTER, false)) {
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            msg = msg.trim();
            if (msg.length() > 300) {
                p.sendMessage(Messages.ERROR + "Die Nachricht darf maximal 100 Zeichen lang sein.");
                return true;
            }

            if (getGreeting(p) == null) {
                Script.executeAsyncUpdate("INSERT INTO ticket_greeting (nrp_id, greeting) VALUES (" + Script.getNRPID(p) + ", '" + msg + "');");
                p.sendMessage(PREFIX + "Du hast die neue Begrüßungsnachricht gesetzt.");
                return true;
            } else {

                if (getGreeting(p).equals(msg)) {
                    p.sendMessage(Messages.ERROR + "Die Nachricht ist bereits die aktuelle Nachricht.");
                    return true;
                }
                Script.executeAsyncUpdate("UPDATE ticket_greeting SET greeting='" + msg + "' WHERE nrp_id=" + Script.getNRPID(p) + ";");
                p.sendMessage(PREFIX + "Du hast die neue Begrüßungsnachricht gesetzt.");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("greeting") && Script.hasRank(p, Rank.SUPPORTER, false)) {
            if (getGreeting(p) == null) {
                p.sendMessage(Messages.ERROR + "Du hast noch keine Begrüßungsnachricht gesetzt.");
                return true;
            }
            p.sendMessage(PREFIX + "Deine aktuelle Begrüßungsnachricht: §6" + getGreeting(p));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("greeting") && (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) && Script.hasRank(p, Rank.SUPPORTER, false)) {
            if (getGreeting(p) == null) {
                p.sendMessage(Messages.ERROR + "Du hast noch keine Begrüßungsnachricht gesetzt.");
                return true;
            }
            Script.executeAsyncUpdate("DELETE FROM ticket_greeting WHERE nrp_id=" + Script.getNRPID(p) + ";");
            p.sendMessage(PREFIX + "Du hast die Begrüßungsnachricht gelöscht.");
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("farewell") && Script.hasRank(p, Rank.SUPPORTER, false)) {
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            msg = msg.trim();
            if (msg.length() > 300) {
                p.sendMessage(Messages.ERROR + "Die Nachricht darf maximal 100 Zeichen lang sein.");
                return true;
            }

            if (getFarewell(p) == null) {
                Script.executeAsyncUpdate("INSERT INTO ticket_farewell (nrp_id, farewell) VALUES (" + Script.getNRPID(p) + ", '" + msg + "');");
                p.sendMessage(PREFIX + "Du hast die neue Verabschiedungsnachricht gesetzt.");
                return true;
            } else {

                if (getFarewell(p).equals(msg)) {
                    p.sendMessage(Messages.ERROR + "Die Nachricht ist bereits die aktuelle Nachricht.");
                    return true;
                }
                Script.executeAsyncUpdate("UPDATE ticket_farewell SET farewell='" + msg + "' WHERE nrp_id=" + Script.getNRPID(p) + ";");
                p.sendMessage(PREFIX + "Du hast die neue Verabschiedungsnachricht gesetzt.");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("farewell") && Script.hasRank(p, Rank.SUPPORTER, false)) {
            if (getFarewell(p) == null) {
                p.sendMessage(Messages.ERROR + "Du hast noch keine Verabschiedungsnachricht gesetzt.");
                return true;
            }
            p.sendMessage(PREFIX + "Deine aktuelle Verabschiedungsnachricht: §6" + getFarewell(p));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("farewell") && (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) && Script.hasRank(p, Rank.SUPPORTER, false)) {
            if (getFarewell(p) == null) {
                p.sendMessage(Messages.ERROR + "Du hast noch keine Verabschiedungsnachricht gesetzt.");
                return true;
            }
            Script.executeAsyncUpdate("DELETE FROM ticket_farewell WHERE nrp_id=" + Script.getNRPID(p) + ";");
            p.sendMessage(PREFIX + "Du hast die Verabschiedungsnachricht gelöscht.");
            return true;
        }

        if(Script.isNRPTeam(p)) {
            p.sendMessage(Messages.ERROR + "Teammitglieder können keine Tickets schreiben.");
            for(Player team : Script.getNRPTeam()) {
                if(Script.hasRank(team, Rank.ADMINISTRATOR, false)) {
                    team.sendMessage(AntiCheatSystem.PREFIX + "§c" + Script.getNRPID(p) + " §chat versucht ein Ticket zu schreiben.");
                }
            }
            return true;
        }

        long time = System.currentTimeMillis();
        Long lastUsage = CancelTicket.cooldown.get(p);
        if (lastUsage != null && lastUsage + 60 * 1000 > time) {
            p.sendMessage(PREFIX + "Warte bis du ein neues Ticket schreiben kannst.");
            return true;
        }

        if (isInQueue(p)) {
            int count = 0;
            Ticket.Queue r_q = queue.get(Script.getNRPID(p));
            for (Map.Entry<Integer, Ticket.Queue> ent : queue.entrySet()) {
                Ticket.Queue q = ent.getValue();
                if (q.getTicketTopic().equals(r_q.getTicketTopic())) count++;
            }

            p.sendMessage(PREFIX + "Es sind noch " + count + " Tickets vor dir.");
            p.sendMessage(Messages.INFO + "Du kannst das Ticket mit /cancelticket zurückziehen.");
        } else {
            if (isInTicket(p)) {
                p.sendMessage(Messages.ERROR + "Du bist bereits in einem Ticket.");
                return true;
            }

            openTicket(p);
        }

        return true;
    }
}
