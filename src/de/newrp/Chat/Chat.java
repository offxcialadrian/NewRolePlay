package de.newrp.Chat;

import de.newrp.API.Friedhof;
import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.Punish;
import de.newrp.Call.Call;
import de.newrp.Player.Passwort;
import de.newrp.Player.ToggleWhisper;
import de.newrp.Ticket.Ticket;
import de.newrp.Ticket.TicketCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class Chat implements Listener {

    public static String[] Filter = new String[]{
            "Reallife",
            "reallife",
            "Admin",
            "admin",
            "oos",
            "Suppe",
            "Huan",
            "huan",
            "Endsieg",
            "endsieg",
            "Hitler",
            "hitler",
            "Heil",
            "heil",
            "Jude",
            "jude",
            "Ritzen",
            "ritzen",
            "Akbar",
            "akbar",
            "vergasen",
            "88",
            "Nazi",
            "nazi",
            "Transe",
            "transe",
            "Schwuchtel",
            "schwuchtel",
            "nigga",
            "nigger",
            "niggah"
    };
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

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return;
        }

        if (Passwort.isLocked(e.getPlayer())) {
            p.sendMessage(Messages.ERROR + "Du musst dein Passwort eingeben!");
            return;
        }



        for(String arg : e.getMessage().split(" ")) {
            if(arg.contains("ChatCraft")) {
                p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §eVerbindung mit ChatCraft\n\n§8§m------------------------------");
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + Script.getName(p) + " wurde vom Server gekickt (Verbindung mit ChatCraft).");
                return;
            }

            if(arg.startsWith("http://") || arg.startsWith("https://") || arg.startsWith("www.") || arg.endsWith(".de")  || arg.endsWith(".eu") || arg.startsWith("germanrp") || arg.startsWith("grp") || arg.startsWith("unicacity") || arg.startsWith("turniptales") || arg.toLowerCase().startsWith("turnip")) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Fremdwerbung bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + e.getMessage());
                p.sendMessage(AntiCheatSystem.PREFIX + "Es liegt ein Verdacht auf Fremdwerbung vor oder deine Nachricht enthält einen Link. Die Nachricht wurde nicht gesendet. Wenn du denkst, dass es sich um einen Fehler handelt, melde ihn bitte im Forum.");
                return;
            }

            if(Script.isIP(arg)) {
                p.sendMessage(Messages.ERROR + "Du darfst keine IPs in den RolePlay-Chat senden!");
                return;
            }
        }

        if (ToggleWhisper.whisper.contains(p.getName())) {
            Chat.handleChatFilter(p, e.getMessage());

            Set<String> foundNames = Chat.getMentionedNames(e.getMessage());
            Location pLoc = p.getLocation();
            String speakWord = "flüstert";
            Notifications.sendMessage(Notifications.NotificationType.CHAT, "§8[§c" + p.getLevel() + "§8] §7" + Script.getName(p) + " flüstert: §7" + e.getMessage());
            for (Player online : Bukkit.getOnlinePlayers()) {
                double distance = pLoc.distance(online.getLocation());
                if (distance > 8.0D) {
                    continue;
                }
                online.sendMessage(Chat.constructMessage(p, e.getMessage(), speakWord, foundNames, distance, Chat.ChatType.WHISPER));
            }

            Log.CHAT.write(p, "[Flüstern]" +  e.getMessage());
            return;
        }

        if (TicketCommand.getTicket(p) != null) {
            handleTicket(p, e.getMessage());
            return;
        }

        if(Friedhof.isDead(p)) {
            p.sendMessage(Messages.ERROR + "Tote können nicht reden!");
            return;
        }

        handleChatFilter(p, e.getMessage());

        if(Call.isOnActiveCall(p)) {
            Call.sendMessage(p, e.getMessage(), false);
        }

        String message = e.getMessage();
        Set<String> foundNames = getMentionedNames(message);
        String speakWord = "sagt";
        if(message.endsWith("?")) speakWord = "fragt";
        Notifications.sendMessage(Notifications.NotificationType.CHAT, "§8[§c" + Script.getLevel(p) + "§8] §7" + Script.getName(p) + " " + speakWord + ": §7" + message);
        Script.updateExpBar(p);
        for (Player online : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distance(online.getLocation());
            if (distance > 30.0D) {
                continue;
            }
            online.sendMessage(constructMessage(p, message, speakWord, foundNames, distance, ChatType.NORMAL));
        }
        Log.CHAT.write(p, "[Chat]" +  message);

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

    public static void handleChatFilter(Player p, String msg) {
        for(String arg : msg.split(" ")) {
            for(String filter : Filter) {
                if(arg.equalsIgnoreCase(filter) || arg.startsWith(filter) || arg.endsWith(filter) || arg.contains(filter)) {
                    Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, "Verdacht auf unangebrachtes Chatverhalten bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + msg);
                    return;
                }
            }
        }
    }
}
