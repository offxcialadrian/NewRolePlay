package de.newrp.features.emergencycall.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.swing.text.html.Option;
import java.util.Optional;

public class EmergencyCallQuitListener implements Listener {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);


    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Optional<EmergencyCall> emergencyCallOptional = this.emergencyCallService.getEmergencyCallByPlayer(player, null);
        emergencyCallOptional.ifPresent(emergencyCallService::dropEmergencyCall);

        final Optional<EmergencyCall> activeEmergencyCall = this.emergencyCallService.getAcceptedEmergencyCallByFactionMember(player);
        emergencyCallOptional.ifPresent(emergencyCall -> emergencyCallService.requeueAcceptedEmergencyCall(player, emergencyCall));
    }
}
