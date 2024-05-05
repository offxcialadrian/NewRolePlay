package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.Notruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Set;

public class EmergencyCallsCommand implements CommandExecutor {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe playerFaction = Beruf.getBeruf(player);

        if(playerFaction == null) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(playerFaction != Beruf.Berufe.POLICE && playerFaction != Beruf.Berufe.RETTUNGSDIENST) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final Set<EmergencyCall> emergencyCalls = this.emergencyCallService.getAllOpenEmergencyCallsForFaction(playerFaction);
        if(emergencyCalls.isEmpty()) {
            player.sendMessage(this.emergencyCallService.getPrefix() + "Es gibt aktuell keine offenen Notrufe");
            return false;
        }

        player.sendMessage(this.emergencyCallService.getPrefix() + "Folgende Notrufe sind offen:");
        for (final EmergencyCall emergencyCall : emergencyCalls) {
            player.sendMessage(this.emergencyCallService.getPrefix() + "§8× §6" + Script.getName(emergencyCall.sender()) + " (" + (int) player.getLocation().distance(emergencyCall.location()) + "m)");
        }

        return false;
    }
}
