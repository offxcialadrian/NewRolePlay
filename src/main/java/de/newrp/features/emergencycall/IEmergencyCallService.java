package de.newrp.features.emergencycall;

import de.newrp.Berufe.Beruf;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IEmergencyCallService {

    void createEmergencyCall(final Player player, final Location location, final Beruf.Berufe targetFaction, final String reason);

    void dropEmergencyCall(final EmergencyCall emergencyCall);

    void doneEmergencyCall(final Player player, final EmergencyCall emergencyCall);

    boolean hasBeenAccepted(final EmergencyCall emergencyCall);

    Set<EmergencyCall> getAllOpenEmergencyCallsForFaction(final Beruf.Berufe faction);

    void acceptEmergencyCall(final Player player, final EmergencyCall emergencyCall);

    Optional<EmergencyCall> getAcceptedEmergencyCallByFactionMember(final Player player);

    void requeueAcceptedEmergencyCall(final Player player, final EmergencyCall emergencyCall);

    boolean factionMemberHasAccepted(final Player player);

    Set<Beruf.Berufe> getAllFactionsWithEmergencyCalls();

    Optional<EmergencyCall> getEmergencyCallByPlayer(final Player player, final Beruf.Berufe faction);

    int estimateArrival(double meter);

    List<String> getReasonsForEmergency(final Beruf.Berufe faction);

    String getPrefix();

    List<Player> getNearbyPlayersOfFactionToLocation(final Location location, final Beruf.Berufe faction);

    void blockEmergencyCalls(final OfflinePlayer targetPlayer, Beruf.Berufe faction);

    void unblockEmergencyCalls(final OfflinePlayer targetPlayer, Beruf.Berufe faction);

    boolean isBlocked(final OfflinePlayer targetPlayer,  Beruf.Berufe faction);


}
