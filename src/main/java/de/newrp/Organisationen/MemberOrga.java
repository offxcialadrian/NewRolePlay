package de.newrp.Organisationen;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MemberOrga implements @NotNull Listener {

    public static ArrayList<Player> falcone = new ArrayList<>();
    public static ArrayList<Player> kartell = new ArrayList<>();
    public static ArrayList<Player> braterstwo = new ArrayList<>();
    public static ArrayList<Player> corleone = new ArrayList<>();
    public static ArrayList<Player> sinaloa = new ArrayList<>();

    @EventHandler
    public static void onJoin(PlayerJoinEvent event) {
        addMember(event.getPlayer());
    }

    public static ArrayList<Player> getMembers(Organisation orga) {
        return getOrga(orga);
    }

    public static void sendMessage(Organisation orga, String string) {
        for (Player player : getMembers(orga)) {
            if (player.isOnline()) {
                player.sendMessage(string);
            }
        }
    }

    public static Boolean isMember(Organisation orga, Player player) {
        return Objects.requireNonNull(getOrga(orga)).contains(player);
    }

    public static void addMember(Player player) {
        if (Organisation.hasOrganisation(player)) {
            Objects.requireNonNull(getOrga(Organisation.getOrganisation(player))).add(player);
        }
    }

    public static void removeMember(Player player) {
        Objects.requireNonNull(getOrga(Organisation.getOrganisation(player))).remove(player);
    }

    private static ArrayList<Player> getOrga(Organisation orga) {
        switch (orga) {
            case FALCONE:
                return falcone;
            case KARTELL:
                return kartell;
            case BRATERSTWO:
                return braterstwo;
            case CORLEONE:
                return corleone;
            case SINALOA:
                return sinaloa;
        }
        return null;
    }
}
