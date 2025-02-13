package de.newrp.Call;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.WiretapCall;
import de.newrp.Player.Mobile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class Call {

    public static Map<Integer, List<Player>> ON_CALL = new HashMap<>();
    public static Map<Integer, List<Player>> WAITING_FOR_CALL = new HashMap<>();

    public static boolean isOnCall(Player p) {
        return getCallIDByPlayer(p) != -1;
    }

    public static boolean isOnActiveCall(Player p) {
        for (int i : ON_CALL.keySet()) {
            if (ON_CALL.get(i).contains(p)) {
                return true;
            }
        }
        return false;
    }

    public static String PREFIX = "§8[§eTelefon§8] §e" + Messages.ARROW + " §7";

    public static boolean isWaitingForCall(Player p) {
        for(List<Player> playerList : WAITING_FOR_CALL.values()) {
            if(playerList.contains(p)) return true;
        }
        return false;
    }

    public static void accept(Player p) {
        if (isWaitingForCall(p)) {
            if (ON_CALL.get(getCallIDByPlayer(p)).isEmpty()) {
                List<Player> playerList = WAITING_FOR_CALL.get(getCallIDByPlayer(p));
                playerList.remove(p);
                Player tg = playerList.get(0);
                List<Player> people = getParticipants(getCallIDByPlayer(p));
                people.add(p);
                people.add(tg);
                ON_CALL.put(getCallIDByPlayer(p), people);
                p.sendMessage(PREFIX + "Der Anruf wurde angenommen!");
                tg.sendMessage(PREFIX + "Du telefonierst nun mit " + Script.getName(tg));
                Script.executeAsyncUpdate("INSERT INTO call_history (nrp_id, participants, time) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getName(tg) + "', '" + System.currentTimeMillis() + "')");
            } else {
                List<Player> onCallList = ON_CALL.get(getCallIDByPlayer(p));
                List<Player> waitingList = WAITING_FOR_CALL.get(getCallIDByPlayer(p));
                waitingList.remove(p);
                onCallList.add(p);
                ON_CALL.put(getCallIDByPlayer(p), onCallList);
                WAITING_FOR_CALL.put(getCallIDByPlayer(p), waitingList);
                StringBuilder sb = new StringBuilder();
                sb.append(PREFIX);
                for (Player inCall : onCallList) {
                    Iterator<Player> iterator = onCallList.iterator();
                    if (iterator.hasNext()) {
                        sb.append(inCall.getName()).append(", ");
                    } else {
                        sb.append(inCall.getName()).append(".");
                    }
                }
                p.sendMessage(PREFIX + "Du bist nun in einem Anruf mit: ");
                p.sendMessage(sb.toString());
                Script.executeAsyncUpdate("INSERT INTO call_history (nrp_id, participants, time) VALUES ('" + Script.getNRPID(p) + "', '" + sb + "', '" + System.currentTimeMillis() + "')");
                sendSystemMessage(p, "§7" + Script.getName(p) + " ist dem Anruf beigetreten.", true);
                WiretapCall.sendNotification(getCallIDByPlayer(p));
            }
        }
    }

    public static int getCallIDByPlayer(Player p) {
        for (int i : ON_CALL.keySet()) {
            if (ON_CALL.get(i).contains(p)) {
                return i;
            }
        }

        for (int i : WAITING_FOR_CALL.keySet()) {
            if (WAITING_FOR_CALL.get(i).contains(p)) {
                return i;
            }
        }
        return -1;
    }

    // 1Minify
    public static void sendMessage(Player chatter, String msg, boolean shout) {
        int callId = getCallIDByPlayer(chatter);
        if(!Mobile.hasConnection(chatter)) msg = distortMessage(msg);
        String message = Script.getName(chatter) + " " + (shout ? "schreit: " : (msg.endsWith("?") ? "fragt: " : "sagt: ")) + msg;
        for (Player p : ON_CALL.get(callId)) {
            if (!p.equals(chatter)) {
                Mobile.getPhone(p).removeAkku(p, 1);
                p.sendMessage(PREFIX + message);
            }
        }
        WiretapCall.sendCallMessage(callId, message);
    }

    public static void sendSystemMessage(Player chatter, String msg, boolean skipChatter) {
        for (Player p : ON_CALL.get(getCallIDByPlayer(chatter))) {
            if (!skipChatter) {
                p.sendMessage(PREFIX + msg);
            } else {
                if (p != chatter) {
                    p.sendMessage(PREFIX + msg);
                }
            }
        }
    }

    public static int getParticipantsAmount(int i) {
        if(ON_CALL.isEmpty()) return 0;
        if(WAITING_FOR_CALL.isEmpty()) return 0;
        if(ON_CALL.containsKey(i) && !WAITING_FOR_CALL.containsKey(i)) return ON_CALL.get(i).size();
        if(!ON_CALL.containsKey(i) && WAITING_FOR_CALL.containsKey(i)) return WAITING_FOR_CALL.get(i).size();
        return 0;
    }

    public static List<Player> getParticipants(int i) {
        if(ON_CALL.isEmpty()) return new ArrayList<>();
        return ON_CALL.get(i);
    }

    public static void sendSystemMessage(int id, String msg) {
        for (Player p : ON_CALL.get(id)) {
            p.sendMessage(PREFIX + msg);
        }
    }

    public static boolean callExists(int id) {
        return ON_CALL.containsKey(id) || WAITING_FOR_CALL.containsKey(id);
    }

    public static void hangup(Player p) {
        List<Player> playerList = ON_CALL.get(getCallIDByPlayer(p));
        if(playerList == null) return;
        if(playerList.isEmpty()) return;
        for(Player player : WAITING_FOR_CALL.get(getCallIDByPlayer(p))) {
            player.sendMessage(PREFIX + "Der Anruf wurde abgebrochen.");
            Script.executeUpdate("INSERT INTO missed_calls (fromID, toID, time) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getNRPID(player) + "', '" + System.currentTimeMillis() + "')");
        }
        if (playerList.size() == 2) {
            sendSystemMessage(p, "§7Der Anruf wurde beendet.", false);
            WAITING_FOR_CALL.remove(getCallIDByPlayer(p));
            ON_CALL.remove(getCallIDByPlayer(p));
        } else {
            playerList.remove(p);
            int i = getCallIDByPlayer(p);
            ON_CALL.remove(i);
            ON_CALL.put(i, playerList);
            sendSystemMessage(i, "§7" + Script.getName(p) + " hat den Anruf verlassen.");
        }
    }

    public static void call(Player caller, Player tg) {
        if(!Mobile.getPhone(tg).getLautlos(tg)) tg.playSound(tg.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
        tg.sendMessage(PREFIX + "Dein Handy klingelt! Ein Anruf von " + Script.getName(caller));
        Script.sendActionBar(tg, "§7Dein Handy klingelt!");
        tg.sendMessage(PREFIX + Messages.INFO + "Benutze /p zum Annehmen oder /h zum ablehnen");
        String pronun = (Script.getGender(caller) == Gender.MALE ? "seinem" : "ihrem");
        Script.sendLocalMessage(5, caller, "§a§o* " + Script.getName(caller) + " wählt eine Nummer auf " + pronun + " " + Mobile.getPhone(caller).getName() + ".");
        ON_CALL.put(generateCallID(), new ArrayList<>(Collections.singletonList(caller)));
        WAITING_FOR_CALL.put(getCallIDByPlayer(caller), new ArrayList<>(Collections.singletonList(tg)));
    }

    public static void addParticipant(Player p, Player tg) {
        int callID = getCallIDByPlayer(p);
        List<Player> playerList = WAITING_FOR_CALL.get(callID);
        playerList.add(tg);
        WAITING_FOR_CALL.put(callID, playerList);
        tg.sendMessage(PREFIX + "Dein Handy klingelt! Ein Anruf von " + Script.getName(p));
        Script.sendActionBar(tg, "§7Dein Handy klingelt!");
        tg.sendMessage(PREFIX + Messages.INFO + "Benutze /p zum Annehmen oder /h zum ablehnen");
        sendSystemMessage(p, Script.getName(p) + " hat " + Script.getName(tg) + " zum Anruf hinzugefügt.", false);
    }

    public static void deny(Player p) {
        sendSystemMessage(p, Script.getName(p) + " hat den Anruf abgelehnt.", true);
        if (isWaitingForCall(p)) {
            int callID = getCallIDByPlayer(p);
            List<Player> playerList = WAITING_FOR_CALL.get(callID);
            playerList.remove(p);
            if(playerList.size() == 1) {
                WAITING_FOR_CALL.remove(callID);
            } else {
                WAITING_FOR_CALL.put(callID, playerList);
            }
        } else if(isOnCall(p)) {
            hangup(p);
        }
    }

    public static void abort(Player p) {
        sendSystemMessage(p, Script.getName(p) + " hat den Anruf abgebrochen.", false);
        if (isWaitingForCall(p)) {
            int callID = getCallIDByPlayer(p);
            List<Player> playerList = WAITING_FOR_CALL.get(callID);
            for(Player player : playerList) {
                player.sendMessage(PREFIX + "Der Anruf wurde abgebrochen.");
                Script.executeUpdate("INSERT INTO missed_calls (fromID, toID, time) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getNRPID(player) + "', '" + System.currentTimeMillis() + "')");
            }
            WAITING_FOR_CALL.remove(callID);
            ON_CALL.remove(callID);
        } else if(isOnCall(p)) {
            hangup(p);
        }
    }

    public static String distortMessage(String msg) {
        StringBuilder sb = new StringBuilder();
        for (char c : msg.toCharArray()) {
            Random r = new Random();
            int i = r.nextInt(100);
            if (i <= 60) {
                sb.append(c);
            } else {
                sb.append("#");
            }
        }
        return sb.toString();
    }


    public static int generateCallID() {
        Random r = new Random();
        int i = r.nextInt(1000000);
        if (callExists(i)) {
            return generateCallID();
        }
        return i;
    }

}
