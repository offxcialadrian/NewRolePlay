package de.newrp.Chat;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Notications;
import de.newrp.Administrator.Punish;
import de.newrp.Player.Passwort;
import de.newrp.Ticket.Ticket;
import de.newrp.Ticket.TicketCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class Chat implements Listener {

    public enum ChatDistance {
        NORMAL("§r", new double[]{10.0, -1.0, -1.0}),
        HIGH("§7", new double[]{20.0, 5.0, -1.0}),
        MAXIMUM("§8", new double[]{30.0, 8.0, -1.0}),
        IGNORE("§r", new double[]{-1.0, -1.0, 35.0});

        private final String color;
        private final double[] distances;

        ChatDistance(String color, double[] distances) {
            this.color = color;
            this.distances = distances;
        }

        public String getColor() {
            return color;
        }

        public double[] getDistances() {
            return distances;
        }
    }

    public enum ChatType {
        NORMAL(0),
        WHISPER(1),
        SHOUT(2),
        SPY(-1);
        private final int index;

        ChatType(int index) {
            this.index = index;
        }

        public ChatDistance getChatDistance(double distance) {
            if (this == SPY) {
                return ChatDistance.IGNORE;
            }
            for (ChatDistance chatDistance : ChatDistance.values()) {
                double[] distances = chatDistance.getDistances();
                double maxDistance = distances[index];
                if (checkDistance(maxDistance, distance))
                    return chatDistance;
            }
            return ChatDistance.IGNORE;
        }

        private boolean checkDistance(double maxDistance, double distance) {
            return !isIgnored(maxDistance) && distance <= maxDistance;
        }


        private boolean isIgnored(double distance) {
            return Double.compare(distance, -1.0) == 0;
        }
    }

    public static Set<String> getMentionedNames(String message) {
        return Arrays.stream(message.split(" "))
                .filter(msg -> Bukkit.getPlayerExact(msg) != null)
                .collect(Collectors.toSet());
    }

    public static String constructMessage(Player p, String message, String speakWord, Set<String> foundNames, double distance, ChatType chatType) {
        StringBuilder sb = new StringBuilder("§8[§c" + p.getLevel() + "§8]").append(" ");
        ChatDistance chatDistance = chatType.getChatDistance(distance);
        String color = chatDistance.getColor();
        for (String foundName : foundNames) {
            message = StringUtils.replace(message, foundName, "§l" + foundName + color);
        }
        return sb.append(color).append(Script.getName(p)).append(" ").append(speakWord).append(": ").append(message).toString();
    }

    public static String constructMessage(String s, String message, String speakWord, Set<String> foundNames, double distance, ChatType chatType) {
        StringBuilder sb = new StringBuilder("§8[§c" + "NPC" + "§8]").append(" ");
        ChatDistance chatDistance = chatType.getChatDistance(distance);
        String color = chatDistance.getColor();
        for (String foundName : foundNames) {
            message = StringUtils.replace(message, foundName, "§l" + foundName + color);
        }
        return sb.append(color).append(s).append(" ").append(speakWord).append(": ").append(message).toString();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);


        if (Passwort.isLocked(e.getPlayer())) {
            p.sendMessage(Messages.ERROR + "Du musst dein Passwort eingeben!");
            return;
        }

        if (TicketCommand.getTicket(p) != null) {
            handleTicket(p, e.getMessage());
            return;
        }

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return;
        }

        if(Friedhof.isDead(p)) {
            p.sendMessage(Messages.ERROR + "Tote können nicht reden!");
            return;
        }

        for(String arg : e.getMessage().split(" ")) {
            if(arg.contains("http://") || arg.contains("https://") || arg.contains("www.") || arg.contains(".de")) {
                p.sendMessage(Messages.ERROR + "Du darfst keine Links in den RolePlay-Chat senden!");
                return;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return;
            }
        }

        String message = e.getMessage();
        Set<String> foundNames = getMentionedNames(message);
        String speakWord = "sagt";
        Notications.sendMessage(Notications.NotificationType.CHAT, "§8[§c" + p.getLevel() + "§8] §7" + Script.getName(p) + " sagt: §7" + message);
        for (Player online : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distance(online.getLocation());
            if (distance > 30.0D) {
                continue;
            }
            online.sendMessage(constructMessage(p, message, speakWord, foundNames, distance, ChatType.NORMAL));
        }

    }

    public static void handleTicket(Player p, String message) {
        Ticket t = TicketCommand.getTicket(p);
        ArrayList<Player> conv = TicketCommand.getConversation(t);
        if (conv.size() < 2 && !Script.isInTestMode()) {
            TicketCommand.close(t);
            p.sendMessage(TicketCommand.PREFIX + "Der Spieler hat das Ticket verlassen (Quit).");
            return;
        }

        for (Player players : conv) {
            players.sendMessage("§b§lTICKET §8× §b" + Script.getName(p) + ": " + message);
        }
        Script.executeAsyncUpdate("INSERT INTO ticket_conv (ticketID, sender, message, time) VALUES(" + t.getID() + ", " + Script.getNRPID(p) + ", '" + message + "', " + System.currentTimeMillis() + ")");
    }
}
