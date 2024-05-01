package de.newrp.Berufe;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class MemberBeruf implements @NotNull Listener {

    public static HashMap<Player, Boolean> regierung = new HashMap<>();
    public static HashMap<Player, Boolean> polizei = new HashMap<>();
    public static HashMap<Player, Boolean> rettungsdienst = new HashMap<>();
    public static HashMap<Player, Boolean> news = new HashMap<>();

    @EventHandler
    public static void onJoin(PlayerJoinEvent event) {
        addMember(event.getPlayer());
    }

    public static Set<Player> getMembers(Beruf.Berufe berufe) {
        HashMap<Player, Boolean> beruf = getBeruf(berufe);
        if (beruf != null) {
            return Objects.requireNonNull(beruf).keySet();
        } else {
            return null;
        }
    }

    public static void sendDutyMessage(Beruf.Berufe beruf, String string) {
        for (Player player : Objects.requireNonNull(getMembers(beruf))) {
            if (player.isOnline()) {
                if (isDuty(player)) {
                    player.sendMessage(string);
                }
            }
        }
    }

    public static void sendMessage(Beruf.Berufe beruf, String string) {
        for (Player player : Objects.requireNonNull(getMembers(beruf))) {
            if (player.isOnline()) {
                player.sendMessage(string);
            }
        }
    }

    public static Boolean isDuty(Player player) {
        return Objects.requireNonNull(getBeruf(Beruf.getBeruf(player))).get(player);
    }

    public static Boolean isMember(Beruf.Berufe beruf, Player player) {
        return Objects.requireNonNull(getBeruf(beruf)).containsKey(player);
    }

    public static void addMember(Player player) {
        if (Beruf.hasBeruf(player)) {
            Objects.requireNonNull(getBeruf(Beruf.getBeruf(player))).put(player, false);
        }
    }

    public static void removeMember(Player player) {
        Objects.requireNonNull(getBeruf(Beruf.getBeruf(player))).remove(player);
    }

    public static void changeDuty(Player player, Boolean duty) {
        Objects.requireNonNull(getBeruf(Beruf.getBeruf(player))).put(player, duty);
    }

    private static HashMap<Player, Boolean> getBeruf(Beruf.Berufe beruf) {
        switch (beruf) {
            case GOVERNMENT:
                return regierung;
            case POLICE:
                return polizei;
            case RETTUNGSDIENST:
                return rettungsdienst;
            case NEWS:
                return news;
        }
        return null;
    }
}
