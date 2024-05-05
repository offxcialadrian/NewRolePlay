package de.newrp.features.emergencycall.impl;

import com.google.common.collect.Lists;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.Notruf;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.AcceptEmergencyCallMetadata;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class EmergencyCallService implements IEmergencyCallService {

    private final Set<EmergencyCall> emergencyCalls = new HashSet<>();
    private final Set<Beruf.Berufe> factionsWithEmergencyCalls = new HashSet<>();

    public EmergencyCallService() {
        this.factionsWithEmergencyCalls.add(Beruf.Berufe.POLICE);
        this.factionsWithEmergencyCalls.add(Beruf.Berufe.RETTUNGSDIENST);
    }

    @Override
    public void createEmergencyCall(Player player, Location location, Beruf.Berufe targetFaction, final String reason) {
        final EmergencyCall emergencyCall = new EmergencyCall(player, location, targetFaction, reason, null);
        this.emergencyCalls.add(emergencyCall);

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefix()).append("§6Achtung! Ein Notruf von ").append(Script.getName(player)).append(" ist eingegangen.")
                .append("\n").append(getPrefix()).append("§6Vorfall§8: §6").append(reason)
                .append("\n").append(buildNearbyPlayersString(location, targetFaction));

        for (Player member : targetFaction.getBeruf().keySet()) {
            member.sendMessage(stringBuilder.toString());
            Script.sendClickableMessage(player, getPrefix() + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + player.getName(), "Klicke hier um den Notruf anzunehmen.");
        }
    }

    @Override
    public void dropEmergencyCall(EmergencyCall emergencyCall) {
        final boolean hasBeenRemoved = this.emergencyCalls.remove(emergencyCall);
        if(hasBeenRemoved) {
            for (Player member : emergencyCall.faction().getBeruf().keySet()) {
                member.sendMessage(this.getPrefix() + "Der Notruf von " + Script.getName(emergencyCall.sender()) + " wurde abgebrochen");
            }
        }
    }

    @Override
    public void doneEmergencyCall(Player player, EmergencyCall emergencyCall) {
        for (Player member : emergencyCall.faction().getBeruf().keySet()) {
            member.sendMessage(this.getPrefix() + Script.getName(player) + " hat den Notruf von " + Script.getName(emergencyCall.sender()) + " abgeschlossen");
        }
        this.emergencyCalls.remove(emergencyCall);
    }

    @Override
    public boolean hasBeenAccepted(EmergencyCall emergencyCall) {
        return emergencyCall.acceptEmergencyCallMetadata() != null;
    }

    @Override
    public Set<EmergencyCall> getAllOpenEmergencyCallsForFaction(Beruf.Berufe faction) {
        return this.emergencyCalls.stream()
                .filter(e -> e.acceptEmergencyCallMetadata() == null && e.faction() == faction)
                .collect(Collectors.toSet());
    }

    @Override
    public void acceptEmergencyCall(Player player, EmergencyCall emergencyCall) {
        final AcceptEmergencyCallMetadata acceptEmergencyCallMetadata = new AcceptEmergencyCallMetadata(player, player.getLocation(), System.currentTimeMillis());
        emergencyCall.acceptEmergencyCallMetadata(acceptEmergencyCallMetadata);
    }

    @Override
    public Optional<EmergencyCall> getAcceptedEmergencyCallByFactionMember(Player player) {
        return emergencyCalls.stream().filter(e -> e.acceptEmergencyCallMetadata() != null && e.acceptEmergencyCallMetadata().player().getUniqueId() == player.getUniqueId()).findFirst();
    }

    @Override
    public void requeueAcceptedEmergencyCall(Player player, EmergencyCall emergencyCall) {
        emergencyCall.acceptEmergencyCallMetadata(null);
        for (Player member : emergencyCall.faction().getBeruf().keySet()) {
            member.sendMessage(this.getPrefix() + Script.getName(player) + " hat den Notruf von " + Script.getName(emergencyCall.sender()) + " wieder geöffnet!");
        }
    }

    @Override
    public boolean factionMemberHasAccepted(Player player) {
        return emergencyCalls.stream().anyMatch(e -> e.acceptEmergencyCallMetadata() != null && e.acceptEmergencyCallMetadata().player().getUniqueId() == player.getUniqueId());
    }

    @Override
    public Set<Beruf.Berufe> getAllFactionsWithEmergencyCalls() {
        return this.factionsWithEmergencyCalls;
    }

    @Override
    public Optional<EmergencyCall> getEmergencyCallByPlayer(Player player, Beruf.Berufe faction) {
        return this.emergencyCalls.stream()
                .filter(e -> e.sender().getUniqueId() == player.getUniqueId() && (faction == null || e.faction() == faction))
                .findFirst();
    }

    @Override
    public int estimateArrival(double meter) {
        return (int) (meter / 6.0);
    }

    @Override
    public List<String> getReasonsForEmergency(Beruf.Berufe faction) {
        final List<String> reasons = new ArrayList<>();
        switch (faction) {
            case RETTUNGSDIENST:
                reasons.add("Test Rettungsdienst");
                break;
            case POLICE:
                reasons.add("Test Cops");
                break;
        }
        return reasons;
    }

    @Override
    public String getPrefix() {
        return "§8[§4Notruf§8] §c" + Messages.ARROW + "§7 ";
    }

    @Override
    public List<Player> getNearbyPlayersOfFactionToLocation(Location location, Beruf.Berufe faction) {
        return faction.getBeruf().keySet().stream()
                .sorted(Comparator.comparingDouble((Player a) -> a.getLocation().distance(location)))
                .limit(2)
                .collect(Collectors.toList());
    }

    private String buildNearbyPlayersString(final Location location, final Beruf.Berufe faction) {
        final StringBuilder stringBuilder = new StringBuilder(getPrefix());
        stringBuilder.append("§6Am nächsten sind: ");
        List<Player> nearbyPlayers = getNearbyPlayersOfFactionToLocation(location, faction);
        if(nearbyPlayers.isEmpty()) {
            return "§6Kein Beamter ist in der Nähe";
        }

        final Player firstPlayer = nearbyPlayers.get(0);
        final Player secondPlayer = getOrDefault(1, null, nearbyPlayers);

        stringBuilder.append(Script.getName(firstPlayer)).append(" (").append((int) firstPlayer.getLocation().distance(location)).append("m)");
        if(secondPlayer != null) {
            stringBuilder.append(" und ").append(Script.getName(secondPlayer)).append(" (").append((int) secondPlayer.getLocation().distance(location)).append("m)");
        }

        return stringBuilder.toString();
    }

    private <E> E getOrDefault(int index, E defaultValue, List<E> list) {
        return index <= list.size() - 1 ? list.get(index) : defaultValue;
    }
}
