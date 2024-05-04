package de.newrp.features.emergencycall.impl;

import com.google.common.collect.Lists;
import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.AcceptEmergencyCallMetadata;
import de.newrp.features.emergencycall.data.EmergencyCall;
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
    }

    @Override
    public void dropEmergencyCall(Player player, Beruf.Berufe targetFaction) {
        this.emergencyCalls.removeIf(e -> e.sender().getUniqueId() == player.getUniqueId() && e.faction() == targetFaction);
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
    public void requeueAcceptedEmergencyCall(EmergencyCall emergencyCall) {
        emergencyCall.acceptEmergencyCallMetadata(null);
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
                .filter(e -> e.sender().getUniqueId() == player.getUniqueId() && e.faction() == faction)
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
}
