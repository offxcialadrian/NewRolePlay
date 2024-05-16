package de.newrp.features.emergencycall.commands;

import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CancelEmergencyCallCommand implements CommandExecutor {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final Player player = (Player) commandSender;

        Optional<EmergencyCall> emergencyCallOptional = this.emergencyCallService.getEmergencyCallByPlayer(player, null);
        if(!emergencyCallOptional.isPresent()) {
            player.sendMessage(this.emergencyCallService.getPrefix() + "Du hast keinen aktiven Notruf");
            return false;
        }

        final EmergencyCall emergencyCall = emergencyCallOptional.get();
        if(emergencyCall.acceptEmergencyCallMetadata() != null) {
            emergencyCall.acceptEmergencyCallMetadata().player().sendMessage(this.emergencyCallService.getPrefix() + "Der Notruf, welchen du aktiv bearbeitest wurde abgebrochen..");
            return false;
        }

        this.emergencyCallService.dropEmergencyCall(emergencyCall);
        player.sendMessage(this.emergencyCallService.getPrefix() + "Du hast deinen Notruf abgebrochen");


        return false;
    }
}
