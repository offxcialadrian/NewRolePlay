package de.newrp.Call;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class Call {

    public static Map<Integer, List<Player>> ON_CALL = new HashMap<>();
    public static Map<Integer, List<Player>> WAITING_FOR_CALL = new HashMap<>();

    public static boolean isOnCall(Player p) {
        return getCallIDByPlayer(p) != -1;
    }

    public static boolean isWaitingForCall(Player p) {
        return WAITING_FOR_CALL.containsValue(p);
    }

    public static void accept(Player p) {
        if (isWaitingForCall(p)) {
            if (ON_CALL.get(getCallIDByPlayer(p)).size() == 0) {
                List<Player> playerList = WAITING_FOR_CALL.get(getCallIDByPlayer(p));
                playerList.remove(p);
                Player tg = playerList.get(0);
                List<Player> people = getParticipants(getCallIDByPlayer(p));
                people.add(p);
                people.add(tg);
                ON_CALL.put(getCallIDByPlayer(p), people);
                p.sendMessage("§8[§eTelefon§8] §eDer Anruf wurde angenommen!");
                tg.sendMessage("§8[§eTelefon§8] §eDu telefonierst nun mit " + Script.getName(tg));
            } else {
                List<Player> onCallList = ON_CALL.get(getCallIDByPlayer(p));
                List<Player> waitingList = WAITING_FOR_CALL.get(getCallIDByPlayer(p));
                waitingList.remove(p);
                onCallList.add(p);
                ON_CALL.put(getCallIDByPlayer(p), onCallList);
                WAITING_FOR_CALL.put(getCallIDByPlayer(p), waitingList);
                StringBuilder sb = new StringBuilder();
                sb.append("§8[§eTelefon§8] §eDu bist nun in einem Anruf mit: ");
                for (Player inCall : onCallList) {
                    Iterator<Player> iterator = onCallList.iterator();
                    if (iterator.hasNext()) {
                        sb.append(inCall.getName()).append(", ");
                    } else {
                        sb.append(inCall.getName()).append(".");
                    }
                }
                p.sendMessage(sb.toString());
                sendSystemMessage(p, "§7" + Script.getName(p) + " ist dem Anruf beigetreten.", true);
            }
        }
    }

    public static int getCallIDByPlayer(Player p) {
        for (Map.Entry entry : ON_CALL.entrySet()) {
            if (p.equals(entry.getValue())) return (Integer) entry.getKey();
        }

        for (Map.Entry entry : WAITING_FOR_CALL.entrySet()) {
            if (p.equals(entry.getValue())) return (Integer) entry.getKey();
        }
        Script.sendBugReport(p, "COULDN'T FIND CALL_ID at getCallIDByPlayer");
        return -1;
    }

    public static void sendMessage(Player chatter, String msg) {
        for (Player p : ON_CALL.get(getCallIDByPlayer(chatter))) {
            if (!p.equals(chatter))
                p.sendMessage("§8[§eTelefon§8] §e" + Script.getName(chatter) + " sagt: " + msg);
        }
    }

    public static void sendSystemMessage(Player chatter, String msg, boolean skipChatter) {
        for (Player p : ON_CALL.get(getCallIDByPlayer(chatter))) {
            if (!skipChatter) {
                p.sendMessage("§8[§eTelefon§8] §e" + msg);
            } else {
                if (p != chatter) {
                    p.sendMessage("§8[§eTelefon§8] §e" + msg);
                }
            }
        }
    }

    public static int getParticipantsAmmount(int i) {
        return ON_CALL.get(i).size() + WAITING_FOR_CALL.get(i).size();
    }

    public static List<Player> getParticipants(int i) {
        return ON_CALL.get(i);
    }

    public static void sendSystemMessage(int id, String msg) {
        for (Player p : ON_CALL.get(id)) {
            p.sendMessage("§8[§eTelefon§8] §e" + msg);
        }
    }

    public static boolean callExists(int id) {
        return ON_CALL.containsKey(id) || WAITING_FOR_CALL.containsKey(id);
    }

    public static void hangup(Player p) {
        List<Player> playerList = ON_CALL.get(getCallIDByPlayer(p));
        if (playerList.size() == 2) {
            sendSystemMessage(p, "§7Der Anruf wurde beendet.", false);
            ON_CALL.remove(getCallIDByPlayer(p));
            WAITING_FOR_CALL.remove(p);
        } else {
            playerList.remove(p);
            int i = getCallIDByPlayer(p);
            ON_CALL.remove(i);
            ON_CALL.put(i, playerList);
            sendSystemMessage(i, "§7" + Script.getName(p) + " hat den Anruf verlassen.");
        }
    }

    public static void call(Player caller, Player tg) {
        tg.playSound(tg.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
        tg.sendMessage("§8[§eTelefon§8] §eDein Handy klingelt! Ein Anruf von " + Script.getName(caller));
        Script.sendActionBar(tg, "§7Dein Handy klingelt!");
        tg.sendMessage("§8[§eTelefon§8] §e" + Messages.INFO + "Benutze /p zum Annehmen oder /h zum ablehnen");
        String pronun = (Script.getGender(caller) == Gender.MALE ? "seinem" : "ihrem");
        Script.sendLocalMessage(5, caller, "§a§o* " + Script.getName(caller) + " wählt eine Nummer auf " + pronun + " Handy.");
        List<Player> people = new ArrayList<>();
        people.add(caller);
        people.add(tg);
        WAITING_FOR_CALL.put(generateCallID(), people);
    }


    public static int generateCallID() {
        int i = Script.getRandom(0, 100);
        if (callExists(i)) {
            generateCallID();
        } else {
            return i;
        }
        return 0;
    }

}
